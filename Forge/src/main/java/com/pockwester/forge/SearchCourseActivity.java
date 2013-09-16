package com.pockwester.forge;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class SearchCourseActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
    private SimpleCursorAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter and bind it to list view
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] { Course.ROW_COURSE_NUMBER },
                new int[] {android.R.id.text1}, 0);

        setListAdapter(adapter);

        // Initiate cursor loader
        getLoaderManager().initLoader(0, null, this);

        // Handle the launch intent
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_class, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l,
                                View v, int position, long id) {
        // call detail activity for clicked entry
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {
        // Pass search query to the Cursor Loader
        Bundle args = new Bundle();
        args.putString(QUERY_EXTRA_KEY, queryStr);
       // Restart Cursor Loader to execute new query
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "";
        if (args != null) {
            // Extract search query
            query = args.getString(QUERY_EXTRA_KEY);
        }
            // Construct new query
            String[] projection = { Course.ROW_ID, Course.ROW_COURSE_NUMBER };

            String where = Course.ROW_COURSE_NUMBER + " LIKE \"%" + query + "%\"";
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
