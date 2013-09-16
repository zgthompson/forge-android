package com.pockwester.forge;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CourseIndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_index, menu);
        return true;
    }

    public void openAddClass(View v) {
        Intent intent = new Intent(this, SearchCourseActivity.class);
        startActivity(intent);
    }
}
