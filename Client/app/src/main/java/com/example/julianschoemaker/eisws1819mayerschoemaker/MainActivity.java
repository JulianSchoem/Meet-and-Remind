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
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOGTAG = "MainActivity";

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    
    BluetoothAdapter bluetoothAdapter;
    Button btn_bluetoothOnOff;
    Button btn_bluetoothDiscovery;
    Button btn_findDevices;
    Button btn_connect;

    public ArrayList<BluetoothDevice> bluetoothDevicesList = new ArrayList<>();
    public BluetoothDevicesAdapter bluetoothDevicesAdapter;

    BluetoothDevice device;

    ListView listview_newDevices;
    ListView listview_bondedDevices;

    private final BroadcastReceiver broadcastReceiverDevices = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice devicelist = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothDevicesList.add(devicelist);

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
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                        Log.d(LOGTAG, "BroadcastReceiver: BOND_BONDED.");
                        Intent activityIntent = new Intent(MainActivity.this, contact.class);
                        String bluetoothID = device.getAddress();
                        activityIntent.putExtra("BTID", bluetoothID);
                        MainActivity.this.startActivity(activityIntent);
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
        unregisterReceiver(broadcastReceiverDevices);
        unregisterReceiver(broadcastReceiverBond);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_bluetoothOnOff = findViewById(R.id.btn_bluetoothOnOff);
        btn_bluetoothDiscovery = findViewById(R.id.btn_bluetoothDiscovery);
        btn_findDevices = findViewById(R.id.btn_findDevices);
        btn_connect = findViewById(R.id.buttonConnect);

        listview_newDevices = findViewById(R.id.listview_newDevices);
       // listview_bondedDevices = findViewById(R.id.listview_bondedDevices);
        bluetoothDevicesList = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiverBond, filter);

        mBluetoothConnection = new BluetoothConnectionService(getApplicationContext());

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
                Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
                startActivity(discoveryIntent);
            }
        });

        btn_findDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();

                    checkBluetoothPermission();

                    bluetoothAdapter.startDiscovery();

                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiverDevices, intentFilter);
                } else if (!bluetoothAdapter.isDiscovering()) {
                    checkBluetoothPermission();

                    bluetoothAdapter.startDiscovery();

                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiverDevices, intentFilter);
                }
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Bluetooth Device herausfinden! Toast funktioniert generell, Methode startClient ohne device nicht zu testen...
                Toast toast = Toast.makeText(getApplicationContext(), "Waiting for: " + device.getName() + " with ID: " + device.getAddress(), Toast.LENGTH_LONG);
                toast.show();

                mBluetoothConnection.startClient(device, MY_UUID_INSECURE);
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

        } else if ( bluetoothAdapter.isEnabled() ) {
            bluetoothAdapter.disable();
        }
    }

    private void checkBluetoothPermission() {
        int checkPermission = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        checkPermission += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (checkPermission != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        String bluetoothDeviceName = bluetoothDevicesList.get(position).getName();
        String bluetoothDeviceAddress = bluetoothDevicesList.get(position).getAddress();

        Toast toast = Toast.makeText(getApplicationContext(), "Waiting for: " + bluetoothDeviceName + " with ID: " + bluetoothDeviceAddress, Toast.LENGTH_LONG);
        toast.show();

        bluetoothDevicesList.get(position).createBond();

    }
}
