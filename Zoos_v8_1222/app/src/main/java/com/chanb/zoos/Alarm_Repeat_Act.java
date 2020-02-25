package com.chanb.zoos;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class Alarm_Repeat_Act extends AppCompatActivity {

    TabLayout tabs;
    ImageButton backBtn_repeat;
    FrameLayout frameLayout;
    Alarm_repeat_frag repeat_frag;
    TextView interval;
    EditText interval_number;
    Button submit;
    Calendar calendar;
    String number;
    ArrayList<RepeatItem> repeatItems;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_repeat_act);

        try {
            setting();
            setFrag();
            setTabs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        repeatItems = new ArrayList<>();
        submit = findViewById(R.id.alarm_repeat_submit);
        interval = findViewById(R.id.intervalView);
        interval_number = findViewById(R.id.repeat_value);
        number = interval_number.getText().toString();
        interval_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (page) {
                    case 0:
                        number = s.toString();
                        submit.setText(number + "일 마다 반복 설정");
                        break;
                    case 1:
                        number = s.toString();
                        submit.setText(number + "주 마다 반복 설정");
                        break;
                    case 2:
                        number = s.toString();
                        submit.setText(number + "개월 마다 반복 설정");
                        break;
                    case 3:
                        number = s.toString();
                        submit.setText(number + "년 마다 반복 설정");
                        break;
                }
            }
        });
        interval.setText("일 마다");
        backBtn_repeat = findViewById(R.id.backButton_alarm_repeat);
        backBtn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm_Repeat_Act.this, Calendar_info_act.class);
                intent.putExtra("page", "3");
                intent.putExtra("data", submit.getText().toString());

                if (page == 0) {
                    ArrayList<RepeatItem> test = new ArrayList<>();
                    int number_form = Integer.valueOf(number);
                    test.add(new RepeatItem(number_form, "day"));
                    intent.putExtra("data_array", test);

                    Log.d("data_array", test.get(0).getType() + test.get(0).getData());

                } else if (page == 1) {
                    intent.putExtra("data_array", repeat_frag.getWeek());
                } else if (page == 2) {
                    intent.putExtra("data_array", repeat_frag.getMonth());
                } else if (page == 3) {
                    intent.putExtra("data_array", repeat_frag.getYear());
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setFrag() {
        frameLayout = findViewById(R.id.container_repeat);
        repeat_frag = new Alarm_repeat_frag();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_repeat, repeat_frag).commit();
    }

    //하단 tabs.
    private void setTabs() {
        tabs = findViewById(R.id.tabs_repeat_Act);
        tabs.addTab(tabs.newTab().setText("매일"));
        tabs.addTab(tabs.newTab().setText("매주"));
        tabs.addTab(tabs.newTab().setText("매월"));
        tabs.addTab(tabs.newTab().setText("매년"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                number = interval_number.getText().toString();
                Fragment selected = null;
                if (position == 0) { // 매일
                    selected = repeat_frag;
                    interval.setText("일 마다");
                    repeat_frag.week.setVisibility(View.GONE);
                    repeat_frag.month_year_form.setVisibility(View.GONE);
                    submit.setText(number + "일 마다 반복 설정");
                    page = 0;
                } else if (position == 1) { //매주
                    selected = repeat_frag;
                    interval.setText("주 마다");
                    repeat_frag.week.setVisibility(View.VISIBLE);
                    repeat_frag.month_year_form.setVisibility(View.GONE);
                    submit.setText(number + "주 마다 반복 설정");

                    page = 1;
                } else if (position == 2) { //매월
                    selected = repeat_frag;
                    interval.setText("개월 마다");
                    repeat_frag.week.setVisibility(View.GONE);
                    repeat_frag.month_year_form.setVisibility(View.VISIBLE);
                    submit.setText(number + "개월 마다 반복 설정");
                    page = 2;
                } else if (position == 3) { //매년
                    selected = repeat_frag;
                    interval.setText("년 마다");
                    repeat_frag.week.setVisibility(View.GONE);
                    repeat_frag.month_year_form.setVisibility(View.VISIBLE);
                    submit.setText(number + "년 마다 반복 설정");
                    page = 3;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container_repeat, selected).commit();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                if (position == 0) { // 홈
                } else if (position == 1) { //일정
                } else if (position == 2) { //메시지
                } else if (position == 3) { //프로필
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                } else if (position == 1) {
                } else if (position == 2) {
                } else if (position == 3) {

                }
            }
        });
    }

}

