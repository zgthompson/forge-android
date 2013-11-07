package com.pockwester.forge.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.pockwester.forge.utils.PWApi;
import com.pockwester.forge.utils.PWApiTask;
import com.pockwester.forge.R;
import com.pockwester.forge.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AW on 10/2/13.
 * Will connect to the PWApi and auth the user
 */
public class LoginActivity extends Activity implements PWApi {

    private String attemptUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_detail, menu);
        return true;
    }

    // Sends a PWApi task to log the user in
    public void attemptToLoginUser(View v)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        TextView t = (TextView) findViewById(R.id.userName);
        nameValuePairs.add(new BasicNameValuePair("username", t.getText().toString()));
        attemptUserName = t.getText().toString();

        t = (TextView) findViewById(R.id.password);
        nameValuePairs.add(new BasicNameValuePair("password", t.getText().toString()));

        new PWApiTask( TASKS.LOGIN, nameValuePairs, this ).execute();
    }

    // Starts the new user activity
    public void startNewUserActivity(View v){
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity( intent );
    }

    @Override
    public void hasResult( PWApi.TASKS task, String result )
    {
        // If the PWApi returns a numeric value then this is the user_id of the user
        if( Utilities.IsNumeric(result) )
        {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user", result).commit();

            SharedPreferences.Editor prefsEditor = getSharedPreferences(result, 0).edit();
            prefsEditor.putString("username", attemptUserName).commit();
            prefsEditor.putBoolean("courses_set", true).commit();

            startActivity( new Intent(this, MainActivity.class) );
            this.finish();
        }
        else
        {
            TextView t = (TextView) findViewById( R.id.loginErrorText );
            t.setText( "Could not find User / Password. Try Again");
        }
    }
}
