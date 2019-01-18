package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private FloatingActionButton fbtn_AddReminder;
    private ListView listview_reminderList;
    private FrameLayout fl_touch_area;
    private ProgressBar progress;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        mToolbar = findViewById(R.id.toolbarDetailC);
        String blueID = getIntent().getExtras().getString("BTID");
        mToolbar.setTitle(blueID);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fbtn_AddReminder = findViewById(R.id.fabReminder);
        fbtn_AddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetail.this, ReminderDetail.class);
                intent.putExtra("ERSTELLEN", "Erinnerung erstellen");
                startActivity(intent);
            }
        });

        fbtn_AddReminder = findViewById(R.id.fabReminder);
        listview_reminderList = findViewById(R.id.listview_reminderlist);

        progress = findViewById(R.id.progressBarContactDetail);

        OkHttpClient client = new OkHttpClient();

        String url = "https://eisws1819mayerschoemaker.herokuapp.com/users/0C:8F:FF:C7:92:2C/contacts/"+blueID+"/reminder";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
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
                    final String arrayRemind[] = new String[counter];

                    try {
                        Iterator<String> keys = jObject.keys();
                        int i = 0;

                        while(keys.hasNext()) {
                            String key = keys.next();
                            if (jObject.get(key) instanceof JSONObject) {
                                array[i] = jObject.getJSONObject(key).getString("title");
                                arrayRemind[i] = jObject.getJSONObject(key).getString("description");
                            }
                            i++;
                        }
                    } catch (Exception e){

                    }


                    ContactDetail.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            listview_reminderList.setAdapter(new AdapterReminderList(getApplicationContext(), array, arrayRemind));

                        }
                    });
                }
            }
        });

        listview_reminderList.setOnItemClickListener(ContactDetail.this);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        fl_touch_area = view.findViewById(R.id.touch_area_delete);
        fl_touch_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 AlertDialog.Builder dialog = new AlertDialog.Builder(ContactDetail.this);
                 dialog.setCancelable(false);
                 dialog.setTitle("Erinnerung löschen?");
                 dialog.setMessage("Bist du Dir sicher, dass Du die Erinnerung endgültig löschen möchtest? " +
                 "Du wirst keine Benachrichtigung mehr zu dieser Erinnerung erhalten. " );
                 dialog.setPositiveButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "cancel".
                }

                })
                 .setNegativeButton("Löschen ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO Action for "Delete".
                }
                });

                 final AlertDialog alert = dialog.create();
                 alert.show();

            }
        });
        Intent activityIntent = new Intent(ContactDetail.this, ReminderDetail.class);
        String reminderName = listview_reminderList.getAdapter().getItem(position).toString();
        activityIntent.putExtra("RNAME", reminderName);
        TextView description = view.findViewById(R.id.txt_contactReminder);
        String reminderDesc = description.getText().toString();
        activityIntent.putExtra("DESC", reminderDesc);
        startActivity(activityIntent);
    }
}
