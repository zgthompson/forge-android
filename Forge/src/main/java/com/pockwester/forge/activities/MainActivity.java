package com.pockwester.forge.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.pockwester.forge.R;
import com.pockwester.forge.adapters.ThreeLineAdapter;
import com.pockwester.forge.adapters.TwoLineAdapter;
import com.pockwester.forge.models.Course;
import com.pockwester.forge.models.CourseInstance;
import com.pockwester.forge.models.StudyGroup;
import com.pockwester.forge.models.ThreeLine;
import com.pockwester.forge.models.TwoLine;
import com.pockwester.forge.providers.ForgeProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends OptionsMenuActivity {

    private String student_id;
    private List<TwoLine> courseList;
    private List<ThreeLine> groupList;
    private List<TwoLine> lookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.main_list);
        LayoutInflater inflater = getLayoutInflater();

        MergeAdapter mergeAdapter = new MergeAdapter();

        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        Set<String> groupSet = prefs.getStringSet("group_ids", null);
        Set<String> courseSet = prefs.getStringSet("instance_ids", null);
        Set<String> lookingSet = prefs.getStringSet("looking_ids", new HashSet<String>());

       // Upcoming study groups for student
        TextView textView = (TextView) inflater.inflate(R.layout.list_header, null);
        textView.setText("Upcoming study groups");
        mergeAdapter.addView(textView);

        if (groupSet != null) {
            groupList = new ArrayList<ThreeLine>();
            populateGroupList(groupSet);
            ArrayAdapter<ThreeLine> groupAdapter = new ThreeLineAdapter(this, groupList);
            mergeAdapter.addAdapter(groupAdapter);
        }
        else {
            textView = (TextView) inflater.inflate(R.layout.empty_list, null);
            textView.setText("No upcoming study groups");
            mergeAdapter.addView(textView);
        }

        if (courseSet != null) {
            courseList = new ArrayList<TwoLine>();
            lookingList = new ArrayList<TwoLine>();
            populateCourseAndLookingList(courseSet, lookingSet);

            // courses student can search for group in
            if (!courseList.isEmpty()) {
                // Header
                textView = (TextView) inflater.inflate(R.layout.list_header, null);
                textView.setText("Find a group");
                mergeAdapter.addView(textView);

                // courses
                ArrayAdapter<TwoLine> courseAdapter = new TwoLineAdapter(this, courseList);
                mergeAdapter.addAdapter(courseAdapter);
            }

            // show student which courses they are currently looking for a group
            if (!lookingList.isEmpty()) {
                // Header
                textView = (TextView) inflater.inflate(R.layout.list_header, null);
                textView.setText("Currently looking");
                mergeAdapter.addView(textView);

                // courses
                ArrayAdapter<TwoLine> lookingAdapter = new TwoLineAdapter(this, lookingList);
                mergeAdapter.addAdapter(lookingAdapter);
            }
        }
        // No courses have been added
        else {
            textView = (TextView) inflater.inflate(R.layout.empty_list, null);
            textView.setText("You have not added any courses");
            mergeAdapter.addView(textView);
        }


        listView.setAdapter(mergeAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(MainActivity.this, ForgeDetailActivity.class);
                detailIntent.putExtra("instance_id", view.getTag().toString());
                startActivity(detailIntent);
            }
        });
    }

    private void populateCourseAndLookingList(Set<String> courseSet, Set<String> lookingSet) {
        String[] projection = new String[] {  CourseInstance.ROW_TITLE, CourseInstance.ROW_SUBJECT_NO};

        for (String instance : courseSet) {
            String where = CourseInstance.ROW_COURSE_INSTANCE_ID + "=" + instance;
            Cursor cursor = getContentResolver().
                    query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);
            if (cursor.moveToFirst()) {
                if (lookingSet.contains(instance)) {
                    lookingList.add(new Course(cursor.getString(0), cursor.getString(1), instance));
                }
                else {
                    courseList.add(new Course(cursor.getString(0), cursor.getString(1), instance));
                }
            }
           cursor.close();
        }
    }

    private void populateGroupList(Set<String> groupSet) {
        String[] projection = new String[] {  StudyGroup.ROW_TITLE, StudyGroup.ROW_SUBJECT_NO, StudyGroup.ROW_TIME };

        for (String group : groupSet) {
            String where = StudyGroup.ROW_STUDY_GROUP_ID + "=" + group;
            Cursor cursor = getContentResolver().
                    query(ForgeProvider.STUDY_GROUP_CONTENT_URI, projection, where, null, null);
            if (cursor.moveToFirst()) {
                groupList.add(new StudyGroup(cursor.getString(0), cursor.getString(1), group, cursor.getString(2)));
            }
           cursor.close();
        }
    }
}
