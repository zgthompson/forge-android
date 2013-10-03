package com.pockwester.forge;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class CourseDetailActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private static String COURSE_ID = "COURSE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter and bind it to list view
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[] { Section.ROW_TIME, Section.ROW_BUILDING},
                new int[] {android.R.id.text1, android.R.id.text2} , 0);

        setListAdapter(adapter);

        // Initiate cursor loader
        // getLoaderManager().initLoader(0, null, this);

        long id = getIntent().getLongExtra("id", -1);

        if (id != -1) {
            populateLoader(id);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> sections = new HashSet<String>(prefs.getStringSet("sections", new HashSet<String>()));
        sections.add(String.valueOf(id));
        prefs.edit().putStringSet("sections", sections).apply();

        startActivity(new Intent(this, CourseIndexActivity.class));
    }

    private void populateLoader(long id) {
        // Pass search query to the Cursor Loader
        Bundle args = new Bundle();
        args.putLong(COURSE_ID, id);
       // Restart Cursor Loader to execute new query
        getLoaderManager().initLoader(0, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Extract id
        long course_id = args.getLong(COURSE_ID);

        String[] courseProjection = { Course.ROW_COURSE_NUMBER };
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(ForgeProvider.COURSE_CONTENT_URI, course_id),
                courseProjection, null, null, null);

        // move to only result and grab the course number
        cursor.moveToFirst();
        String course_number = cursor.getString(0);

        // Construct new query
        String[] sectionProjection = { Section.ROW_ID, Section.ROW_TIME, Section.ROW_BUILDING };
        String where = Section.ROW_COURSE_ID + "=" + "\"" + course_number + "\"";
        String[] whereArgs = null;
        String sortOrder = Section.ROW_TIME + " COLLATE LOCALIZED ASC";

        // Create the new Cursor loader
        return new CursorLoader(this, ForgeProvider.SECTION_CONTENT_URI, sectionProjection, where, whereArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
