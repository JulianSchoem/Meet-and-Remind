package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReminderDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Toolbar mToolbar;
    Button btn_info;

    ListView listview_labelList;

    FloatingActionButton fabCheck;
    boolean oneIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        mToolbar = findViewById(R.id.toolbarDetailR);
        mToolbar.setTitle("Erinnerung bearbeiten");
        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                        "Der Themenvorschlag erscheint als blaues Symbol bei der Kontaktliste" );
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
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
                //TODO ERINNERUNG SPEICHERN
                onBackPressed();
            }
        });

        listview_labelList = findViewById(R.id.listview_labelList);


        listview_labelList.setOnItemClickListener(ReminderDetail.this);


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

                    final String array[] = new String[21];

                    try {
                        array[0] = jObject.getJSONObject("0fW3diAbM2XGbG7QHPbT").getString("name");
                        array[1] = jObject.getJSONObject("9ijZBWDS8GdJUh0imgHg").getString("name");
                        array[2] = jObject.getJSONObject("BIixGicWrM86qQW6vELV").getString("name");
                        array[3] = jObject.getJSONObject("CqAqKNPyhliigtPnyT2B").getString("name");
                        array[4] = jObject.getJSONObject("EF0PhXVy8wvAlpVAEfL6").getString("name");
                        array[5] = jObject.getJSONObject("F5Hn7LSMNmJCdZqvvNps").getString("name");
                        array[6] = jObject.getJSONObject("FE5YajfbgoM6LbVrB8n5").getString("name");
                        array[7] = jObject.getJSONObject("FhqQys8RGIsOn65w3pML").getString("name");
                        array[8] = jObject.getJSONObject("ITVEYiMzGLgFddYeHgLO").getString("name");
                        array[9] = jObject.getJSONObject("JVNeeK7Zs8YvRApwANPd").getString("name");
                        array[10] = jObject.getJSONObject("Jphf6xYsyv7xWd8Ml97x").getString("name");
                        array[11] = jObject.getJSONObject("QIjYBZVaCDm7klkyu3OR").getString("name");
                        array[12] = jObject.getJSONObject("TRTgEQbGDG28XIJkgTRx").getString("name");
                        array[13] = jObject.getJSONObject("UtbmzHHAduDNtdZXvfA1").getString("name");
                        array[14] = jObject.getJSONObject("XkvUTjffuyL3AywF6SOa").getString("name");
                        array[15] = jObject.getJSONObject("bn5HuqmQZnYuapxP0CMb").getString("name");
                        array[16] = jObject.getJSONObject("kGQW8gPEOSroZx6zKLrk").getString("name");
                        array[17] = jObject.getJSONObject("oCYNOCsf0giwm11MXqcl").getString("name");
                        array[18] = jObject.getJSONObject("rmdxuW4mK6qE0eAD727Z").getString("name");
                        array[19] = jObject.getJSONObject("t2VZboDxLUkKnN5VhMLB").getString("name");
                        array[20] = jObject.getJSONObject("xUVGp5YPfW9UhEGUK8jg").getString("name");


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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView img_check = view.findViewById(R.id.img_check);

        if (img_check.getVisibility() == View.VISIBLE) {
            if ( oneIsChecked == true) {
                img_check.setVisibility(View.INVISIBLE);
                oneIsChecked = false;
            }
        } else {
            if ( oneIsChecked == false) {
                img_check.setVisibility(View.VISIBLE);
                oneIsChecked = true;
            }
        }

        //TODO Label für Themenvorschlag beim Speichern übergeben
        // TODO BUG: wenn Beschreibung länger, in Liste 2 Häkchen...

    }
}
