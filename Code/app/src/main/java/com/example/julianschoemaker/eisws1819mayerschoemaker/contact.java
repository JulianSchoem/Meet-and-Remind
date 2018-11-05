package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class contact extends AppCompatActivity {

    public static String BTID;

    TextView textview_bluetoothID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        textview_bluetoothID = findViewById(R.id.textview_bluetoothID);
        BTID = getIntent().getExtras().getString(BTID);
        textview_bluetoothID.setText(BTID);
    }
}
