package com.pockwester.forge;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the username text if available
        SharedPreferences settings = getSharedPreferences( PrefFileNames.USER_PREFS, 0);
        String UN = settings.getString("USER",null);
        int U = settings.getInt( "USER_ID", 0 );
        TextView t = (TextView) findViewById(R.id.outputText);

        if( UN == null || UN.length() <= 0 )
        {
            logoutUser();
        }
        else
        {
            t.setText( "Hello " + UN );
            Button btn = (Button) findViewById( R.id.loginButton );
            btn.setText( "Logout" );

            // Set button to logout user
            btn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutUser();
                }
            });

            btn = (Button) findViewById( R.id.class_button );
            btn.setEnabled( true );

            btn = (Button) findViewById( R.id.availability_button );
            btn.setEnabled( true );

            ImageButton iBtn = (ImageButton) findViewById( R.id.forge_button );
            iBtn.setEnabled( true );
        }

    }

    private void logoutUser()
    {
        // Clear the login prefs
        SharedPreferences settings = getSharedPreferences(PrefFileNames.USER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString( "USER", "" );
        editor.putInt( "USER_ID", 0 );
        editor.commit();

        // Set the main view to reflect the user is not logged in
        TextView t = (TextView) findViewById(R.id.outputText);
        t.setText( "Please Login" );
        Button btn = (Button) findViewById( R.id.loginButton );
        btn.setText( "Login" );

        // Set button to send user to login window
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin( v );
            }
        });

        btn = (Button) findViewById( R.id.class_button );
        btn.setEnabled( false );

        btn = (Button) findViewById( R.id.availability_button );
        btn.setEnabled( false );

        ImageButton iBtn = (ImageButton) findViewById( R.id.forge_button );
        iBtn.setEnabled( false );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openCourseIndex(View v) {
        Intent intent = new Intent(this, CourseIndexActivity.class);
        startActivity(intent);
    }

    public void openAvailability(View v) {
        Intent intent = new Intent(this, AvailabilityActivity.class);
        startActivity(intent);
    }

    public void openLogin(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity( intent );
    }
}
