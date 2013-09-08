package com.pockwester.forge;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zack on 9/6/13.
 */

public class DBAdapter {

    public static final String DATABASE_NAME = "forge.db";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_COURSES = "create table courses (_id integer primary key autoincrement, "
            + CoursesDBAdapter.COURSE_ID + " TEXT,"
            + CoursesDBAdapter.COURSE_NUMBER + " TEXT,"
            + CoursesDBAdapter.COURSE_TYPE + " TEXT,"
            + CoursesDBAdapter.SECTION_NUMBER+ " TEXT,"
            + CoursesDBAdapter.UNITS + " TEXT,"
            + CoursesDBAdapter.TITLE + " TEXT,"
            + CoursesDBAdapter.TIME + " TEXT,"
            + CoursesDBAdapter.LOCATION + " TEXT,"
            + CoursesDBAdapter.INSTRUCTOR + " TEXT" + ");";


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param ctx
     */
    public DBAdapter(Context ctx)
    {
        context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_COURSES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            // Adding any table mods to this guy here
        }
    }

    /**
     * open the db
     * @return this
     * @throws SQLException
     * return type: DBAdapter
     */
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * close the db
     * return type: void
     */
    public void close()
    {
        DBHelper.close();
    }
}
