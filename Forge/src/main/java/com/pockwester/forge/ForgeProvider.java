package com.pockwester.forge;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by zack on 9/6/13.
 */
public class ForgeProvider extends ContentProvider {

    private ForgeDBHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String AUTHORITY = "com.pockwester.forge.provider";
    private static final String COURSE_PATH = "courses";

    private static final int COURSES = 1;
    private static final int COURSE_ID = 2;

    public static final Uri COURSE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, COURSE_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSE_PATH + "/#", COURSE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ForgeDBHelper(getContext(), ForgeDBHelper.DATABASE_NAME, null,
                ForgeDBHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Course.TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case COURSES:
                break;
            case COURSE_ID:
                queryBuilder.appendWhere(Course.ROW_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                Log.e("forge", uri.toString());
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy,
                having, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // Return string that identifies the MIME type for the URI
        switch(uriMatcher.match(uri)) {
            case COURSES:
                return "vnd.android.cursor.dir/vnd.pockwester.course";
            case COURSE_ID:
                return "vnd.android.cursor.item/vnd.pockwester.course";
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(Course.TABLE_NAME, Course.ROW_UNITS, values);
        if (rowId > 0) {
            Uri outUri = ContentUris.withAppendedId(COURSE_CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(outUri, null);
            Log.d("forge", "inserted: " + rowId);
            return outUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch(uriMatcher.match(uri)) {
            case COURSES:
                count = db.delete(Course.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                String newSelection = Course.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.delete(Course.TABLE_NAME, newSelection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch(uriMatcher.match(uri)) {
            case COURSES:
                count = db.update(Course.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSE_ID:
                String newSelection = Course.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(Course.TABLE_NAME, values, newSelection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d("forge", "updated a row");
        return count;
    }


    private static class ForgeDBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "forge.db";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_TABLE_COURSES = "create table " + Course.TABLE_NAME
            + " (" + Course.ROW_ID + " integer primary key autoincrement, "
            + Course.ROW_COURSE_ID + " TEXT,"
            + Course.ROW_COURSE_NUMBER + " TEXT,"
            + Course.ROW_COURSE_TYPE + " TEXT,"
            + Course.ROW_SECTION_NUMBER+ " TEXT,"
            + Course.ROW_UNITS + " TEXT,"
            + Course.ROW_TITLE + " TEXT,"
            + Course.ROW_TIME + " TEXT,"
            + Course.ROW_LOCATION + " TEXT,"
            + Course.ROW_INSTRUCTOR + " TEXT" + ");";

        public ForgeDBHelper(Context context, String name,
                                   SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_COURSES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Course.TABLE_NAME);
            onCreate(db);
        }
    }
}
