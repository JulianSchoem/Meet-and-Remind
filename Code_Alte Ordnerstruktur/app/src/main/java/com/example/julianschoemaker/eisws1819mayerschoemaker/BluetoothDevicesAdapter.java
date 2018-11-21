package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> devices;
    private int viewResourceID;


    public BluetoothDevicesAdapter(Context context, int resourceID, ArrayList<BluetoothDevice> devices) {
        super(context, resourceID, devices);
        this.devices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceID = resourceID;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceID, null);



        final BluetoothDevice device = devices.get(position);

        if (device != null) {
            final TextView deviceName = convertView.findViewById(R.id.deviceName);
            final TextView deviceAddress = convertView.findViewById(R.id.deviceAddress);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }

            if (deviceAddress != null) {
                deviceAddress.setText(device.getAddress());
            }


        }

        return convertView;
    }



}
