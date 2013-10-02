package com.pockwester.forge;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

/**
 * Created by AW on 10/1/13.
 */
public class AvailabilityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_detail, menu);
        return true;
    }
}
