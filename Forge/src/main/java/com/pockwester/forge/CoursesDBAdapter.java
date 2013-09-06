package com.pockwester.forge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zack on 9/6/13.
 */

public class CoursesDBAdapter extends DBAdapter {

    public static final String ROW_ID = "_id";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NUMBER = "course_number";
    public static final String COURSE_TYPE = "course_type";
    public static final String SECTION_NUMBER = "section_number";
    public static final String TITLE= "title";
    public static final String TIME = "time";
    public static final String LOCATION = "location";
    public static final String INSTRUCTOR = "instructor";

    private static final String DATABASE_TABLE = "courses";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public CoursesDBAdapter(Context context) {
        super(context);
        mCtx = context;
    }

    /**
     * Open the courses database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public CoursesDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new course. If the course is successfully created return the new
     * rowId for that course, otherwise return a -1 to indicate failure.
     *
     * @return rowId or -1 if failed
     */
    public long createCourse(String course_id, String course_number, String course_type,
                             String section_number, String title, String time, String location,
                             String instructor) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COURSE_ID, course_id);
        initialValues.put(COURSE_NUMBER, course_number);
        initialValues.put(COURSE_TYPE, course_type);
        initialValues.put(SECTION_NUMBER, section_number);
        initialValues.put(TITLE, title);
        initialValues.put(TIME, time);
        initialValues.put(LOCATION, location);
        initialValues.put(INSTRUCTOR, instructor);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the course with the given rowId
     *
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteCourse(long rowId) {

        return mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all courses in the database
     *
     * @return Cursor over all courses
     */
    public Cursor getAllCourses() {

        return mDb.query(DATABASE_TABLE, new String[] { ROW_ID,
                COURSE_ID, COURSE_NUMBER, COURSE_TYPE, SECTION_NUMBER, TITLE, TIME, LOCATION,
                INSTRUCTOR }, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the course that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching course, if found
     * @throws SQLException if course could not be found/retrieved
     */
    public Cursor getCar(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID,
                COURSE_ID, COURSE_NUMBER, COURSE_TYPE, SECTION_NUMBER, TITLE, TIME, LOCATION,
                INSTRUCTOR }, ROW_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the course.
     *
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCourse(long rowId, String course_id, String course_number, String course_type,
                             String section_number, String title, String time, String location,
                             String instructor) {
        ContentValues args = new ContentValues();
        args.put(COURSE_ID, course_id);
        args.put(COURSE_NUMBER, course_number);
        args.put(COURSE_TYPE, course_type);
        args.put(SECTION_NUMBER, section_number);
        args.put(TITLE, title);
        args.put(TIME, time);
        args.put(LOCATION, location);
        args.put(INSTRUCTOR, instructor);

        return mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) >0;
    }
}
