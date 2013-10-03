package com.pockwester.forge;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AW on 10/2/13.
 */
public class NewUserActivity extends Activity implements PWApi{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    public void createUser(View v)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        TextView t = (TextView) findViewById(R.id.userName);
        nameValuePairs.add(new BasicNameValuePair("username", t.getText().toString()));

        t = (TextView) findViewById(R.id.password);
        nameValuePairs.add(new BasicNameValuePair("password", t.getText().toString()));

        t = (TextView) findViewById(R.id.email);
        nameValuePairs.add(new BasicNameValuePair("email", t.getText().toString()));

        new PWApiTask( TASKS.USER_CREATE, nameValuePairs, this ).execute();
    }

    @Override
    public void hasResult( PWApi.TASKS task, String result )
    {
        // If the result is 1 then the user was created successfully
        if( result.equals("1"))
        {
            this.finish();
        }
        else
        {
            TextView t = (TextView) findViewById(R.id.errorText);
            t.setText( result );
        }
    }

}
