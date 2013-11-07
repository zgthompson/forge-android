package com.pockwester.forge.models;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zack on 9/12/13.
 */
public class Course {

    String title;
    String catalogName;
    String id;

    public Course(JSONObject courseObject) throws JSONException {
        this.title = courseObject.getString("title");
        this.catalogName = courseObject.getString("subject") + " " + courseObject.getString("catalog_no");
        this.id = courseObject.getString("id");
    }

    public Course(String title, String catalogName) {
        this.title = title;
        this.catalogName = catalogName;
    }

    public String getTitle() {
        return title;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getId() {
        return id;
    }

    /*
    public static ContentValues jsonToContentValues(JSONObject courseObject) throws JSONException {
        ContentValues values = new ContentValues();
        try {
            values.put(ROW_COURSE_NUMBER, courseObject.getString("course_id"));
            values.put(ROW_UNITS, courseObject.getString("units"));
            values.put(ROW_TITLE, courseObject.getString("title"));
        } catch (JSONException e) {
            Log.e("forge", "JSONException in Course.jsonToContentValues");
            throw new JSONException("Improper course object");
        }
        return values;
    }
    */
}
