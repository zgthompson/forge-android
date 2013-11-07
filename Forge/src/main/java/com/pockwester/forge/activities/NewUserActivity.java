package com.pockwester.forge.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
 */
public class NewUserActivity extends Activity implements PWApi {

    private String attemptUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    public void createUser(View v)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        TextView t = (TextView) findViewById(R.id.userName);
        attemptUsername = t.getText().toString();
        nameValuePairs.add(new BasicNameValuePair("username", attemptUsername));

        t = (TextView) findViewById(R.id.password);
        nameValuePairs.add(new BasicNameValuePair("password", t.getText().toString()));

        t = (TextView) findViewById(R.id.email);
        nameValuePairs.add(new BasicNameValuePair("email", t.getText().toString()));

        new PWApiTask( TASKS.CREATE_STUDENT, nameValuePairs, this ).execute();
    }

    @Override
    public void hasResult( PWApi.TASKS task, String result )
    {
        // If the result is 1 then the user was created successfully
        if( Utilities.IsNumeric(result) )
        {
            Log.d("forge", "user: " + result);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user", result).commit();
            getSharedPreferences(result, 0).edit().putString("username", attemptUsername).commit();
            startActivity( new Intent(this, CourseIndexActivity.class) );
            finish();
        }
        else
        {
            TextView t = (TextView) findViewById(R.id.errorText);
            t.setText( result );
        }
    }

}
