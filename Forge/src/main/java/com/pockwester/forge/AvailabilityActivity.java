package com.pockwester.forge;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by ZGT on 10/31/13.
 * Window that shows the availability of the user. This requires that the
 * user be authenticated through the PWApi services.
 */
public class AvailabilityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        populateView();

    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            view.setBackgroundColor(Color.GREEN);
        }
        else {
            view.setBackgroundColor(Color.RED);
        }
    }

    private void populateView() {
        // Grab parent view and view inflater
        LinearLayout parentView = (LinearLayout) findViewById(R.id.avail_holder);
        LayoutInflater inflater = getLayoutInflater();

        View view = null;

        for (int i = 0; i < 15; i++) {
            view = inflater.inflate(R.layout.avail_row, null);
            ((TextView) view.findViewById(R.id.avail_time)).setText(indexToTime(i));
            view.setTag(i);
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
}
