package com.pockwester.forge.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.pockwester.forge.R;
import com.pockwester.forge.adapters.CourseAdapter;
import com.pockwester.forge.models.Course;
import com.pockwester.forge.models.CourseInstance;
import com.pockwester.forge.providers.ForgeProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

public class MainActivity extends OptionsMenuActivity {


    private List<Course> courseList;
    private MergeAdapter mergeAdapter;
    private CourseAdapter courseAdapter;
    String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.main_list);
        LayoutInflater inflater = getLayoutInflater();

        mergeAdapter = new MergeAdapter();

        TextView textView = (TextView) inflater.inflate(R.layout.list_header, null);
        textView.setText("Upcoming study groups");
        mergeAdapter.addView(textView);



        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        Set<String> groupSet = prefs.getStringSet("group_ids", null);
        Set<String> courseSet = prefs.getStringSet("instance_ids", null);

        if (groupSet != null) {
            populateGroupList(groupSet);
        }
        else {
            textView = (TextView) inflater.inflate(R.layout.empty_list, null);
            textView.setText("No upcoming study groups");
            mergeAdapter.addView(textView);
        }

        textView = (TextView) inflater.inflate(R.layout.list_header, null);
        textView.setText("Find a group");
        mergeAdapter.addView(textView);

        if (courseSet != null) {
            courseList = new ArrayList<Course>();
            populateCourseList(courseSet);
            courseAdapter = new CourseAdapter(this, courseList);
            mergeAdapter.addAdapter(courseAdapter);
        }
        else {
            textView = (TextView) inflater.inflate(R.layout.empty_list, null);
            textView.setText("You have not added any courses");
            mergeAdapter.addView(textView);
        }

        listView.setAdapter(mergeAdapter);
    }

    private void populateCourseList(Set<String> courseSet) {
        String[] projection = new String[] {  CourseInstance.ROW_TITLE, CourseInstance.ROW_CATALOG_NAME };

        for (String instance : courseSet) {
            String where = CourseInstance.ROW_COURSE_INSTANCE_ID + "=" + instance;
            Cursor cursor = getContentResolver().
                    query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);
            if (cursor.moveToFirst()) {
                courseList.add(new Course(cursor.getString(0), cursor.getString(1)));
            }
           cursor.close();
        }
    }

    private void populateGroupList(Set<String> groupSet) {

    }
}
