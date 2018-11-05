package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOGTAG = "MainActivity";
    
    BluetoothAdapter bluetoothAdapter;
    Button btn_bluetoothOnOff;
    Button btn_bluetoothDiscovery;
    Button btn_findDevices;

    public ArrayList<BluetoothDevice> bluetoothDevicesList = new ArrayList<>();
    public BluetoothDevicesAdapter bluetoothDevicesAdapter;
    ListView listview_newDevices;

    private final BroadcastReceiver broadcastReceiverState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try{
                if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED) ) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(LOGTAG, "State off");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(LOGTAG, "State turning off");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(LOGTAG, "State on");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(LOGTAG, "State turning on");
                            break;
                    }
                }
            } catch(NullPointerException e) {
                System.out.print("NullPointerException Caught");
            }
        }
    };

    private final BroadcastReceiver broadcastReceiverDiscovery = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                if(action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED) ) {
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);

                    switch (mode) {
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            Log.d(LOGTAG, "Discovery enabled");
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            Log.d(LOGTAG, "Discovery disabled: able to receive connections");
                            break;
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Log.d(LOGTAG, "Discovery disabled: Not able to receive connecttions");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Log.d(LOGTAG, "Connecting...");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            Log.d(LOGTAG, "Connected!");
                            break;

                    }
                }
            } catch(NullPointerException e) {
                System.out.print("NullPointerException Caught");
            }
        }
    };

    private final BroadcastReceiver broadcastReceiverDevices = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevicesList.add(device);

                    bluetoothDevicesAdapter = new BluetoothDevicesAdapter(context, R.layout.adapter_devices_view, bluetoothDevicesList);
                    listview_newDevices.setAdapter(bluetoothDevicesAdapter);
                }
            } catch(NullPointerException e) {
                System.out.print("NullPointerException Caught");
            }
        }
    };

    private final BroadcastReceiver broadcastReceiverBond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                        Log.d(LOGTAG, "BroadcastReceiver: BOND_BONDED.");
                        Intent activityIntent = new Intent(MainActivity.this, contact.class);
                        String bluetoothID = device.getAddress();
                        activityIntent.putExtra("BTID", bluetoothID);
                        MainActivity.this.startActivity(activityIntent);
                    }
                    if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                        Log.d(LOGTAG, "BroadcastReceiver: BOND_BONDING.");
                    }
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        Log.d(LOGTAG, "BroadcastReceiver: BOND_NONE.");
                    }
                }
            } catch(NullPointerException e) {
                System.out.print("NullPointerException Caught");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiverState);
        unregisterReceiver(broadcastReceiverDevices);
        unregisterReceiver(broadcastReceiverDiscovery);
        unregisterReceiver(broadcastReceiverBond);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_bluetoothOnOff = findViewById(R.id.btn_bluetoothOnOff);
        btn_bluetoothDiscovery = findViewById(R.id.btn_bluetoothDiscovery);
        btn_findDevices = findViewById(R.id.btn_findDevices);
        listview_newDevices = findViewById(R.id.listview_newDevices);
        bluetoothDevicesList = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiverBond, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listview_newDevices.setOnItemClickListener(MainActivity.this);

        btn_bluetoothOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothEnableDisable();
            }
        });

        btn_bluetoothDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "Discovery on");

                Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
                startActivity(discoveryIntent);

                IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(broadcastReceiverDiscovery, intentFilter);
            }
        });

        btn_findDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "Unpaired Devices");

                if(bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                    Log.d(LOGTAG, "Cancel discovery");

                    permissionCheck();

                    bluetoothAdapter.startDiscovery();
                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiverDevices, intentFilter);
                } else if (!bluetoothAdapter.isDiscovering()) {

                    permissionCheck();

                    bluetoothAdapter.startDiscovery();
                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiverDevices, intentFilter);
                }
            }
        });
    }

    public void bluetoothEnableDisable() {
        if(bluetoothAdapter == null) {
            // Can not use BT
            Log.d(LOGTAG, "Device does not have BT");
        } else if ( !bluetoothAdapter.isEnabled() ) {
            Intent bluetoothEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bluetoothEnableIntent);

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiverState, bluetoothIntent);
        } else if ( bluetoothAdapter.isEnabled() ) {
            bluetoothAdapter.disable();

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiverState, bluetoothIntent);
        }
    }

    public void permissionCheck() {
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {

            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        String bluetoothDeviceName = bluetoothDevicesList.get(position).getName();
        String bluetoothDeviceAddress = bluetoothDevicesList.get(position).getAddress();

        Log.d(LOGTAG, "clicked a device: " + bluetoothDeviceName + " with ID: " + bluetoothDeviceAddress);

        Toast toast = Toast.makeText(getApplicationContext(), "device: " + bluetoothDeviceName + " with ID: " + bluetoothDeviceAddress, Toast.LENGTH_LONG);
        toast.show();

        bluetoothDevicesList.get(position).createBond();

    }
}
