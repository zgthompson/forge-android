package com.pockwester.forge;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Menu;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AW on 10/1/13.
 * Window that shows the availability of the user. This requires that the
 * user be authenticated through the PWApi services.
 */
public class AvailabilityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.availability);
    }
    public void ViewButtonAnim(View imgvuw){
               imgvuw.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.availability, menu);
        return true;
    }

}
