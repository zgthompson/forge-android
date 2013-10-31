package com.pockwester.forge;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zack on 10/16/13.
 */
public class CourseInstanceAdapter extends ArrayAdapter<CourseInstance> {

    private final List<CourseInstance> list;
    private final Activity context;

    public CourseInstanceAdapter(Activity context, List<CourseInstance> list) {
        super(context, android.R.layout.simple_list_item_2, list);
        this.context = context;
        this.list= list;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        else {
            view = convertView;
        }

        ((TextView) view.findViewById(android.R.id.text1)).setText(list.get(pos).getNameDisplay());
        ((TextView) view.findViewById(android.R.id.text2)).setText(list.get(pos).getSectionDisplay());
        view.setTag(list.get(pos).getId());

        return view;
    }
}