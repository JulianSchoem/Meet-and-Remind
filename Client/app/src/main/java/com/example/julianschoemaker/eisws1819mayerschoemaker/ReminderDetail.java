package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create or edit Reminder
 */
public class ReminderDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btn_info;
    private ListView listview_labelList;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private FloatingActionButton fabCheck;

    String labelText;

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

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        String editTextTitleText = getIntent().getExtras().getString("RNAME");
        String editTextDescriptionText = getIntent().getExtras().getString("DESC");
        if ( editTextTitleText != null){
            editTextTitle.setText(editTextTitleText);
        }
        if ( editTextDescriptionText != null){
            editTextDescription.setText(editTextDescriptionText);
        }

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
                /**
                 * POST Reminder
                 */

                String url = "https://eisws1819mayerschoemaker.herokuapp.com/users/0C:8F:FF:C7:92:2C/contacts/54:27:58:24:B2:7F/reminder";

                String title = editTextTitle.getText().toString();
                String desc = editTextDescription.getText().toString();

                // create your json here
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", title);
                    jsonObject.put("description", desc);
                    jsonObject.put("label", labelText);
                    jsonObject.put("priority", 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient();

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // put your json here
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                if (mToolbar.getTitle().equals("Erinnerung bearbeiten")){
                    String id = getIntent().getExtras().getString("ID");
                    url = "https://eisws1819mayerschoemaker.herokuapp.com/users/0C:8F:FF:C7:92:2C/contacts/54:27:58:24:B2:7F/reminder/"+id;
                    request = new Request.Builder()
                            .url(url)
                            .put(body)
                            .build();
                }

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String yourResponse = response.body().string();
                        if(response.isSuccessful()){

                            ReminderDetail.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReminderDetail.this, "Ok: "+yourResponse,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            ReminderDetail.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReminderDetail.this, "Not Ok: "+yourResponse,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                });

                onBackPressed();
            }
        });




        listview_labelList = findViewById(R.id.listview_labelList);
        listview_labelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView img_check = view.findViewById(R.id.img_check);
                img_check.setBackgroundResource(R.drawable.list_activated_background);
                TextView labelName = view.findViewById(R.id.txt_labelName);
                labelText = labelName.getText().toString();

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
