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

    TextView textview_bluetoothID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        textview_bluetoothID = findViewById(R.id.textview_bluetoothID);

        BlueID = getIntent().getExtras().getString(BlueID);

        textview_bluetoothID.setText(BlueID);


    }

}
