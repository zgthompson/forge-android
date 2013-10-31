package com.pockwester.forge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zack on 10/16/13.
 */
public class CourseInstance {

    public static final String TABLE_NAME = "course_instance";
    public static final String ROW_ID = "_id";
    public static final String ROW_COURSE_INSTANCE_ID = "course_instance_id";
    public static final String ROW_CATALOG_NAME = "catalog_name";
    public static final String ROW_TITLE = "title";
    public static final String ROW_SECTION_ID = "section_id";
    public static final String ROW_LOCATION = "location";
    public static final String ROW_COMPONENT = "component";
    public static final String ROW_TIME = "time";
    public static final String ROW_DAY = "day";

    String id;
    String title;
    String catalogName;
    Map<String, Section> sections;

    public static void addToDB(CourseInstance instance, Context context) {

        String[] projection = new String[] { ROW_COURSE_INSTANCE_ID };
        String where = ROW_COURSE_INSTANCE_ID + "=" + instance.getId();
        Cursor findInstance = context.getContentResolver()
                .query(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, projection, where, null, null);

        // only add instance if it is not in the db already
        if (!findInstance.moveToFirst()) {
            for (ContentValues values : instance.createContentValuesList()) {
                Uri uri = context.getContentResolver().insert(ForgeProvider.COURSE_INSTANCE_CONTENT_URI, values);
                Log.d("forge: inserting ", uri.toString());
            }
        }
        findInstance.close();
    }

    public static Collection<CourseInstance> createInstanceCollection(String jsonString) {
        Map<String, CourseInstance> instanceMap = new HashMap<String, CourseInstance>();
        try {
            JSONObject jsonResult = new JSONObject(jsonString);
            JSONArray instanceArray = jsonResult.getJSONArray("instances");
            for (int i = 0; i < instanceArray.length(); i++) {
                JSONObject curInstance = instanceArray.getJSONObject(i);
                String instance_id = curInstance.getString("course_instance_id");
                if (instanceMap.containsKey(instance_id)) {
                    instanceMap.get(instance_id).addSection(curInstance);
                }
                else {
                    instanceMap.put(instance_id, new CourseInstance(curInstance));
                }
            }
        }
        catch (JSONException e) {
            Log.e("forge", "JSONException in CourseInstance.createInstanceCollection", e);
        }

        return instanceMap.values();
    }

    public CourseInstance(JSONObject instanceObject) throws JSONException {
        this.title = instanceObject.getString("title");
        this.catalogName = instanceObject.getString("subject") + " " + instanceObject.getString("catalog_no");
        this.id = instanceObject.getString("course_instance_id");
        this.sections = new HashMap<String, Section>();
        this.sections.put(instanceObject.getString("section_id"), new Section(instanceObject));
    }

    public CourseInstance(Cursor instanceCursor) {

        instanceCursor.moveToFirst();

        this.title = instanceCursor.getString(instanceCursor.getColumnIndex(CourseInstance.ROW_TITLE));
        this.catalogName= instanceCursor.getString(instanceCursor.getColumnIndex(CourseInstance.ROW_CATALOG_NAME));
        this.id = instanceCursor.getString(instanceCursor.getColumnIndex(CourseInstance.ROW_COURSE_INSTANCE_ID));
        this.sections = new HashMap<String, Section>();

        String section_id;

        do {
            section_id = instanceCursor.getString(instanceCursor.getColumnIndex(CourseInstance.ROW_SECTION_ID));
            if (!sections.containsKey(section_id)) {
                sections.put(section_id, new Section(instanceCursor));
            }
            else {
                sections.get(section_id).addTime(instanceCursor);
            }
        } while (instanceCursor.moveToNext());

        instanceCursor.close();
    }

    public List<ContentValues> createContentValuesList() {
        List<ContentValues> valueList = new ArrayList<ContentValues>();

        for (Section curSection : sections.values()) {
            for (Map.Entry<String,String> timeAndDay : curSection.getTimeAndDay().entrySet()) {

                ContentValues values = new ContentValues();

                values.put(ROW_CATALOG_NAME, catalogName);
                values.put(ROW_COMPONENT, curSection.getType());
                values.put(ROW_COURSE_INSTANCE_ID, id);
                values.put(ROW_DAY, timeAndDay.getValue());
                values.put(ROW_TIME, timeAndDay.getKey());
                values.put(ROW_SECTION_ID, curSection.getId());
                values.put(ROW_LOCATION, curSection.getLocation());
                values.put(ROW_TITLE, title);

                valueList.add(values);
            }
        }
        return valueList;
    }





    public void addSection(JSONObject instanceObject) throws JSONException {
        String section_id = instanceObject.getString("section_id");
        if (this.sections.containsKey(section_id)) {
            this.sections.get(section_id).addTime(instanceObject);
        }
        else {
            this.sections.put(section_id, new Section(instanceObject));
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getSectionDisplay() {
        String output = "";
        for (Section section : this.sections.values()) {
           output += section.getTimeDisplay()  + System.getProperty("line.separator");
        }
        return output;
    }

    public String getNameDisplay() {
        return catalogName + " " + sections.keySet().toString();
    }


    static class Section {
        String id;
        String location;
        String type;
        Map<String, String> timeAndDay;

        public Section(JSONObject sectionObject) throws JSONException {
            this.id = sectionObject.getString("section_id");
            this.location = sectionObject.getString("location");
            this.type = sectionObject.getString("component");
            this.timeAndDay = new HashMap<String, String>();

            addTime(sectionObject);
        }

        public Section(Cursor sectionCursor) {
            this.id = sectionCursor.getString(sectionCursor.getColumnIndex(CourseInstance.ROW_SECTION_ID));
            this.location = sectionCursor.getString(sectionCursor.getColumnIndex(CourseInstance.ROW_LOCATION));
            this.type = sectionCursor.getString(sectionCursor.getColumnIndex(CourseInstance.ROW_COMPONENT));
            this.timeAndDay = new HashMap<String, String>();

            addTime(sectionCursor);
        }

        public void addTime(JSONObject sectionObject) throws JSONException {
            String time = sectionObject.getString("start_time") + "-" + sectionObject.getString("end_time");
            String day = sectionObject.getString("day");
            String days = this.timeAndDay.get(time);

            if (days != null && !days.contains(day)) {
                days += day;
                this.timeAndDay.put(time, days);
            }
            else if (days == null) {
                this.timeAndDay.put(time, day);
            }
        }

        public void addTime(Cursor sectionCursor) {
            String time = sectionCursor.getString(sectionCursor.getColumnIndex(CourseInstance.ROW_TIME));
            String day = sectionCursor.getString(sectionCursor.getColumnIndex(CourseInstance.ROW_DAY));

            timeAndDay.put(time, day);
        }

        public String getId() {
            return id;
        }

        public String getLocation() {
            return location;
        }

        public String getType() {
            return type;
        }

        public Map<String, String> getTimeAndDay() {
            return timeAndDay;
        }

        public String getTimeDisplay() {
            String output = type + ": ";
            for (Map.Entry<String, String> cursor : timeAndDay.entrySet()) {
               output += cursor.getValue() + " " + cursor.getKey() + ", ";
            }
            output += location;

            return output;
        }
    }
}
