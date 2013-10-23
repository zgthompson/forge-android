package com.pockwester.forge;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        ListView listView = (ListView) findViewById(R.id.section_list);

        instanceList = new ArrayList<CourseInstance>();
        adapter = new CourseInstanceAdapter(this, instanceList);

        listView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> instanceSet = new HashSet<String>(prefs.getStringSet("instance_ids", new HashSet<String>()));

        if (!instanceSet.isEmpty()) {
            instanceList.clear();
            String[] projection = new String[] { CourseInstance.ROW_COURSE_INSTANCE_ID,
                    CourseInstance.ROW_CATALOG_NAME, CourseInstance.ROW_TITLE,
                    CourseInstance.ROW_SECTION_ID, CourseInstance.ROW_LOCATION,
                    CourseInstance.ROW_COMPONENT, CourseInstance.ROW_TIME, CourseInstance.ROW_DAY };

            for (String instance : instanceSet) {
                Log.d("forge: instance: ", instance);
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

        /*
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CourseIndexActivity.this);
                Set<String> sections = new HashSet<String>(prefs.getStringSet("sections", new HashSet<String>()));

                sections.remove(String.valueOf(id));

                if (sections.isEmpty()) {
                    sections = null;
                    findViewById(R.id.section_list).setVisibility(View.GONE);
                }

                prefs.edit().putStringSet("sections", sections).apply();

                getLoaderManager().restartLoader(0, null, CourseIndexActivity.this);
            }
        });
        */
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

    public void openSearchCourse(View v) {
        Intent intent = new Intent(this, SearchCourseActivity.class);
        startActivity(intent);
    }


    @Override
    public void hasResult(TASKS task, String result) {
        instanceList.clear();


        for (CourseInstance instance : CourseInstance.createInstanceCollection(result)) {
            instanceList.add(instance);
        }

        adapter.notifyDataSetChanged();
    }
}
