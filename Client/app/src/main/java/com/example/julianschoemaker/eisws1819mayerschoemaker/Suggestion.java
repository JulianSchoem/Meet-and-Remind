package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;



public class Suggestion extends AppCompatActivity {

    Toolbar mToolbar;
    TextView webText;
    BingWebSearch bing;
    ProgressBar progress;



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

        final String[] jsonString = new String[1];
        final String[] neu = new String[1];

        @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, Void> task = new AsyncTask <Void, Void, Void> () {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = findViewById(R.id.progressBar);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                bing = new BingWebSearch();
                jsonString[0] = bing.getJson();
                try {
                    JSONObject jObject = new JSONObject(jsonString[0]);
                    JSONArray jArray = new JSONArray(jObject.getString("value"));
                    neu[0] = jArray.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                webText.setText(neu[0]);
                progress.setVisibility(View.GONE);
            }
        };

        task.execute();




    }


}


class BingWebSearch {

    static String subscriptionKey = "781d2e7c0a2843c8ad3426b17eedf47b";

    /*
     * If you encounter unexpected authorization errors, double-check these values
     * against the endpoint for your Bing Web search instance in your Azure
     * dashboard.
     */
    static String host = "https://api.cognitive.microsoft.com";
    static String path = "/bing/v7.0/news/search";
    static String searchTerm = "Food Trends";


    public String getJson(){

        if (subscriptionKey.length() != 32) {
            System.out.println("Invalid Bing Search API subscription key!");
            System.out.println("Please paste yours into the source code.");
            System.exit(1);
            return "Error1";

        }

        // Call the SearchWeb method and print the response.
        try {
            SearchResults result = SearchWeb(searchTerm);
            String json = prettify(result.jsonResponse);
            System.out.println(json);
            return json;


        }
        catch (Exception e) {
            e.printStackTrace(System.out);
            return "Error2";

        }
    }



    public static SearchResults SearchWeb (String searchQuery) throws Exception {
        // construct the search request URL (in the form of URL + query string)
        URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8"));
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // receive JSON body
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();
        // construct result object for return
        SearchResults results = new SearchResults(new HashMap<String, String>(), response);

        // extract Bing-related HTTP headers
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")) {
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        stream.close();
        return results;

    }


    // pretty-printer for JSON; uses GSON parser to parse and re-serialize
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }


}

class SearchResults{
    HashMap<String, String> relevantHeaders;
    String jsonResponse;
    SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}




