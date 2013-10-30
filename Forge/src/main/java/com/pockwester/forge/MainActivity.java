package com.pockwester.forge;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        String user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", null);

        if( user == null )
        {
            logoutUser();
        }
        else
        {
            TextView t = (TextView) findViewById(R.id.outputText);
            String username = getSharedPreferences(user, 0).getString("username", "");

            t.setText( "Hello " + username);
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
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user", null).commit();

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
