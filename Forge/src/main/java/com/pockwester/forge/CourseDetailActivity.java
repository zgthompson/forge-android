package com.pockwester.forge;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class CourseDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);


        long id = getIntent().getLongExtra("id", -1);

        if (id != -1) {
            populateView(id);
        }
        else {
            Log.e("forge", "no id in CourseDetailActivity");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_detail, menu);
        return true;
    }

    private void populateView(long id) {
        String[] projection = { Course.ROW_COURSE_NUMBER, Course.ROW_TITLE };

        Uri uri = ContentUris.withAppendedId(ForgeProvider.COURSE_CONTENT_URI, id);

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        ((TextView) findViewById(R.id.course_number)).setText(cursor.getString(0));
        ((TextView) findViewById(R.id.course_title)).setText(cursor.getString(1));

        cursor.close();
    }
    
}
