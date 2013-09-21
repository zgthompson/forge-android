package com.pockwester.forge;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
        JSONArray coursesArray= null;

        // save time of api request
        String timeOfRequest = "" + (System.currentTimeMillis() / 1000L);

        // make request to api
        String response = postToServer(new BasicNameValuePair("apitask", "get_courses"),
                new BasicNameValuePair("last_update", getLastUpdate()));


        // make sure response was successful and covert to json
        if (response != null) {
            coursesArray = convertToArray(response);
        }
        else {
            Log.e("forge", "api request unsuccessful in DBSyncService");
            return;
        }

        // make sure conversion was successful and update db
        if (coursesArray != null) {
            addOrUpdateCourses(coursesArray);
        }
        else {
            Log.e("forge", "JSON conversion unsuccessful in DBSyncService");
            return;
        }

        // set last_update to time of request
        setLastUpdate(timeOfRequest);

        // create notification intent
        Intent resultIntent = new Intent(NOTIFICATION);
        resultIntent.putExtra(RESULT, Activity.RESULT_OK);

        // notify MainActivity of completion
        sendBroadcast(resultIntent);
    }

    private String getLastUpdate() {
        SharedPreferences prefs = getSharedPreferences("com.pockwester.forge", Context.MODE_PRIVATE);
        return prefs.getString("last_update", "0");
    }

    private void setLastUpdate(String timeOfRequest) {
        SharedPreferences prefs = getSharedPreferences("com.pockwester.forge", Context.MODE_PRIVATE);
        prefs.edit().putString("last_update", timeOfRequest).commit();
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

    private void addOrUpdateCourses(JSONArray coursesArray) {
        for (int i = 0; i < coursesArray.length(); i++) {
            try {
                addOrUpdateCourse(Course.jsonToContentValues(coursesArray.getJSONObject(i)));
            }
            catch (JSONException e) {
                Log.e("forge", "JSONException in DBSyncService.onHandleIntent", e);
            }
        }
    }


    private void addOrUpdateCourse(ContentValues courseValues) {
        long row_id = getRowId(courseValues.getAsString(Course.ROW_COURSE_ID));

        if (row_id != -1) {
            updateCourse(courseValues, row_id);
        }
        else {
            addCourse(courseValues);
        }

    }

    private void updateCourse(ContentValues courseValues, long id) {
        getContentResolver().update(ContentUris.withAppendedId(ForgeProvider.COURSE_CONTENT_URI, id),
                courseValues, null, null);
    }

    private void addCourse(ContentValues courseValues) {
        getContentResolver().insert(ForgeProvider.COURSE_CONTENT_URI, courseValues);
    }

    private long getRowId(String courseId) {
        Cursor cursor = getContentResolver().query(ForgeProvider.COURSE_CONTENT_URI,
                new String[] { Course.ROW_ID }, Course.ROW_COURSE_ID + "=" + courseId, null, null);

        long row_id;

        if (cursor.moveToFirst()) {
            row_id = cursor.getLong(0);
        }
        else {
            row_id = -1;
        }
        cursor.close();
        return row_id;
    }

}
