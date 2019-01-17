package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import android.os.Handler;
import android.os.Message;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.example.julianschoemaker.eisws1819mayerschoemaker.BluetoothConnectionService.STATE_CONNECTED;
import static com.example.julianschoemaker.eisws1819mayerschoemaker.BluetoothConnectionService.STATE_CONNECTING;
import static com.example.julianschoemaker.eisws1819mayerschoemaker.BluetoothConnectionService.STATE_CONNECTION_FAILED;

public class BluetoothConnectionService extends IntentService {

    public static final String APP_NAME = "Meet And Remind";
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTENING = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 4; // connection failed

    private static String state = "NONE";

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    public BluetoothConnectionService(){
        super("Notify Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("abc", "Timer has started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Service runs even if App is closed
        return START_STICKY;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onHandleIntent(Intent intent) {
        while(intent == null){
            // here Service runs if App is closed
            checkConnection();
        }
        while(true) {
            checkConnection();
        }
    }

    public void checkConnection(){
        // TODO pairedDevicesList should be in Shared Preferences for improved Code
        // TODO pairedDeviceList should be replaced with ContactList from Activity ContactList
        ArrayList<BluetoothDevice> pairedDeviceList = new ArrayList<>();
        BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bt = mybluetoothAdapter.getBondedDevices();
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                pairedDeviceList.add(device);
            }
        }

        /**
         * Client Side Application Logic
         * Runs through PairedDevicesList, starts Client, Waits (Thread sleeps),
         * if Connection was successful, send Notification.
         */
        for (int i= 0; i<pairedDeviceList.size(); i++)
        {
            try {
                Log.v("abc", "(intent not null) device = " + pairedDeviceList.get(i).getName());
                BluetoothDevice device = pairedDeviceList.get(i);
                ClientClass clientClass = new ClientClass(device);
                clientClass.start();
                Thread.sleep(15000);
                Log.v("abc", "(intent not null) state = " + state);
                if (state.equals("CONNECTED")){
                    // TODO Check if there is a Reminder to THIS Contact
                    sendNotification();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNotification(){
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), ReminderDetail.class);
        // TODO GET Reminder Details that belongs to this Contact
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure
            nChannel.enableLights(true);
            nChannel.setLightColor(Color.CYAN);
            nm.createNotificationChannel(nChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.logo_meet_remind)
                .setContentTitle("Ein Freund ist in der Nähe!")
                .setContentText("Überprüfe, ob du eine offene Erinnerung hast!")
                .setContentIntent(resultPendingIntent);

        nm.notify(NOTIFICATION_ID, builder.build());
    }

    static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case STATE_NONE:
                    state = "NONE";
                    break;
                case STATE_LISTENING:
                    state = "LISTENING";
                    break;
                case STATE_CONNECTING:
                    state = "CONNECTING";
                    break;
                case STATE_CONNECTED:
                    state = "CONNECTED";
                    break;
                case STATE_CONNECTION_FAILED:
                    state = "CONNECTION_FAILED";
                    break;
            }
            return false;
        }
    });

}

/**
 * Server for Bluetooth Connection
 * Initialized in Main Activity (ContactList)
 */
class ServerClass extends Thread{

    private BluetoothServerSocket serverSocket;
    public BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public ServerClass() {
        try {
            serverSocket = mybluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(BluetoothConnectionService.APP_NAME, BluetoothConnectionService.MY_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        BluetoothSocket socket = null;
        while(socket == null){

            try {
                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                BluetoothConnectionService.mHandler.sendMessage(message);

                socket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                BluetoothConnectionService.mHandler.sendMessage(message);
            }
        }
    }
}

class ClientClass extends Thread{

    private BluetoothDevice device;
    private BluetoothSocket socket;

    public ClientClass(BluetoothDevice device) {
        this.device = device;
        try {
            socket = device.createRfcommSocketToServiceRecord(BluetoothConnectionService.MY_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            socket.connect();
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            BluetoothConnectionService.mHandler.sendMessage(message);
        }
        catch (IOException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            BluetoothConnectionService.mHandler.sendMessage(message);

        }
    }
}
