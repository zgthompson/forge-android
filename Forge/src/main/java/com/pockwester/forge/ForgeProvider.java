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
    private static final String SECTION_PATH = "sections";

    private static final int COURSES = 1;
    private static final int COURSE_ID = 2;
    private static final int SECTIONS = 3;
    private static final int SECTION_ID = 4;

    public static final Uri COURSE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_PATH);
    public static final Uri SECTION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SECTION_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, COURSE_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSE_PATH + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, SECTION_PATH, SECTIONS);
        uriMatcher.addURI(AUTHORITY, SECTION_PATH + "/#", SECTION_ID);
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

            case COURSES:
                queryBuilder.setTables(Course.TABLE_NAME);
                break;
            case COURSE_ID:
                queryBuilder.setTables(Course.TABLE_NAME);
                queryBuilder.appendWhere(Course.ROW_ID + "=" + uri.getLastPathSegment());
                break;
            case SECTIONS:
                queryBuilder.setTables(Section.TABLE_NAME);
                break;
            case SECTION_ID:
                queryBuilder.setTables(Section.TABLE_NAME);
                queryBuilder.appendWhere(Section.ROW_ID + "=" + uri.getLastPathSegment());
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
            case SECTIONS:
                return "vnd.android.cursor.dir/vnd.pockwester.section";
            case SECTION_ID:
                return "vnd.android.cursor.item/vnd.pockwester.section";
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
            case COURSES:
                rowId = db.insert(Course.TABLE_NAME, Course.ROW_UNITS, values);
                if (rowId > 0) {
                    outUri = ContentUris.withAppendedId(COURSE_CONTENT_URI, rowId);
                }
                break;
            case SECTIONS:
                rowId = db.insert(Section.TABLE_NAME, Section.ROW_TYPE, values);
                Log.d("forge", "section: " + rowId);
                if (rowId > 0) {
                    outUri = ContentUris.withAppendedId(SECTION_CONTENT_URI, rowId);
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
            case COURSES:
                count = db.delete(Course.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                newSelection = Course.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.delete(Course.TABLE_NAME, newSelection, selectionArgs);
                break;
            case SECTIONS:
                count = db.delete(Section.TABLE_NAME, selection, selectionArgs);
                break;
            case SECTION_ID:
                newSelection = Section.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.delete(Section.TABLE_NAME, newSelection, selectionArgs);
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
            case COURSES:
                count = db.update(Course.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSE_ID:
                newSelection = Course.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(Course.TABLE_NAME, values, newSelection, selectionArgs);
                break;
            case SECTIONS:
                count = db.update(Section.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SECTION_ID:
                newSelection = Section.ROW_ID + "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(Section.TABLE_NAME, values, newSelection, selectionArgs);
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
                + Course.ROW_COURSE_NUMBER + " TEXT,"
                + Course.ROW_UNITS + " TEXT,"
                + Course.ROW_TITLE + " TEXT" + ");";

        private static final String CREATE_TABLE_SECTIONS = "create table " + Section.TABLE_NAME
                + " (" + Section.ROW_ID + " integer primary key autoincrement, "
                + Section.ROW_SECTION_ID + " TEXT,"
                + Section.ROW_SECTION_NUM + " TEXT,"
                + Section.ROW_COURSE_ID + " TEXT,"
                + Section.ROW_BUILDING + " TEXT,"
                + Section.ROW_INSTRUCTOR + " TEXT,"
                + Section.ROW_TIME + " TEXT,"
                + Section.ROW_TYPE + " TEXT" + ");";

        public ForgeDBHelper(Context context, String name,
                                   SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_COURSES);
            db.execSQL(CREATE_TABLE_SECTIONS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Course.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Section.TABLE_NAME);
            onCreate(db);
        }
    }
}
