package com.pockwester.forge;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Set;

public class CourseIndexActivity extends Activity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[] { Section.ROW_COURSE_ID, Section.ROW_TIME},
                new int[] {android.R.id.text1, android.R.id.text2 } , 0);


        ListView listView = (ListView) findViewById(R.id.section_list);
        listView.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_index, menu);
        return true;
    }

    public void openSearchCourse(View v) {
        Intent intent = new Intent(this, SearchCourseActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader newLoader = null;

        Set<String> sectionSet = getSharedPreferences("com.pockwester.forge", MODE_PRIVATE).getStringSet("sections", null);

        if (sectionSet != null) {
            String[] sectionProjection = { Section.ROW_ID, Section.ROW_COURSE_ID, Section.ROW_TIME };
            String selection = "";
            int count = 0;
            for (String sectionId : sectionSet) {
                if (count > 0) selection += " OR ";
                selection += Section.ROW_ID + "=" + sectionId;
                count++;
            }

            newLoader = new CursorLoader(this, ForgeProvider.SECTION_CONTENT_URI, sectionProjection, selection, null, null);
        }
        return newLoader;
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
