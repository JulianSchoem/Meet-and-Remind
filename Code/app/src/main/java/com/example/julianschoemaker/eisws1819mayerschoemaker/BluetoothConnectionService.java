package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "Meet & Remind";

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;

    public BluetoothConnectionService(Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //Runs while listening for incoming connections. Runs until connection ist accepted ( or cancelled)
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){

            BluetoothServerSocket tmp = null;

            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch(IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running");

            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFCOM server socket start...");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

                Toast toast = Toast.makeText(mContext, "Accepted Connection", Toast.LENGTH_LONG);
                toast.show();
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            /**
            if (socket != null){
                connected(socket, mmDevice);
            }

            Log.i(TAG, "END mAcceptThread");
             **/
        }

        public void cancel(){
            Log.d( TAG, "cancel: Cancelling AcceptThread. ");
            try{
                mmServerSocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. "+ e.getMessage());
            }
        }
    }



}
