package com.pockwester.forge.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pockwester.forge.R;
import com.pockwester.forge.models.ThreeLine;

import java.util.List;

/**
 * Created by zack on 11/17/13.
 */

public class ThreeLineAdapter extends ArrayAdapter<ThreeLine> {

    private final List<ThreeLine> list;
    private final Activity context;

    public ThreeLineAdapter(Activity context, List<ThreeLine> list) {
        super(context, R.layout.three_line_list_item, list);
        this.context = context;
        this.list= list;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.three_line_list_item, null);
        }
        else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.text1)).setText(list.get(pos).getLineOne());
        ((TextView) view.findViewById(R.id.text2)).setText(list.get(pos).getLineTwo());
        ((TextView) view.findViewById(R.id.text3)).setText(list.get(pos).getLineThree());
        view.setTag(list.get(pos).getId());

        return view;
    }
}