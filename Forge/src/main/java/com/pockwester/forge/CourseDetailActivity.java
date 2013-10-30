package com.pockwester.forge;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseDetailActivity extends ListActivity implements PWApi {

    private CourseInstanceAdapter adapter;
    private List<CourseInstance> instanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter and bind it to list view
        instanceList = new ArrayList<CourseInstance>();

        adapter = new CourseInstanceAdapter(this, instanceList);

        setListAdapter(adapter);

        String course_id = getIntent().getStringExtra("course_id");

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("course_id", course_id));

        new PWApiTask(TASKS.INSTANCE_SEARCH, args, this).execute();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_detail, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), CourseIndexActivity.class);
                startActivity(myIntent);
                break;
        }

        return true;
    }

    @Override
    public void hasResult(TASKS task, String result) {
        if (task == TASKS.INSTANCE_SEARCH) {
            instanceList.clear();


            for (CourseInstance instance : CourseInstance.createInstanceCollection(result)) {
                instanceList.add(instance);
            }

            adapter.notifyDataSetChanged();
        }
        else if (task == TASKS.UPDATE_COURSE) {
            startActivity(new Intent(this, CourseIndexActivity.class));
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);
        Set<String> instance_ids = new HashSet<String>(prefs.getStringSet("instance_ids", new HashSet<String>()));

        String instance_id = v.getTag().toString();

        if (!instance_ids.contains(instance_id)) {
            //update course list for student on android
            instance_ids.add(instance_id);
            prefs.edit().putStringSet("instance_ids", instance_ids).apply();
            CourseInstance.addToDB(instanceList.get(position), this);

            // inform api that course has been added
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("student_id", student_id));
            nameValuePairs.add(new BasicNameValuePair("instance_id", instance_id));
            nameValuePairs.add(new BasicNameValuePair("action", "add"));

            new PWApiTask( TASKS.UPDATE_COURSE, nameValuePairs, this ).execute();
        }
        else {
            startActivity(new Intent(this, CourseIndexActivity.class));
        }

    }
}
