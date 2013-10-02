package com.pockwester.forge;

import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Menu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AvailabilityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.availability);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.availability, menu);
        return true;
    }



}
