package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;


public class Suggestion extends AppCompatActivity {

    Toolbar mToolbar;
    TextView textViewJSON;

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

        textViewJSON = findViewById(R.id.textViewJSON);

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
                    final String myResponse = response.body().string();
                    Suggestion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewJSON.setText(myResponse);
                        }
                    });
                }
            }
        });

    }
}


