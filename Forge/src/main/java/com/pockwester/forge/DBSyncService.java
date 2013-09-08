package com.pockwester.forge;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
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
        List<List<String>> dbList= null;


        response = postToServer(new BasicNameValuePair("apitask", "get_classes"));

        // make sure response was successful and covert to json
        if (response != null) {
            dbList = convertToList(response);
        }

        // make sure conversion was successful and update db
        if (dbList != null) {
            CoursesDBAdapter db = new CoursesDBAdapter(this).open();
            for (List<String> rowList :dbList) {
                addCourseToDB(db, rowList);
            }
            db.close();
        }

        Intent resultIntent = new Intent(NOTIFICATION);
        resultIntent.putExtra(RESULT, Activity.RESULT_OK);
        sendBroadcast(resultIntent);
    }

    private String postToServer(NameValuePair... params) {

        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>(Arrays.asList(params));
        paramsList.add(new BasicNameValuePair("apikey", Constants.API_KEY));

        try {
            // set url
            HttpPost httpPost = new HttpPost(Constants.API_ROOT);
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

    private List<List<String>> convertToList(String response) {

        try {

            List<List<String>> dbList = new ArrayList<List<String>>();
            JSONArray dbArray = new JSONArray(response);
            int db_len = dbArray.length();
            // iterate over every row in db
            for (int i = 0; i < db_len; i++) {

                List<String> rowList = new ArrayList<String>();
                JSONArray rowArray = (JSONArray) dbArray.get(i);
                int row_len = rowArray.length();
                // iterate over every item in a row
                for (int j = 0; j < row_len; j++) {
                    rowList.add(rowArray.getString(j));
                }
                dbList.add(rowList);
            }
            return dbList;
        } catch (JSONException e) {
            Log.e("forge", "JSONException in DBSyncService.convertToList", e);
            return null;
        }

    }

    private void addCourseToDB(CoursesDBAdapter db, List<String> rowList) {
        // this is very ugly, need to reformat JSON response from server to be more descriptive
        long id = db.createCourse(rowList.get(0), rowList.get(1) + rowList.get(2), rowList.get(6), rowList.get(3),
                rowList.get(5), rowList.get(4), rowList.get(7) + " " + rowList.get(8), rowList.get(9),
                rowList.get(10));
        Log.d("forge", "_id: " + id);

    }

}
