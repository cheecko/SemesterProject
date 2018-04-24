package com.example.sa.semesterproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SA on 1/14/2018.
 */

public class CustomAlertDialogListViewAdapter extends ArrayAdapter<CalendarView.Fasting> {
    private final List<CalendarView.Fasting> list;

    static class ViewHolder{
        protected TextView fastingName;
        protected ImageView fastingColor;
    }

    public CustomAlertDialogListViewAdapter(Context context, List<CalendarView.Fasting> list) {
        super(context, R.layout.dialog_layout, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_layout, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.fastingColor = (ImageView) view.findViewById(R.id.fastingColor);
        viewHolder.fastingName = (TextView) view.findViewById(R.id.fastingName);
        view.setTag(viewHolder);

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.fastingColor.setImageDrawable(list.get(position).getFastingColor());
        holder.fastingName.setText(list.get(position).getFastingName());
        return view;
    }
}
