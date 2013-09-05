package com.pockwester.forge;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Arrays;

public class AddClassActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Execute request for classes in the background
        new NetworkTask().execute(new BasicNameValuePair("apikey", Constants.API_KEY),
                new BasicNameValuePair("apitask", "get_classes"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_class, menu);
        return true;
    }

    private class NetworkTask extends AsyncTask<NameValuePair, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(NameValuePair... params) {

            AndroidHttpClient client = AndroidHttpClient.newInstance("Android");

            try {
                // set url
                HttpPost httpPost = new HttpPost(Constants.API_ROOT);
                // set params
                httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));
                // fire away
                return client.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                client.close();
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            //Do something with result
            if (result != null) {
                TextView textView = (TextView) findViewById(R.id.class_test);
                try {
                    textView.setText(Utility.inputStreamToString(result.getEntity().getContent()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    
}
