package com.pockwester.forge;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zack on 9/7/13.
 */
public class DBSyncService extends IntentService {

    public static final String NOTIFICATION = "com.pockwester.forge.db_sync";
    public static final String RESULT = "result";
    static final String API_ROOT = "http://arthurwut.com/pockwester/api/";
    static final String API_KEY = "test";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DBSyncService() {
        super("DBSyncService");
    }

    // Will be called aysnchronously
    @Override
    protected void onHandleIntent(Intent intent) {

        String response;
        JSONArray coursesArray= null;


        response = postToServer(new BasicNameValuePair("apitask", "get_courses"));

        // make sure response was successful and covert to json
        if (response != null) {
            coursesArray = convertToArray(response);
        }

        // make sure conversion was successful and update db
        if (coursesArray != null) {
            addNewCourses(coursesArray);
        }

        Intent resultIntent = new Intent(NOTIFICATION);
        resultIntent.putExtra(RESULT, Activity.RESULT_OK);
        sendBroadcast(resultIntent);
    }

    private String postToServer(NameValuePair... params) {

        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>(Arrays.asList(params));
        paramsList.add(new BasicNameValuePair("apikey", API_KEY));

        try {
            // set url
            HttpPost httpPost = new HttpPost(API_ROOT);
            // set params
            httpPost.setEntity(new UrlEncodedFormEntity(paramsList));
            // fire away
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(httpPost, responseHandler);
        } catch (IOException e) {
            Log.e("forge", "IOException in DBSyncService.postToServer", e);
            return null;
        } finally {
            client.close();
        }
    }

    private JSONArray convertToArray(String response) {

        try {
            JSONObject coursesObject = new JSONObject(response);
            return coursesObject.getJSONArray("courses");
        } catch (JSONException e) {
            Log.e("forge", "JSONException in DBSyncService.convertToList", e);
            return null;
        }
    }

    private void addNewCourses(JSONArray coursesArray) {
        ContentResolver cr = getContentResolver();
        ContentValues curValues;

        for (int i = 0; i < coursesArray.length(); i++) {
            try {
                curValues = Course.jsonToContentValues(coursesArray.getJSONObject(i));
                if (curValues != null) {
                    cr.insert(ForgeProvider.COURSE_CONTENT_URI, curValues);
                }
            } catch (JSONException e) {
                Log.e("forge", "JSONException in DBSyncService.onHandleIntent", e);
            }
        }
    }

}
