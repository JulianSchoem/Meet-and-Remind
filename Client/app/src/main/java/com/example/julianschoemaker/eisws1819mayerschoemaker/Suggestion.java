package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Suggestion extends AppCompatActivity {

    Toolbar mToolbar;
    TextView webText;

    static String subscriptionKey = "781d2e7c0a2843c8ad3426b17eedf47b";
    static String host = "https://api.cognitive.microsoft.com";
    static String path = "/bing/v7.0/news/search";
    static String searchTerm = "Food";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        mToolbar = (Toolbar) findViewById(R.id.toolbarSuggestion);
        mToolbar.setTitle("Themenvorschlag");
        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webText = findViewById(R.id.textViewWeb);

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.cognitive.microsoft.com/bing/v7.0/news/search?q=Food&count=10&offset=0&mkt=de-de&safeSearch=Moderate";
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
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();

                    Suggestion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webText.setText(jsonData);
                        }
                    });
                }
            }
        });



    }


}



