package com.pockwester.forge.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pockwester.forge.models.CourseInstance;
import com.pockwester.forge.providers.ForgeProvider;
import com.pockwester.forge.utils.PWApi;
import com.pockwester.forge.utils.PWApiTask;
import com.pockwester.forge.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zack on 11/4/13.
 */
public class ForgeDetailActivity extends OptionsMenuActivity implements PWApi {
    String student_id;
    String instance_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forge_detail);

        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        instance_id= getIntent().getStringExtra("instance_id");

        Set<String> looking_ids = prefs.getStringSet("looking_ids", new HashSet<String>());

        if (looking_ids.contains(instance_id)) {
            findViewById(R.id.looking_button).setVisibility(View.GONE);
        }

        String[] projection = new String[] {  CourseInstance.ROW_TITLE, CourseInstance.ROW_SUBJECT_NO };
        String where = CourseInstance.ROW_COURSE_INSTANCE_ID + "=" + instance_id;
        Cursor cursor = getContentResolver().
                query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);
        if (cursor.moveToFirst()) {
            ((TextView) findViewById(R.id.catalog_no)).setText(cursor.getString(1));
            ((TextView) findViewById(R.id.course_title)).setText(cursor.getString(0));
        }
       cursor.close();

    }

    public void lookingForGroup(View view) {
        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        // updating looking for group ids in shared prefs
        Set<String> looking_ids = new HashSet<String>(prefs.getStringSet("looking_ids", new HashSet<String>()));
        looking_ids.add(instance_id);
        prefs.edit().putStringSet("looking_ids", looking_ids).commit();

        // notify api
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("student_id", student_id));
        args.add(new BasicNameValuePair("instance_id", instance_id));
        args.add(new BasicNameValuePair("flag", "y"));

        new PWApiTask(TASKS.LOOKING_FOR_GROUP, args, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
                break;
        }

        return true;
    }

    @Override
    public void hasResult(TASKS task, String result) {
        startActivity( new Intent(this, MainActivity.class) );
    }
}
