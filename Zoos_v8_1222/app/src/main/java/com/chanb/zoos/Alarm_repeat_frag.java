package com.chanb.zoos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Alarm_repeat_frag extends Fragment {

    ViewGroup rootView;
    LinearLayout week;
    boolean[] week_data;
    LinearLayout month_year_form;
    Button mon, tue, wed, thur, fri, sat, sun;
    ArrayList<RepeatItem> week_array, month_array, year_array, day_Array;
    RelativeLayout month_repeat_30, month_repeat_lastFri, month_repeat_lastDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.alarm_repeat_frag, container, false);
        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void setting() {
        week_array = new ArrayList<>();
        month_array = new ArrayList<>();
        year_array = new ArrayList<>();
        day_Array = new ArrayList<>();

        week = rootView.findViewById(R.id.repeat_on_week);
        month_year_form = rootView.findViewById(R.id.month_year_form);
        month_repeat_30 = rootView.findViewById(R.id.month_year_30);
        month_repeat_lastFri = rootView.findViewById(R.id.month_year_lastFri);
        month_repeat_lastDay = rootView.findViewById(R.id.month_year_lastDay);

        mon = rootView.findViewById(R.id.repeat_frag_monday);
        tue = rootView.findViewById(R.id.repeat_frag_tuesday);
        wed = rootView.findViewById(R.id.repeat_frag_wednesday);
        thur = rootView.findViewById(R.id.repeat_frag_thursday);
        fri = rootView.findViewById(R.id.repeat_frag_friday);
        sat = rootView.findViewById(R.id.repeat_frag_saturday);
        sun = rootView.findViewById(R.id.repeat_frag_sunday);

        final int color = getResources().getColor(R.color.start_with_phone_color);

        week_data = new boolean[]{false, sun.isSelected(), mon.isSelected(), tue.isSelected(), wed.isSelected(),
                thur.isSelected(), fri.isSelected(), sat.isSelected()};

        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mon.setBackgroundResource(R.drawable.round);
                mon.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(1, "week"));
            }
        });

        tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tue.setBackgroundResource(R.drawable.round);
                tue.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(2, "week"));
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wed.setBackgroundResource(R.drawable.round);
                wed.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(3, "week"));
            }
        });
        thur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thur.setBackgroundResource(R.drawable.round);
                thur.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(4, "week"));
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fri.setBackgroundResource(R.drawable.round);
                fri.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(5, "week"));
            }
        });

        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sat.setBackgroundResource(R.drawable.round);
                sat.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(6, "week"));
            }
        });

        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sun.setBackgroundResource(R.drawable.round);
                sun.setTextColor(Color.WHITE);
                week_array.add(new RepeatItem(7, "week"));
            }
        });

        month_repeat_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) month_repeat_30.getChildAt(0);
                text.setTextColor(color);
                ImageView check = (ImageView) month_repeat_30.getChildAt(1);
                check.setVisibility(View.VISIBLE);
                month_array.add(new RepeatItem(1, "month"));
                year_array.add(new RepeatItem(1, "year"));
            }
        });

        month_repeat_lastFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) month_repeat_lastFri.getChildAt(0);
                text.setTextColor(color);
                ImageView check = (ImageView) month_repeat_lastFri.getChildAt(1);
                check.setVisibility(View.VISIBLE);
                month_array.add(new RepeatItem(2, "month"));
                year_array.add(new RepeatItem(2, "year"));
            }
        });

        month_repeat_lastDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) month_repeat_lastDay.getChildAt(0);
                text.setTextColor(color);
                ImageView check = (ImageView) month_repeat_lastDay.getChildAt(1);
                check.setVisibility(View.VISIBLE);
                month_array.add(new RepeatItem(3, "month"));
                year_array.add(new RepeatItem(3, "year"));
            }
        });

    }

    public ArrayList getWeek() {
        return week_array;
    }

    public boolean[] getWeek_data() {
        return week_data;
    }

    public ArrayList getMonth() {
        return month_array;
    }

    public ArrayList getYear() {
        return year_array;
    }

}