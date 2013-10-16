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
    protected int black;
    protected int white;
    protected int blue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.availability);
        black =0;
        blue =0;
        white = 0;
    }

    public void ViewButtonBlack(View imgvuw){
            if((black % 2) == 0){
                imgvuw.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }
            else{ imgvuw.setBackgroundResource(R.drawable.abc_ab_bottom_solid_dark_holo);
            }
        black = (black + 1);
        //findViewById(R.id.imageView16)
              }

    public void ViewButtonWhite(View imgvuw){
        if((white % 2) == 0){
            imgvuw.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
        else{ imgvuw.setBackgroundResource(R.drawable.abc_ab_bottom_solid_light_holo);
        }
        white = (white + 1);
    }

    public void ViewButtonBlue(View imgvuw){
        if((blue % 2) == 0){
            imgvuw.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
        else{ imgvuw.setBackgroundResource(R.drawable.abc_cab_background_top_holo_dark);
        }
        blue = (blue + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.availability, menu);
        return true;
    }

}
