package com.pockwester.forge.utils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.pockwester.forge.utils.PWApi;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * Created by AW on 10/2/13.
 * Will execute a PWApi task with the provided post data.
 *
 * Will return the result of the task with the hasResult() method of the PWApi interface
 */
public class PWApiTask extends AsyncTask<String, String, String> {

    // API Params
    public static final String API_ROOT = "http://arthurwut.com/pockwester/api/";
    public static final String API_KEY = "test";

    // Variables for executing task
    private List<NameValuePair> data;
    private PWApi obj;
    private PWApi.TASKS task;

    // Constructor
    // Requires that the callback object be a implement the PWApi interface for callback information
    public PWApiTask( PWApi.TASKS task, List<NameValuePair> data, PWApi obj )
    {
        this.task = task;
        this.data = data;
        this.obj = obj;
    }

    // Process the API request
    protected String doInBackground(String... urls) {

        // Code to connect to PWApi and auth user
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpPost httpPost = new HttpPost(API_ROOT);

        // Add the api key to allow processing
        data.add(new BasicNameValuePair("apikey", API_KEY));
        data.add(new BasicNameValuePair("apitask", task.toString() ));

        // Connect to the api
        try {

            // Set the post data
            httpPost.setEntity( new UrlEncodedFormEntity( data ));

            // Fire away
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(httpPost, responseHandler);
        } catch (IOException e) {
            Log.e("forge", "IOException in PWApiTask.doInBackground", e);
            return "Could not connect to API";
        } finally {
            client.close();
        }
    }

    // Do the callback on the callback object
    protected void onPostExecute(String result) {
        obj.hasResult( task, result );
    }
}