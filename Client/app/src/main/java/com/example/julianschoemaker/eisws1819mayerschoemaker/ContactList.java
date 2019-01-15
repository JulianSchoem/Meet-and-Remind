package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
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

        private static final int NOTIFICATION_ID = 1;
        private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

        public void displayMessage(int resultCode, Bundle resultData) {

            String message = resultData.getString("message");

            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(getApplicationContext(), ReminderDetail.class);
            // TODO which Reminder Detail? putExtra()/Parameter..!
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel nChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                //Configure
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.CYAN);
                nm.createNotificationChannel(nChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.logo_meet_remind)
                    .setContentTitle(""+resultCode)
                    .setContentText(""+message)
                    .setContentIntent(resultPendingIntent);

            nm.notify(NOTIFICATION_ID, builder.build());
        }
    }


}
