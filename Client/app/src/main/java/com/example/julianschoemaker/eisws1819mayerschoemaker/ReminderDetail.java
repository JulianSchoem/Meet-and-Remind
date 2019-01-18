package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReminderDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btn_info;
    private ListView listview_labelList;
    private FloatingActionButton fabCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        mToolbar = findViewById(R.id.toolbarDetailR);
        String neu = getIntent().getExtras().getString("ERSTELLEN");
        if (neu != null){
            mToolbar.setTitle(neu);
        }
        else mToolbar.setTitle("Erinnerung bearbeiten");

        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * On Click Listeners
         */
        btn_info = findViewById(R.id.btn_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ReminderDetail.this);
                dialog.setCancelable(false);
                dialog.setTitle("Thema einer Erinnerung");
                dialog.setMessage("Du kannst Erinnerungen ein Thema zuweisen. " +
                        "Mit diesen Themen kann dir das System Themenvorschläge liefern, " +
                        "die dir im Gespräch weiterhelfen können. " +
                        "Der Themenvorschlag erscheint als blaues Symbol bei der Kontaktliste." );
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "cancel".
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        fabCheck = findViewById(R.id.fabCheck);
        fabCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save Reminder (POST)
                onBackPressed();
            }
        });

        listview_labelList = findViewById(R.id.listview_labelList);
        listview_labelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView img_check = view.findViewById(R.id.img_check);
                img_check.setBackgroundResource(R.drawable.list_activated_background);

                //TODO save Topic and POST to Reminder (needed for Suggestion)
                }
        });

        /**
         * GET TOPICS
         */
        OkHttpClient client = new OkHttpClient();

        String url = "https://eisws1819mayerschoemaker.herokuapp.com/topics";
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

                    try {
                        Iterator<String> keys = jObject.keys();
                        int i = 0;

                        while(keys.hasNext() ) {
                            String key = keys.next();
                            if (jObject.get(key) instanceof JSONObject) {
                                array[i] = jObject.getJSONObject(key).getString("name");
                            }
                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ReminderDetail.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listview_labelList.setAdapter(new AdapterLabelList(getApplicationContext(), array));
                        }
                    });
                }
            }
        });
    }

}
