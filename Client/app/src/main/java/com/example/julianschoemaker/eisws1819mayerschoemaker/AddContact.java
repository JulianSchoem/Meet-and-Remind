package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Find Bluetooth Devices and get all bonded devices.
 */
public class AddContact extends AppCompatActivity {

    private static final String LOGTAG = "AddContact";
    static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BluetoothAdapter mybluetoothAdapter;
    private Switch btn_bluetoothOnOff;
    private Switch btn_bluetoothDiscovery;
    private Button btn_findDevices;

    //List of all Bluetooth Devices nearby
    public ArrayList<BluetoothDevice> bluetoothDevicesList = new ArrayList<>();
    public BluetoothDevicesAdapter bluetoothDevicesAdapter;
    private ListView listview_newDevices;
    private BroadcastReceiver broadcastReceiverDevices;

    //List of all bonded Bluetooth Devices
    public ArrayList<BluetoothDevice> pairedDevicesList = new ArrayList<>();
    public BluetoothDevicesAdapter pairedDevicesAdapter;
    private ListView listview_bondedDevices;
    private BroadcastReceiver broadcastReceiverBond;

    //own Device
    private BluetoothDevice device;

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Neuen Kontakt hinzufügen");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_bluetoothOnOff = findViewById(R.id.btn_bluetoothOnOff);
        btn_bluetoothDiscovery = findViewById(R.id.btn_bluetoothDiscovery);
        btn_findDevices = findViewById(R.id.btn_findDevices);

        listview_newDevices = findViewById(R.id.listview_newDevices);
        listview_bondedDevices = findViewById(R.id.listview_bondedDevices);
        status = findViewById(R.id.status);

        mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mybluetoothAdapter.isEnabled()){
            btn_bluetoothOnOff.setChecked(true);
        } else {
            btn_bluetoothOnOff.setChecked(false);
        }
        if ( mybluetoothAdapter.isDiscovering()){
            btn_bluetoothDiscovery.setChecked(true);
        } else {
            btn_bluetoothDiscovery.setChecked(false);
        }

        /**
         * BroadcastReceiver ACTION FOUND
         */
        broadcastReceiverDevices = new BroadcastReceiver() {
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

        /**
         * BroadcastReceiver ACTION_BOND_STATE_CHANGED
         */
        broadcastReceiverBond = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                try {
                    if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                            Log.d(LOGTAG, "BroadcastReceiver: BOND_BONDED.");
                            Intent activityIntent = new Intent(AddContact.this, ContactDetail.class);
                            String bluetoothID = device.getAddress();
                            activityIntent.putExtra("BTID", bluetoothID);
                            AddContact.this.startActivity(activityIntent);
                        }
                    }
                } catch(NullPointerException e) {
                    System.out.print("NullPointerException Caught");
                }
            }
        };

        //Register Receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiverBond, filter);
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiverDevices, intentFilter);

        /**
         * On Click Listeners
         */

        btn_bluetoothOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bluetoothEnableDisable();
            }
        });

        btn_bluetoothDiscovery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                    Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
                    startActivity(discoveryIntent);
                }
            }
        });

        btn_findDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get bonded Devices
                if (!mybluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                } else {
                    Set<BluetoothDevice> bt = mybluetoothAdapter.getBondedDevices();
                    if (bt.size() > 0) {
                        for (BluetoothDevice device : bt) {
                            pairedDevicesList.add(device);
                        }
                    }
                    pairedDevicesAdapter = new BluetoothDevicesAdapter(getApplicationContext(), R.layout.adapter_devices_view, pairedDevicesList);
                    listview_bondedDevices.setAdapter(pairedDevicesAdapter);

                    // Discover Devices nearby
                    if (mybluetoothAdapter.isDiscovering()) {
                        mybluetoothAdapter.cancelDiscovery();
                    }
                    checkBluetoothPermission();
                    mybluetoothAdapter.startDiscovery();

                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiverDevices, intentFilter);
                }
            }
        });

        listview_newDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mybluetoothAdapter.cancelDiscovery();
                String bluetoothDeviceName = bluetoothDevicesList.get(position).getName();
                String bluetoothDeviceAddress = bluetoothDevicesList.get(position).getAddress();
                Toast toast = Toast.makeText(getApplicationContext(), "Waiting for: " + bluetoothDeviceName + " with ID: " + bluetoothDeviceAddress, Toast.LENGTH_LONG);
                toast.show();

                //Bond with New Device
                bluetoothDevicesList.get(position).createBond();
            }
        });

        listview_bondedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Add Contact
                // TODO POST REQUEST
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverDevices);
        unregisterReceiver(broadcastReceiverBond);
    }

    /**
     * Helping Methods.
     */

    public void bluetoothEnableDisable() {
        if(mybluetoothAdapter == null) {
            // Can not use BT
            Log.d(LOGTAG, "Device does not have BT");
        } else if ( !mybluetoothAdapter.isEnabled() ) {
            Intent bluetoothEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bluetoothEnableIntent);

        } else if ( mybluetoothAdapter.isEnabled() ) {
            mybluetoothAdapter.disable();
        }
    }

    private void checkBluetoothPermission() {
        int checkPermission = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        checkPermission += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (checkPermission != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }
}
