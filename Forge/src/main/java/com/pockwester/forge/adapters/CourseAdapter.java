package com.pockwester.forge.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pockwester.forge.models.Course;

import java.util.List;

/**
 * Created by zack on 10/16/13.
 */
public class CourseAdapter extends ArrayAdapter<Course> {

    private final List<Course> list;
    private final Activity context;

    public CourseAdapter(Activity context, List<Course> list) {
        super(context, android.R.layout.simple_list_item_2, list);
        this.context = context;
        this.list= list;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(android.R.layout.simple_list_item_2, null);
        }
        else {
            view = convertView;
        }

        ((TextView) view.findViewById(android.R.id.text1)).setText(list.get(pos).getCatalogName());
        ((TextView) view.findViewById(android.R.id.text2)).setText(list.get(pos).getTitle());
        view.setTag(list.get(pos).getId());

        return view;
    }
}
