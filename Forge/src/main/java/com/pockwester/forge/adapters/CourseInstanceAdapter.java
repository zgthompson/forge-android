package com.pockwester.forge.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pockwester.forge.R;
import com.pockwester.forge.models.CourseInstance;

import java.util.List;

/**
 * Created by zack on 10/16/13.
 */
public class CourseInstanceAdapter extends ArrayAdapter<CourseInstance> {

    private final List<CourseInstance> list;
    private final Activity context;
    private TYPES type;

    public enum TYPES { ADD, REMOVE, FORGE };

    public CourseInstanceAdapter(Activity context, List<CourseInstance> list, TYPES type) {
        super(context, R.layout.course_list_item, list);
        this.context = context;
        this.list= list;
        this.type = type;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.course_list_item, null);
        }
        else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.text1)).setText(list.get(pos).getNameDisplay());
        ((TextView) view.findViewById(R.id.text2)).setText(list.get(pos).getSectionDisplay());
        view.setTag(list.get(pos).getId());

        ImageView imageView = (ImageView) view.findViewById(R.id.course_icon);

        switch (type) {
            case ADD:
                imageView.setBackgroundResource(android.R.drawable.ic_input_add);
                break;
            case REMOVE:
                imageView.setBackgroundResource(android.R.drawable.ic_delete);
                break;
            case FORGE:
                imageView.setBackgroundResource(R.drawable.anvil_icon);
                break;
        }

        return view;
    }
}