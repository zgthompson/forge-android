package com.pockwester.forge;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zack on 9/26/13.
 */
public class Section {
    public static final String TABLE_NAME = "sections";
    public static final String ROW_ID = "_id";
    public static final String ROW_SECTION_ID = "section_id";
    public static final String ROW_SECTION_NUM = "section_number";
    public static final String ROW_BUILDING = "building";
    public static final String ROW_INSTRUCTOR = "instructor";
    public static final String ROW_TYPE = "type";
    public static final String ROW_COURSE_ID = "course_id";
    public static final String ROW_TIME = "time";


    public static final int TYPE = 1;

    public static ContentValues jsonToContentValues(JSONObject courseObject) throws JSONException {
        ContentValues values = new ContentValues();
        try {
            values.put(ROW_SECTION_ID, courseObject.getString("section_id"));
            values.put(ROW_SECTION_NUM, courseObject.getString("section_number"));
            values.put(ROW_BUILDING, courseObject.getString("building"));
            values.put(ROW_INSTRUCTOR, courseObject.getString("instructor"));
            values.put(ROW_TYPE, courseObject.getString("type"));
            values.put(ROW_COURSE_ID, courseObject.getString("course_id"));
            values.put(ROW_TIME, courseObject.getString("time"));
        } catch (JSONException e) {
            Log.e("forge", "JSONException in Section.jsonToContentValues");
            throw new JSONException("Improper section object");
        }
        return values;
    }

    // To prevent instantiation
    private Section() {}
}
