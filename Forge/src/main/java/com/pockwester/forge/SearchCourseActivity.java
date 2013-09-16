package com.pockwester.forge;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class SearchCourseActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String QUERY = "QUERY";
    private SimpleCursorAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter and bind it to list view
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[] { Course.ROW_COURSE_NUMBER, Course.ROW_TITLE },
                new int[] {android.R.id.text1, android.R.id.text2}, 0);

        setListAdapter(adapter);

        // Initiate cursor loader
        getLoaderManager().initLoader(0, null, this);

        // Show all courses
        updateLoader("");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.search_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        LinearLayout linearLayout = (LinearLayout) actionBar.getCustomView();

        EditText editText = (EditText) linearLayout.getChildAt(1);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // update list view to display courses matching current text
                updateLoader(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return true;
    }

    public void onListItemClick(ListView l,
                                View v, int position, long id) {
        // call detail activity for clicked entry
    }

    private void updateLoader(String queryStr) {
        // Pass search query to the Cursor Loader
        Bundle args = new Bundle();
        args.putString(QUERY, queryStr);
       // Restart Cursor Loader to execute new query
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "";
        if (args != null) {
            // Extract search query
            query = args.getString(QUERY);
        }
        // Construct new query
        String[] projection = { Course.ROW_ID, Course.ROW_COURSE_NUMBER, Course.ROW_TITLE };

        String where = Course.ROW_COURSE_NUMBER + " LIKE \"%" + query + "%\" OR " +
                Course.ROW_TITLE + " LIKE \"%" + query + "%\"";
        String[] whereArgs = null;
        String sortOrder = Course.ROW_COURSE_NUMBER + " COLLATE LOCALIZED ASC";

        // Create the new Cursor loader
        return new CursorLoader(this, ForgeProvider.COURSE_CONTENT_URI, projection, where, whereArgs,
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
