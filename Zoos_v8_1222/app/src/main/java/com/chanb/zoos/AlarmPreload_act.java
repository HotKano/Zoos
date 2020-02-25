package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmPreload_act extends AppCompatActivity {

    LinearLayout min3, min5, min10, min15, min30, hour1, hour2, hour3, hour12, day1, day2, week1;
    TextView m3, m5, m10, m15, m30, h1, h2, h3, h12, d1, d2, w1;
    ImageButton back;
    String type;
    int data;
    Button submit;
    ArrayList<PreloadItem> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_preload_act);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        temp = new ArrayList<>();
        back = findViewById(R.id.backButton_repeat_act);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        min3 = findViewById(R.id.min_three);
        min3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "min";
                data = 3;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                min3.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) min3.getChildAt(0);
                TextView m1 = (TextView) min3.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        min5 = findViewById(R.id.min_five);
        min5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "min";
                data = 5;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                min5.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) min5.getChildAt(0);
                TextView m1 = (TextView) min5.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });

        min10 = findViewById(R.id.min_ten);
        min10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "min";
                data = 10;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                min10.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) min10.getChildAt(0);
                TextView m1 = (TextView) min10.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        min15 = findViewById(R.id.min_tenFive);
        min15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "min";
                data = 15;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                min15.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) min15.getChildAt(0);
                TextView m1 = (TextView) min15.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);

            }
        });
        min30 = findViewById(R.id.min_halfHour);
        min30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "min";
                data = 30;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                min30.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) min30.getChildAt(0);
                TextView m1 = (TextView) min30.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        hour1 = findViewById(R.id.hour_one);
        hour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hour";
                data = 1;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                hour1.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) hour1.getChildAt(0);
                TextView m1 = (TextView) hour1.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        hour2 = findViewById(R.id.hour_two);
        hour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hour";
                data = 2;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                hour2.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) hour2.getChildAt(0);
                TextView m1 = (TextView) hour2.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        hour3 = findViewById(R.id.hour_three);
        hour3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hour";
                data = 3;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                hour3.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) hour3.getChildAt(0);
                TextView m1 = (TextView) hour3.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        hour12 = findViewById(R.id.hour_halfDay);
        hour12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hour";
                data = 12;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                hour12.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) hour12.getChildAt(0);
                TextView m1 = (TextView) hour12.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });

        day1 = findViewById(R.id.date_one);
        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "date";
                data = 1;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                day1.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) day1.getChildAt(0);
                TextView m1 = (TextView) day1.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        day2 = findViewById(R.id.date_two);
        day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "date";
                data = 2;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                day2.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) day2.getChildAt(0);
                TextView m1 = (TextView) day2.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);
            }
        });
        week1 = findViewById(R.id.date_week);
        week1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "date";
                data = 7;
                String data_form = String.valueOf(data);
                temp.add(new PreloadItem(data_form, type));
                week1.setBackgroundResource(R.drawable.round);
                TextView m = (TextView) week1.getChildAt(0);
                TextView m1 = (TextView) week1.getChildAt(1);
                m.setTextColor(Color.WHITE);
                m1.setTextColor(Color.WHITE);

            }
        });

        submit = findViewById(R.id.repeat_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmPreload_act.this, Calendar_info_act.class);
                intent.putExtra("page", "2");
                //intent.putExtra("type", type);
                //intent.putExtra("data", data);
                //intent.putExtra("type_array", temp);

                if (temp.size() > 0) {
                    intent.putExtra("type_array", temp);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });


    }

}

