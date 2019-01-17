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
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class NotifyService extends IntentService {

    public static final String APP_NAME = "Meet And Remind";

    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");



    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    public NotifyService(){
        super("Notify Service");
    }

    public static void sendNotification(ResultReceiver receiver) {

        Bundle bundle = new Bundle();
        bundle.putString("message", "Counting done...");
        receiver.send(1234, bundle);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("abc", "Timer has started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Service läuft weiter, auch wenn App geschlossen wird.
        return START_STICKY;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onHandleIntent(Intent intent) {
        while(intent == null){
           /** //Wenn App geschlossen, alle 5 sekunden eine Benachrichtigung. Nervt, deswegen auskommentiert...
             int time = 5;

            for(int i = 0; i< time; i++){
                Log.v("abc", "(intent null) i = "+i);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){

                }
            }
            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(getApplicationContext(), ReminderDetail.class);
            // TODO which Reminder Detail? putExtra()!
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
                    .setContentTitle("Titel der Erinnerung")
                    .setContentText("Beschreibung der Erinnerung")
                    .setContentIntent(resultPendingIntent);

            nm.notify(NOTIFICATION_ID, builder.build());
            **/
        }

        while(true) {


            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            int time = intent.getIntExtra("time", 0);
            for (int i = 0; i < time; i++) {
                Log.v("abc", "(intent not null) i = " + i);

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.v("abc", "FEHLER");
                }
            }

            sendNotification(receiver);
        }
    }

}

class ServerClass extends Thread{

    private BluetoothServerSocket serverSocket;
    public BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    public ServerClass() {

        try {
            serverSocket = mybluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NotifyService.APP_NAME, NotifyService.MY_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        BluetoothSocket socket = null;

        while(socket == null){

            try {

                socket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (socket != null){
                //send receive

                break;

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
            socket = device.createRfcommSocketToServiceRecord(NotifyService.MY_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            socket.connect();
            //TODO Configure Push Notification
            //device.getName() getReminder...

        }
        catch (IOException e) {
            e.printStackTrace();

        }

    }


}
