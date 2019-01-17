package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ContactList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private FloatingActionButton fbtn_AddContact;

    private ListView listview_contactList;
    FrameLayout touch_area_chat;
    ProgressBar progress;

    //TODO Bei Match Themenvorschlag blaues Icon!

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_contact_list);

        ServerClass serverClass = new ServerClass();
        serverClass.start();

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

        progress = findViewById(R.id.progressBarContactList);

        OkHttpClient client = new OkHttpClient();

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


        listview_contactList.setOnItemClickListener(ContactList.this);

        //Notify Service
        MessageReceiver receiver = new MessageReceiver(new Message());
        Intent serviceIntent = new Intent(this, NotifyService.class);
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
