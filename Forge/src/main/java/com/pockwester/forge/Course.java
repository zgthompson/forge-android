package com.pockwester.forge;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zack on 9/12/13.
 */
public class Course {

    public static final String TABLE_NAME = "courses";
    public static final String ROW_ID = "_id";
    public static final String ROW_COURSE_ID = "course_id";
    public static final String ROW_COURSE_NUMBER = "course_number";
    public static final String ROW_COURSE_TYPE = "course_type";
    public static final String ROW_SECTION_NUMBER = "section_number";
    public static final String ROW_UNITS = "units";
    public static final String ROW_TITLE= "title";
    public static final String ROW_TIME = "time";
    public static final String ROW_LOCATION = "location";
    public static final String ROW_INSTRUCTOR = "instructor";

    public static ContentValues jsonToContentValues(JSONObject courseObject) throws JSONException {
        ContentValues values = new ContentValues();
        try {
            values.put(ROW_COURSE_ID, courseObject.getString("course_id"));
            values.put(ROW_COURSE_NUMBER, courseObject.getString("course_number"));
            values.put(ROW_COURSE_TYPE, courseObject.getString("course_type"));
            values.put(ROW_SECTION_NUMBER, courseObject.getString("section_number"));
            values.put(ROW_UNITS, courseObject.getString("units"));
            values.put(ROW_TITLE, courseObject.getString("title"));
            values.put(ROW_TIME, courseObject.getString("time"));
            values.put(ROW_LOCATION, courseObject.getString("location"));
            values.put(ROW_INSTRUCTOR, courseObject.getString("instructor"));
        } catch (JSONException e) {
            Log.e("forge", "JSONException in Course.jsonToContentValues");
            throw new JSONException("Improper course object");
        }
        return values;
    }

    // To prevent instantiation
    private Course() {}
}
