package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Suggestion as Web View
 */
public class Suggestion extends AppCompatActivity {

    private Toolbar mToolbar;
    private BingWebSearch bing;
    private ProgressBar progress;
    private WebView webView;

    //GET Topic Match from Heroku Server
    private String searchTerm = ContactList.matchedTopic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        mToolbar = findViewById(R.id.toolbarSuggestion);
        mToolbar.setTitle("Themenvorschlag");
        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * GET BING NEWS API URL
         */

        //arrays for JSON Requests
        final String[] jsonString = new String[1];
        final String[] array = new String[1];
        final String[] url = new String[1];

        //AsyncTask needed, because of long running GET Request and loading WebView
        @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, Void> task = new AsyncTask <Void, Void, Void> () {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = findViewById(R.id.progressBar);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                bing = new BingWebSearch();
                jsonString[0] = bing.getJson(searchTerm);
                try {
                    JSONObject jObject = new JSONObject(jsonString[0]);
                    JSONArray jArray = new JSONArray(jObject.getString("value"));
                    array[0] = jArray.getString(0);
                    JSONObject jObject2 = new JSONObject(array[0]);
                    url[0] = jObject2.getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                webView = findViewById(R.id.webView);
                webView.getSettings().getJavaScriptEnabled();
                webView.getSettings().setDomStorageEnabled(true);
                webView.loadUrl(url[0]);

                progress.setVisibility(View.GONE);
            }
        };

        task.execute();
    }
}





