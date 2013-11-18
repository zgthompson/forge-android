package com.pockwester.forge.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.pockwester.forge.providers.ForgeProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 10/16/13.
 */
public class CourseInstance extends CourseIdentifier implements ThreeLine {

    public static final String TABLE_NAME = "course_instance";
    public static final String ROW_ID = "_id";
    public static final String ROW_COURSE_INSTANCE_ID = "course_instance_id";
    public static final String ROW_SUBJECT_NO= "subject_no";
    public static final String ROW_TITLE = "title";
    public static final String ROW_TIME = "time";

    protected String time;


    public CourseInstance(String title, String subjectNo, String id, String time) {
        super(title, subjectNo, id);
        this.time = time;
    }

    public CourseInstance(JSONObject instanceObject) throws JSONException {
        super(instanceObject.getString("title"), instanceObject.getString("subject_no"), instanceObject.getString("id"));
        this.time = instanceObject.getString("time");
    }

    @Override
    public String getLineThree() {
        return time;
    }

    public static List<CourseInstance> createInstanceList(String jsonString) {
        List<CourseInstance> instances = new ArrayList<CourseInstance>();
        try {
            JSONObject jsonResult = new JSONObject(jsonString);
            JSONArray instanceArray = jsonResult.getJSONArray("instances");
            for (int i = 0; i < instanceArray.length(); i++) {
                instances.add(new CourseInstance( instanceArray.getJSONObject(i) ));
            }
        }
        catch (JSONException e) {
            Log.e("forge", "JSONException in CourseInstance.createInstanceCollection", e);
        }

        return instances;
    }

    public static void addToDB(CourseInstance instance, Context context) {

        String[] projection = new String[] { ROW_COURSE_INSTANCE_ID };
        String where = ROW_COURSE_INSTANCE_ID + "=" + instance.getId();
        Cursor findInstance = context.getContentResolver()
                .query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);

        // only add instance if it is not in the db already
        if (!findInstance.moveToFirst()) {
            context.getContentResolver().insert(
                    ForgeProvider.COURSE_INSTANCE_CONTENT_URI, instance.toContentValues() );
        }
        findInstance.close();
    }

    public ContentValues toContentValues() {

        ContentValues values = new ContentValues();

        values.put(ROW_COURSE_INSTANCE_ID, id);
        values.put(ROW_SUBJECT_NO, subjectNo);
        values.put(ROW_TIME, time);
        values.put(ROW_TITLE, title);

        return values;
    }
}
