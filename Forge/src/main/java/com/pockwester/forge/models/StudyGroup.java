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
 * Created by zack on 11/17/13.
 */
public class StudyGroup extends CourseIdentifier implements ThreeLine {

    public static final String TABLE_NAME = "study_group";
    public static final String ROW_ID = "_id";
    public static final String ROW_STUDY_GROUP_ID = "study_group_id";
    public static final String ROW_SUBJECT_NO= "subject_no";
    public static final String ROW_TITLE = "title";
    public static final String ROW_TIME = "time";
    public static final String ROW_STUDENTS = "students";

    String time;
    String students;

    public StudyGroup(JSONObject courseObject) throws JSONException {
        super(courseObject.getString("title"), courseObject.getString("subject_no"), courseObject.getString("id"));
        this.time = courseObject.getString("time");
        this.students = courseObject.getJSONArray("students").toString().replaceAll("[\\[\"\\]]", "").replaceAll(",", ", ");
    }

    public StudyGroup(String title, String subjectNo, String id, String time, String students) {
        super(title, subjectNo, id);
        this.time = time;
        this.students = students;
    }
    @Override
    public String getLineThree() {
        return time;
    }

    public static void addToDB(StudyGroup group, Context context) {

        String[] projection = new String[] { ROW_STUDY_GROUP_ID };
        String where = ROW_STUDY_GROUP_ID + "=" + group.getId();
        Cursor findGroup = context.getContentResolver()
                .query(ForgeProvider.STUDY_GROUP_CONTENT_URI, projection, where, null, null);

        // only add instance if it is not in the db already
        if (!findGroup.moveToFirst()) {
            context.getContentResolver().insert(
                    ForgeProvider.STUDY_GROUP_CONTENT_URI, group.toContentValues());
        }
        findGroup.close();
    }

    public ContentValues toContentValues() {

        ContentValues values = new ContentValues();

        values.put(ROW_STUDY_GROUP_ID, id);
        values.put(ROW_SUBJECT_NO, subjectNo);
        values.put(ROW_TIME, time);
        values.put(ROW_TITLE, title);
        values.put(ROW_STUDENTS, students);

        return values;
    }

    public static List<StudyGroup> createGroupList(String jsonString) {
        List<StudyGroup> groups = new ArrayList<StudyGroup>();
        try {
            JSONObject jsonResult = new JSONObject(jsonString);
            JSONArray groupArray = jsonResult.getJSONArray("study_groups");
            for (int i = 0; i < groupArray.length(); i++) {
                groups.add(new StudyGroup(groupArray.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            Log.e("forge", "JSONException in StudyGroup.createInstanceList", e);
        }

        return groups;
    }

}
