package com.example.lenovo.savemonk;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String RESPONSE_URL = "http://server.savemonk.com/GetStoreCatWiseMainServlet?category=Fashion";
    ArrayList<String> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // text=(TextView) findViewById(R.id.text);
        arrayList=new ArrayList<>();



        RequestAsyncTask task=new RequestAsyncTask();
        task.execute();

           }

    private void updateUi(){


        ListView listView=(ListView) findViewById(R.id.content);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(adapter);
    }



    private class RequestAsyncTask extends AsyncTask<URL,Void,JSONArray> {

        @Override
        protected JSONArray doInBackground(URL... params) {
            URL url = createUrl(RESPONSE_URL);

            JSONArray jsonResponse=null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }



          return  jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONArray offer) {
           if (offer == null) {
                return;
            }
            try {
                extractStoreFromJson(offer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //updateUi();
        }


    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("Error with creating URL", exception.toString());
            return null;
        }
        return url;
    }

    private JSONArray makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        JSONArray offerList=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String result=readFromStream(inputStream);
             offerList=new JSONArray(result);
        } catch (IOException e) {
            Log.e("Error",e.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return offerList;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private void extractStoreFromJson(JSONArray offerList) throws JSONException {
        try{
          //  JSONArray offerList=new JSONArray(jsonResponse);
            String store="";
            for(int i=0;i<offerList.length();i++){
                JSONObject offerObject= offerList.getJSONObject(i);
                store=offerObject.getString("store_name").toUpperCase();
                arrayList.add(store);

            }
            updateUi();
            return;
        }
    catch (JSONException e){
        Log.e("Error parsing json respons",e.toString());
    }
    return;
    }
}


