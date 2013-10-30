package com.pockwester.forge;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseIndexActivity extends Activity implements PWApi {

    private List<CourseInstance> instanceList;
    private CourseInstanceAdapter adapter;
    String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        ListView listView = (ListView) findViewById(R.id.section_list);

        instanceList = new ArrayList<CourseInstance>();
        adapter = new CourseInstanceAdapter(this, instanceList);

        listView.setAdapter(adapter);

        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        SharedPreferences prefs = getSharedPreferences(student_id, 0);
        Set<String> instanceSet = new HashSet<String>(prefs.getStringSet("instance_ids", new HashSet<String>()));

        if (!instanceSet.isEmpty()) {
            instanceList.clear();
            String[] projection = new String[] { CourseInstance.ROW_COURSE_INSTANCE_ID,
                    CourseInstance.ROW_CATALOG_NAME, CourseInstance.ROW_TITLE,
                    CourseInstance.ROW_SECTION_ID, CourseInstance.ROW_LOCATION,
                    CourseInstance.ROW_COMPONENT, CourseInstance.ROW_TIME, CourseInstance.ROW_DAY };

            for (String instance : instanceSet) {
                String where = CourseInstance.ROW_COURSE_INSTANCE_ID + "=" + instance;
                Cursor instanceCursor = getContentResolver().
                        query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);
                instanceList.add(new CourseInstance(instanceCursor));
            }

            adapter.notifyDataSetChanged();

            /*
            // Delete '[', ']' and ' ' from string
            String instance_ids = instanceSet.toString().replaceAll("[\\[\\s\\]]","");

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("instance_ids", instance_ids));


            new PWApiTask(TASKS.INSTANCE_SEARCH, args, this).execute();
            */
        }

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences prefs = getSharedPreferences(student_id, 0);
                Set<String> instanceSet = new HashSet<String>(prefs.getStringSet("instance_ids", new HashSet<String>()));

                String instance_id = view.getTag().toString();

                // update shared prefs
                instanceSet.remove(instance_id);
                prefs.edit().putStringSet("instance_ids", instanceSet).apply();

                // update list view
                instanceList.remove(position);
                adapter.notifyDataSetChanged();

                // inform api
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("student_id", student_id));
                nameValuePairs.add(new BasicNameValuePair("instance_id", instance_id));
                nameValuePairs.add(new BasicNameValuePair("action", "remove"));

                new PWApiTask( TASKS.UPDATE_COURSE, nameValuePairs, CourseIndexActivity.this ).execute();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_index, menu);
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

    public void refreshCourses(MenuItem item) {
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("student_id", student_id));

        new PWApiTask(TASKS.INSTANCE_SEARCH, args, this).execute();
    }

    public void openSearchCourse(View v) {
        Intent intent = new Intent(this, SearchCourseActivity.class);
        startActivity(intent);
    }


    @Override
    public void hasResult(TASKS task, String result) {
        if (task == TASKS.INSTANCE_SEARCH) {

            // clear list view
            instanceList.clear();

            // prepare new set for shared prefs instance_ids
            Set<String> newInstanceSet = new HashSet<String>();

            // create course instance objects
            for (CourseInstance instance : CourseInstance.createInstanceCollection(result)) {

                // add for list view
                instanceList.add(instance);

                // add for shared prefs
                newInstanceSet.add(instance.getId());

                // add for db
                CourseInstance.addToDB(instance, this);
            }

            // update list view display
            adapter.notifyDataSetChanged();

            // update shared prefs
            getSharedPreferences(student_id, 0).edit().putStringSet("instance_ids", newInstanceSet).commit();
        }
    }
}
