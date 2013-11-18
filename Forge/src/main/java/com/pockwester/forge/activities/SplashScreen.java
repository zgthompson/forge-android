package com.pockwester.forge.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pockwester.forge.models.CourseInstance;
import com.pockwester.forge.models.StudyGroup;
import com.pockwester.forge.utils.PWApi;
import com.pockwester.forge.utils.PWApiTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zack on 11/6/13.
 */
public class SplashScreen extends Activity implements PWApi {

    private String student_id;
    private boolean updating_instances = false;
    private boolean updating_groups = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", null);


        if( student_id == null ) {
            startActivity( new Intent(this, LoginActivity.class));
        }
        else {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("student_id", student_id));

            new PWApiTask( TASKS.GRAB_UPDATES, nameValuePairs, this ).execute();

        }
    }

    @Override
    public void hasResult(TASKS task, String result) {
        if (task == TASKS.GRAB_UPDATES) {
            Log.d("forge", "Grab updates returns: " + result);
            if (!isUpdates(result)) {
                Log.d("forge", "no updates");
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        else if (task == TASKS.INSTANCE_SEARCH) {

            // prepare new set for shared prefs instance_ids
            Set<String> newInstanceSet = new HashSet<String>();

            // create course instance objects
            for (CourseInstance instance : CourseInstance.createInstanceList(result)) {

                // add for shared prefs
                newInstanceSet.add(instance.getId());

                // add for db
                CourseInstance.addToDB(instance, this);
            }

            // update shared prefs
            getSharedPreferences(student_id, 0).edit().putStringSet("instance_ids", newInstanceSet).commit();

            // start activity if done updating
            updating_instances = false;

            if (!updating_instances && !updating_groups) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        else if (task == TASKS.GROUP_SEARCH) {

            // prepare new set for shared prefs group_ids
            Set<String> newGroupSet = new HashSet<String>();

            // create course group objects
            for (StudyGroup group : StudyGroup.createGroupList(result)) {

                // add for shared prefs
                newGroupSet.add(group.getId());

                // add for db
                StudyGroup.addToDB(group, this);
            }

            // update shared prefs
            getSharedPreferences(student_id, 0).edit().putStringSet("group_ids", newGroupSet).commit();

            // start activity if done updating
            updating_groups = false;

            if (!updating_groups && !updating_groups) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    // check if study groups or courses have changed since last login
    private boolean isUpdates(String result) {
        boolean needToUpdate = false;
        SharedPreferences prefs = getSharedPreferences(student_id, 0);

        Set<String> instanceSet = new HashSet<String>(prefs.getStringSet("instance_ids", new HashSet<String>()));
        Set<String> groupSet = new HashSet<String>(prefs.getStringSet("group_ids", new HashSet<String>()));

        Set<String> compareSet = new HashSet<String>();

        try {
            JSONObject jsonResult = new JSONObject(result);

            JSONArray instanceArray = jsonResult.getJSONArray("instances");
            for (int i = 0; i < instanceArray.length(); i++) {
                compareSet.add(instanceArray.getString(i));
            }

            if (!compareSet.equals(instanceSet)) {
                needToUpdate = true;
                updating_instances = true;
                updateInstances();
            }

            compareSet.clear();

            JSONArray groupArray = jsonResult.getJSONArray("study_groups");
            for (int i = 0; i < groupArray.length(); i++) {
                compareSet.add(groupArray.getString(i));
            }

            if (!compareSet.equals(groupSet)) {
                needToUpdate = true;
                updating_groups = true;
                updateGroups();
            }

            compareSet.clear();

            JSONArray lookingArray = jsonResult.getJSONArray("looking");
            for (int i = 0; i < lookingArray.length(); i++) {
                compareSet.add(lookingArray.getString(i));
            }

            prefs.edit().putStringSet("looking_ids", compareSet).commit();

        } catch (JSONException e) {
            Log.e("forge", "JSONException in SplashScreen.isUpdates", e);
        }

        return needToUpdate;
    }

    private void updateInstances() {
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("student_id", student_id));

        new PWApiTask(TASKS.INSTANCE_SEARCH, args, this).execute();
    }

    private void updateGroups() {
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        args.add(new BasicNameValuePair("student_id", student_id));

        new PWApiTask(TASKS.GROUP_SEARCH, args, this).execute();
    }
}
