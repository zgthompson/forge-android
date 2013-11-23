package com.pockwester.forge.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pockwester.forge.R;
import com.pockwester.forge.models.StudyGroup;
import com.pockwester.forge.providers.ForgeProvider;
import com.pockwester.forge.utils.PWApi;
import com.pockwester.forge.utils.PWApiTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 11/21/13.
 */
public class GroupDetailActivity extends OptionsMenuActivity implements PWApi {
    String student_id;
    String group_id;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // hide keyboard unless edit text is selected
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        username = prefs.getString("username", "anonymous");

        group_id = getIntent().getStringExtra("group_id");

        String[] projection = new String[] {  StudyGroup.ROW_TITLE, StudyGroup.ROW_SUBJECT_NO, StudyGroup.ROW_TIME, StudyGroup.ROW_STUDENTS };
        String where = StudyGroup.ROW_STUDY_GROUP_ID + "=" + group_id;
        Cursor cursor = getContentResolver().
                query(ForgeProvider.STUDY_GROUP_CONTENT_URI, projection, where, null, null);
        if (cursor.moveToFirst()) {
            ((TextView) findViewById(R.id.subject_no)).setText(cursor.getString(1));
            ((TextView) findViewById(R.id.course_title)).setText(cursor.getString(0));
            ((TextView) findViewById(R.id.time)).setText(cursor.getString(2));
            ((TextView) findViewById(R.id.students)).setText(cursor.getString(3));
        }

        cursor.close();

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("group_id", group_id));

        new PWApiTask(TASKS.GET_MESSAGES, args, this).execute();
    }

    public void sendMessage(View v) {
        EditText newMessage = (EditText) findViewById(R.id.new_message);
        String message = newMessage.getText().toString();
        newMessage.setText("");

        if (!message.isEmpty()) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();

            args.add(new BasicNameValuePair("group_id", group_id));
            args.add(new BasicNameValuePair("sender_id", student_id));
            args.add(new BasicNameValuePair("message", message));

            new PWApiTask(TASKS.SEND_MESSAGE, args, this).execute();

            addMessage(username + ": " + message);
        }
    }

    @Override
    public void hasResult(TASKS task, String result) {
        if (task == TASKS.GET_MESSAGES) {
            populateMessages(result);
        }
    }

    private void addMessage(String message) {
        TextView tv = new TextView(getApplicationContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setText(message);
        ((LinearLayout) findViewById(R.id.messages)).addView(tv);
    }

    private void populateMessages(String result) {
        try {
            JSONArray array = new JSONArray(result);
            JSONObject curObject;
            for (int i = 0; i < array.length(); i++) {
                curObject = array.getJSONObject(i);
                addMessage(curObject.getString("sender") + ": " + curObject.getString("message"));
            }
        } catch (JSONException e) {
            Log.e("forge", "JSONException in GroupDetailActivity.populateMessages", e);
        }
    }
}
