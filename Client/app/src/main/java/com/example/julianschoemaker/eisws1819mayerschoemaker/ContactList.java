package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactList extends AppCompatActivity {

    private FloatingActionButton fbtn_AddContact;

    private ListView listview_contactList;
    private FrameLayout touch_area_chat;
    private ProgressBar progress;

    public static String matchedTopic;

    //TODO GET REQUEST for Topic Match
    //TODO If there is a Match, change Icon color to Blue

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_contact_list);

        /**
         * Setup for Client side Application Logic
         */
        // Setup Server for Bluetooth Connection
        ServerClass serverClass = new ServerClass();
        serverClass.start();
        //BluetoothConnectionService
        Intent serviceIntent = new Intent(this, BluetoothConnectionService.class);
        startService(serviceIntent);

        Toolbar mToolbar = findViewById(R.id.toolbarList);
        mToolbar.setTitle(getString(R.string.app_name));
        fbtn_AddContact = findViewById(R.id.fab);
        fbtn_AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactList.this, AddContact.class);
                startActivity(intent);
            }
        });

        progress = findViewById(R.id.progressBarContactList);
        listview_contactList = findViewById(R.id.listview_contactlist);
        listview_contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Touch Area works fine, but not on first "OnItemClick"
                //TODO Touch Area / OnImageClick works on first "OnItemClick"
                touch_area_chat = view.findViewById(R.id.touch_area_chat);
                touch_area_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent suggestionIntent = new Intent(ContactList.this, Suggestion.class);
                        startActivity(suggestionIntent);
                    }
                });
                Intent activityIntent = new Intent(ContactList.this, ContactDetail.class);
                String bluetoothID = listview_contactList.getAdapter().getItem(position).toString();
                activityIntent.putExtra("BTID", bluetoothID);
                startActivity(activityIntent);
                }
        });

        /**
         * GET REQUEST
         * All Contacts, that belongs to 0C:8F:FF:C7:92:2C
         */
        OkHttpClient client = new OkHttpClient();

        //TODO GET own Bluetooth ID
        //TODO POST own BTID
        // User's TO-Do: Add Contact
        //For Testing Purpose Dummy Data
        String url = "https://eisws1819mayerschoemaker.herokuapp.com/users/0C:8F:FF:C7:92:2C/contacts";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.v("abc", "Check Internet");

                ContactList.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ContactList.this);
                        dialog.setCancelable(false);
                        dialog.setTitle("Fehler");
                        dialog.setMessage("Überprüfe deine Internetverbindung und starte die App neu" );
                        dialog.setPositiveButton("App schließen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                        final AlertDialog alert = dialog.create();
                        alert.show();
                    }
                });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if ( response.isSuccessful())
                    {
                        final String jsonData = response.body().string();
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //get length of list
                        Iterator<String> count = jObject.keys();
                        int counter = 0;
                        while(count.hasNext()) {
                            String key = count.next();
                            try {
                                if (jObject.get(key) instanceof JSONObject) {
                                    counter++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        final String array[] = new String[counter];

                        //get Contact Bluetooth Ids
                        try {
                            Iterator<String> keys = jObject.keys();
                            int i = 0;

                            while(keys.hasNext()) {
                                String key = keys.next();
                                if (jObject.get(key) instanceof JSONObject) {
                                        array[i] = key;
                                }
                                i++;
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        ContactList.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                listview_contactList.setAdapter(new AdapterContactList(getApplicationContext(), array));
                            }
                        });
                }
            }
        });

        /**
         * GET REQUEST
         * Matched topic that belogs to 0C:8F:FF:C7:92:2C and 54:27:58:24:B2:7F
         */
        OkHttpClient client2 = new OkHttpClient();

        //TODO GET own Bluetooth ID
        //TODO POST own BTID
        // User's TO-Do: Add Contact
        //For Testing Purpose Dummy Data
        String url2 = "https://eisws1819mayerschoemaker.herokuapp.com/users/0C:8F:FF:C7:92:2C/contacts/54:27:58:24:B2:7F";
        Request request2 = new Request.Builder()
                .url(url2)
                .build();
        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if ( response.isSuccessful())
                {
                    final String jsonData = response.body().string();
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(jsonData);
                        matchedTopic = jObject.getString("topic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
