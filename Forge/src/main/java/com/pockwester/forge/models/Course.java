package com.pockwester.forge.models;

import com.pockwester.forge.activities.CourseIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zack on 9/12/13.
 */
public class Course extends CourseIdentifier {

    public Course(JSONObject courseObject) throws JSONException {
        super(courseObject.getString("title"), courseObject.getString("subject_no"), courseObject.getString("id"));
    }

    public Course(String title, String subjectNo, String id) {
        super(title, subjectNo, id);
    }
}
