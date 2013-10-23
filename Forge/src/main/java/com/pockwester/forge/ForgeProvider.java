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
    private static final String COURSE_INSTANCE_PATH = "course_instances";

    private static final int COURSE_INSTANCES = 1;
    private static final int COURSE_INSTANCE_ID = 2;

    public static final Uri COURSE_INSTANCE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_INSTANCE_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, COURSE_INSTANCE_PATH, COURSE_INSTANCES);
        uriMatcher.addURI(AUTHORITY, COURSE_INSTANCE_PATH + "/#", COURSE_INSTANCE_ID);
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

        switch (uriMatcher.match(uri)) {

            case COURSE_INSTANCES:
                queryBuilder.setTables(CourseInstance.TABLE_NAME);
                break;
            case COURSE_INSTANCE_ID:
                queryBuilder.setTables(CourseInstance.TABLE_NAME);
                queryBuilder.appendWhere(CourseInstance.ROW_ID + "=" + uri.getLastPathSegment());
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
            case COURSE_INSTANCES:
                return "vnd.android.cursor.dir/vnd.pockwester.course_instance";
            case COURSE_INSTANCE_ID:
                return "vnd.android.cursor.item/vnd.pockwester.course_instance";
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = 0;
        Uri outUri = null;

        switch (uriMatcher.match(uri)) {
            case COURSE_INSTANCES:
                rowId = db.insert(CourseInstance.TABLE_NAME, null, values);
                if (rowId > 0) {
                    outUri = ContentUris.withAppendedId(COURSE_INSTANCE_CONTENT_URI, rowId);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }

        if (outUri != null) {
            getContext().getContentResolver().notifyChange(outUri, null);
            return outUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String newSelection;
        int count;

        switch(uriMatcher.match(uri)) {
            case COURSE_INSTANCES:
                count = db.delete(CourseInstance.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_INSTANCE_ID:
                newSelection = CourseInstance.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.delete(CourseInstance.TABLE_NAME, newSelection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String newSelection;
        int count;

        switch(uriMatcher.match(uri)) {
            case COURSE_INSTANCES:
                count = db.update(CourseInstance.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSE_INSTANCE_ID:
                newSelection = CourseInstance.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(CourseInstance.TABLE_NAME, values, newSelection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private static class ForgeDBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "forge.db";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_TABLE_COURSE_INSTANCE = "create table " +
                CourseInstance.TABLE_NAME + " (" +
                CourseInstance.ROW_ID + " integer primary key autoincrement, " +
                CourseInstance.ROW_COURSE_INSTANCE_ID + " TEXT," +
                CourseInstance.ROW_CATALOG_NAME + " TEXT," +
                CourseInstance.ROW_TITLE + " TEXT," +
                CourseInstance.ROW_SECTION_ID + " TEXT," +
                CourseInstance.ROW_LOCATION + " TEXT," +
                CourseInstance.ROW_TIME + " TEXT," +
                CourseInstance.ROW_COMPONENT + " TEXT," +
                CourseInstance.ROW_DAY + " TEXT" + ");";

        public ForgeDBHelper(Context context, String name,
                                   SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_COURSE_INSTANCE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CourseInstance.TABLE_NAME);
            onCreate(db);
        }
    }
}
