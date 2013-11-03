package com.pockwester.forge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZGT on 10/31/13.
 * Window that shows the availability of the user. This requires that the
 * user be authenticated through the PWApi services.
 */
public class AvailabilityActivity extends Activity implements PWApi {

    private List<Character> availList;
    private String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        availList = new ArrayList<Character>(168);


        student_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("student_id", student_id));

        new PWApiTask( TASKS.GET_AVAILABILITY, nameValuePairs, this ).execute();
    }

    public void onToggleClicked(View view) {
        // button on or off
        boolean on = ((ToggleButton) view).isChecked();

        // multiply the day by 24 and add the hour of the day
        int availIndex = (Integer) view.getTag() * 24 + (Integer) ((View) view.getParent()).getTag();

        if (on) {
            view.setBackgroundColor(Color.GREEN);
            availList.set(availIndex, '2');
        }
        else {
            view.setBackgroundColor(Color.RED);
            availList.set(availIndex, '0');
        }
    }

    public void submitAvailability (View view) {
        String availString = "";
        for (Character c : availList) {
            availString += c;
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("student_id", student_id));
        nameValuePairs.add(new BasicNameValuePair("avail_string", availString));

        new PWApiTask( TASKS.UPDATE_AVAILABILITY, nameValuePairs, this ).execute();
    }

    private void populateView() {
        // Grab parent view and view inflater
        LinearLayout parentView = (LinearLayout) findViewById(R.id.avail_holder);
        LayoutInflater inflater = getLayoutInflater();

        View view = null;

        // Create 14 time rows from 8am to 10pm
        for (int i = 0; i < 15; i++) {
            view = inflater.inflate(R.layout.avail_row, null);
            // convert index to 12 hour time
            ((TextView) view.findViewById(R.id.avail_time)).setText(indexToTime(i));

            LinearLayout dayHolder = (LinearLayout) view.findViewById(R.id.day_holder);
            dayHolder.setTag(i);

            // Set the color of each button based on availability from api call
            for (int j = 0; j < 7; j++) {
                ToggleButton dayButton = (ToggleButton) dayHolder.getChildAt(j);
                dayButton.setTag(j);

                char timeValue = availList.get(j * 24 + i);
                if (timeValue == '0') {
                    dayButton.setBackgroundColor(Color.RED);
                }
                else if (timeValue == '2') {
                    dayButton.setBackgroundColor(Color.GREEN);
                }
            }

            parentView.addView(view);
        }
    }

    private String indexToTime(int index) {
        String result = "";
        result += (index + 7) % 12 + 1;
        result += index > 3 ? " PM" : " AM";
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.availability, menu);
        return true;
    }

    @Override
    public void hasResult(TASKS task, String result) {
        if (task == TASKS.GET_AVAILABILITY) {
            for (int i = 0; i < result.length(); i++) {
                availList.add(i, result.charAt(i));
            }
            populateView();
        }
        else if (task == TASKS.UPDATE_AVAILABILITY) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
