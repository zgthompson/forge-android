package com.pockwester.forge.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by zack on 11/6/13.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", null);


        if( user == null ) {
            startActivity( new Intent(this, LoginActivity.class));
        }
        else {
            startActivity( new Intent(this, MainActivity.class));
        }
    }
}
