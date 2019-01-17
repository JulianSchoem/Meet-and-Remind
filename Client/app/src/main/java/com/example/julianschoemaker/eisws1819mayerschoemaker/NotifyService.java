package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.example.julianschoemaker.eisws1819mayerschoemaker.NotifyService.STATE_CONNECTED;
import static com.example.julianschoemaker.eisws1819mayerschoemaker.NotifyService.STATE_CONNECTING;
import static com.example.julianschoemaker.eisws1819mayerschoemaker.NotifyService.STATE_CONNECTION_FAILED;

public class NotifyService extends IntentService {

    public static final String APP_NAME = "Meet And Remind";

    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTENING = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 4;

    static String state = "NONE";


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
            // TODO Code wie unten... Wie Benachrichtigung ohne intent ( und Receiver...?)
           /** //Wenn App geschlossen, alle 5 sekunden eine Benachrichtigung.
             int time = 5;

            for(int i = 0; i< time; i++){
                Log.v("abc", "(intent null) i = "+i);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){

                }
            }
            **/
        }

        while(true) {

            // TODO Wo Server Socket erstellen? In Intent Service wäre praktisch, wenns geht..

            ResultReceiver receiver = intent.getParcelableExtra("receiver");

            ArrayList<BluetoothDevice> pairedDeviceList = new ArrayList<>();
            BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bt = mybluetoothAdapter.getBondedDevices();

            if (bt.size() > 0) {

                for (BluetoothDevice device : bt) {
                    pairedDeviceList.add(device);
                }
            }

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
                        sendNotification(receiver);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

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

                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                NotifyService.mHandler.sendMessage(message);

                socket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                NotifyService.mHandler.sendMessage(message);
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
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            NotifyService.mHandler.sendMessage(message);
            //TODO Configure Push Notification
            //device.getName() getReminder...

        }
        catch (IOException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            NotifyService.mHandler.sendMessage(message);

        }

    }


}
