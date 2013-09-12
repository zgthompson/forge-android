package com.pockwester.forge;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zack on 9/12/13.
 */
public class Course {
    String course_id;
    String course_number;
    String course_type;
    String section_number;
    String units;
    String title;
    String time;
    String location;
    String instructor;

    public Course(JSONObject courseObject) {
        try {
            course_id = courseObject.getString("course_id");
            course_number = courseObject.getString("course_number");
            course_type = courseObject.getString("course_type");
            section_number = courseObject.getString("section_number");
            units = courseObject.getString("units");
            title = courseObject.getString("title");
            time = courseObject.getString("time");
            location = courseObject.getString("location");
            instructor = courseObject.getString("instructor");
        } catch (JSONException e) {
            Log.e("forge", "JSONException in Course constructor", e);
        }
    }
}
