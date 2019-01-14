package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


public class ContactList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private FloatingActionButton fbtn_AddContact;

    private ListView listview_contactList;
    FrameLayout touch_area_chat;

    //TODO Bei Match Themenvorschlag blaues Icon!

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarList);
        mToolbar.setTitle(getString(R.string.app_name));
        fbtn_AddContact = findViewById(R.id.fab);
        fbtn_AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactList.this, AddContact.class);
                startActivity(intent);
            }
        });

        listview_contactList = (ListView) findViewById(R.id.listview_contactlist);

        listview_contactList.setAdapter(new AdapterContactList(this, new String[]{"Michael",
                "Ralf", "Mareike", "Petra"}));


        listview_contactList.setOnItemClickListener(ContactList.this);

        //Notify Service
        MessageReceiver receiver = new MessageReceiver(new Message());
        Intent serviceIntent = new Intent(this, NotifyService.class);
        serviceIntent.putExtra("time", 10);
        serviceIntent.putExtra("receiver", receiver);
        startService(serviceIntent);

        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    public class Message {

        public void displayMessage(int resultCode, Bundle resultData) {

            String message = resultData.getString("message");
            Toast.makeText(ContactList.this, resultCode + " " + message, Toast.LENGTH_SHORT).show();
        }
    }


}
