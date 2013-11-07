package com.pockwester.forge.activities;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pockwester.forge.models.Course;
import com.pockwester.forge.adapters.CourseAdapter;
import com.pockwester.forge.utils.PWApi;
import com.pockwester.forge.utils.PWApiTask;
import com.pockwester.forge.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchCourseActivity extends ListActivity implements PWApi {

    private String query;
    private ArrayAdapter<Course> adapter;
    private List<Course> courseList;
    private boolean searching;
    private boolean newQuery;
    private Timer timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter and bind it to list view
        query = "";
        searching = false;
        newQuery = false;
        courseList = new ArrayList<Course>();
        adapter = new CourseAdapter(this, courseList);

        setListAdapter(adapter);



    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkForUpdates();
            }
        }, 1000, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {

            actionBar.setCustomView(R.layout.search_bar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LinearLayout linearLayout = (LinearLayout) actionBar.getCustomView();

            EditText editText = (EditText) linearLayout.getChildAt(1);

            if (editText != null) {

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // sets query to most recent text
                        if (s.length() > 1) {
                            query = s.toString();
                            newQuery = true;
                        }
                        else {
                            newQuery = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }

        return true;
    }

    @Override
    public void hasResult(TASKS task, String result) {
        courseList.clear();
        try {
            JSONObject jsonResult = new JSONObject(result);
            JSONArray courseArray = jsonResult.getJSONArray("courses");
            for (int i = 0; i < courseArray.length(); i++) {
               courseList.add(new Course(courseArray.getJSONObject(i))) ;
            }
        } catch (JSONException e) {
            Log.e("forge", "JSONException in SearchCourseActivity.hasResult", e);
        }

        adapter.notifyDataSetChanged();

        searching = false;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent detailIntent = new Intent(this, CourseDetailActivity.class);
        detailIntent.putExtra("course_id", v.getTag().toString());
        startActivity(detailIntent);
    }

    private void checkForUpdates() {
        if (!searching && newQuery) {

            searching = true;
            newQuery = false;

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("like", query));

            new PWApiTask(TASKS.COURSE_SEARCH, args, SearchCourseActivity.this).execute();
        }
    }
}
