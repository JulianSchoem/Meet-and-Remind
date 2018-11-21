package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class contact extends AppCompatActivity {

    public static String BlueID = "BTID";

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    TextView textview_bluetoothID;
    Button btnConnect;

    BluetoothDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        textview_bluetoothID = findViewById(R.id.textview_bluetoothID);

        BlueID = getIntent().getExtras().getString(BlueID);

        textview_bluetoothID.setText(BlueID);

        btnConnect = findViewById(R.id.buttonConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Bluetooth Device herausfinden! Toast funktioniert generell, Methode startClient ohne device nicht zu testen...
                Toast toast = Toast.makeText(getApplicationContext(), "Waiting for: " + device.getName() + " with ID: " + device.getAddress(), Toast.LENGTH_LONG);
                toast.show();

                //mBluetoothConnection.startClient(device, MY_UUID_INSECURE);
            }
        });
    }

}
