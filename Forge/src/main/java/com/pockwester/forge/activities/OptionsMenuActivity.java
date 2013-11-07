package com.pockwester.forge.activities;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.pockwester.forge.R;

/**
 * Created by zack on 11/6/13.
 */
public class OptionsMenuActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.update_course:
                openCourseIndex();
                break;
            case R.id.update_avail:
                openAvailability();
                break;
            case R.id.logout:
                logoutUser();
                break;
            default:
                return false;
        }

        return true;
    }

    public void openCourseIndex() {
        startActivity( new Intent(this, CourseIndexActivity.class) );
    }

    public void openAvailability() {
        startActivity( new Intent(this, AvailabilityActivity.class) );
    }

    public void logoutUser() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user", null).commit();
        startActivity( new Intent(this, LoginActivity.class ));
        finish();
    }
}
