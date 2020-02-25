package com.chanb.zoos;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Calendar_select_act extends AppCompatActivity {


    ImageView back;
    Button submit;
    TimePicker timePicker;
    DatePicker datePicker;
    TextView startDay, endDay, startTime, endTime, waiting, waiting_start;
    LinearLayout startDayZone, endDayZone;
    int year_input, month_input, day_input, hour_input, minute_input;
    int year_input_end, month_input_end, day_input_end, hour_input_end, minute_input_end;
    String id;
    String kind = "start";
    GlobalApplication globalApplication;
    //요일 피커
    NumberPicker text_day_picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_select_act);
        try {
            setting();
            timeSetting();
            setTestDay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //뷰 세팅.
    private void setting() {
        text_day_picker = findViewById(R.id.daysOfWeek);
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        startDay = findViewById(R.id.start_date_select);
        endDay = findViewById(R.id.end_date_select);
        startTime = findViewById(R.id.start_time_select);
        endTime = findViewById(R.id.end_time_select);
        startDayZone = findViewById(R.id.start_date_zone);
        endDayZone = findViewById(R.id.end_date_zone);
        waiting = findViewById(R.id.waiting_select);
        waiting_start = findViewById(R.id.waiting_select_start);

        id = getIntent().getStringExtra("id");

        startDayZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind = "start";
                startTime.setVisibility(View.VISIBLE);
                startDay.setVisibility(View.VISIBLE);
                waiting_start.setVisibility(View.GONE);
            }
        });

        endDayZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind = "end";
                endTime.setVisibility(View.VISIBLE);
                endDay.setVisibility(View.VISIBLE);
                waiting.setVisibility(View.GONE);
            }
        });

        back = findViewById(R.id.backButton_calendar_select);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        submit = findViewById(R.id.calendar_input_select);

        datePicker = findViewById(R.id.datePicker_select);
        timePicker = findViewById(R.id.timePicker_select);


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {


            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (kind.equals("start")) {
                    hour_input = hourOfDay;
                    minute_input = minute;
                    String hour_form = hour_input + "";
                    String min_form = minute_input + "";

                    if (hour_input < 10)
                        hour_form = "0" + hour_form;

                    if (minute_input < 10)
                        min_form = "0" + min_form;

                    startTime.setVisibility(View.VISIBLE);
                    startDay.setVisibility(View.VISIBLE);
                    waiting_start.setVisibility(View.GONE);
                    startTime.setText(hour_form + " : " + min_form);

                } else if (kind.equals("end")) {
                    hour_input_end = hourOfDay;
                    minute_input_end = minute;
                    String hour_form = hour_input_end + "";
                    String min_form = minute_input_end + "";

                    if (hour_input_end < 10)
                        hour_form = "0" + hour_form;

                    if (minute_input_end < 10)
                        min_form = "0" + min_form;


                    endTime.setText(hour_form + " : " + min_form);
                    endTime.setVisibility(View.VISIBLE);
                    endDay.setVisibility(View.VISIBLE);
                    waiting.setVisibility(View.GONE);
                }

            }

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    try {
                        if (kind.equals("start")) {
                            String day = getDateDay(year, monthOfYear, dayOfMonth);
                            year_input = year;
                            month_input = monthOfYear;
                            day_input = dayOfMonth;

                            String month_form = String.valueOf(monthOfYear + 1);
                            String date_form = String.valueOf(dayOfMonth);

                            String year_form = String.valueOf(year);
                            year_form = year_form.substring(2, 4);

                            if ((monthOfYear + 1) < 10)
                                month_form = "0" + month_form;

                            if ((dayOfMonth) < 10)
                                date_form = "0" + date_form;

                            startTime.setVisibility(View.VISIBLE);
                            startDay.setVisibility(View.VISIBLE);
                            waiting_start.setVisibility(View.GONE);
                            startDay.setText(year_form + "." + month_form + "." + date_form + "(" + day + ")");

                        } else if (kind.equals("end")) {
                            String day = getDateDay(year, monthOfYear, dayOfMonth);
                            year_input_end = year;
                            month_input_end = monthOfYear;
                            day_input_end = dayOfMonth;

                            String month_form = String.valueOf(monthOfYear + 1);
                            String date_form = String.valueOf(dayOfMonth);

                            String year_form = String.valueOf(year);
                            year_form = year_form.substring(2, 4);

                            if ((monthOfYear + 1) < 10)
                                month_form = "0" + month_form;

                            if ((dayOfMonth) < 10)
                                date_form = "0" + date_form;

                            endTime.setVisibility(View.VISIBLE);
                            endDay.setVisibility(View.VISIBLE);
                            waiting.setVisibility(View.GONE);
                            endDay.setText(year_form + "." + month_form + "." + date_form + "(" + day + ")");
                        }


                        String day_pre = getDateDay(year, monthOfYear, dayOfMonth - 1);
                        String day_now = getDateDay(year, monthOfYear, dayOfMonth);
                        String day_next = getDateDay(year, monthOfYear, dayOfMonth + 1);
                        String[] days = {day_pre, day_now, day_next};
                        text_day_picker.setDisplayedValues(days);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendar_select_act.this, Calendar_info_act.class);

                intent.putExtra("year", year_input + "");
                intent.putExtra("month", month_input + "");
                intent.putExtra("date", day_input + "");
                intent.putExtra("hour", hour_input + "");
                intent.putExtra("minute", minute_input + "");

                intent.putExtra("endYear", year_input_end + "");
                intent.putExtra("endMonth", month_input_end + "");
                intent.putExtra("endDate", day_input_end + "");

                intent.putExtra("endHour", hour_input_end + "");
                intent.putExtra("endMinute", minute_input_end + "");
                intent.putExtra("page", "1");

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                finish();
            }
        });

    }

    //피커에 타임 세팅하는 메소드.
    private void timeSetting() {
        timePicker.setIs24HourView(true);

        year_input = datePicker.getYear();
        month_input = datePicker.getMonth();
        day_input = datePicker.getDayOfMonth();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour_input = timePicker.getHour();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            minute_input = timePicker.getMinute();
        }

        year_input_end = datePicker.getYear();
        month_input_end = datePicker.getMonth();
        day_input_end = datePicker.getDayOfMonth();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour_input_end = timePicker.getHour();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            minute_input_end = timePicker.getMinute();
        }

        String hour_form = hour_input + "";
        String min_form = minute_input + "";

        if (hour_input < 10)
            hour_form = "0" + hour_form;

        if (minute_input < 10)
            min_form = "0" + min_form;

        String month_form = String.valueOf(month_input + 1);
        String date_form = String.valueOf(day_input);
        String day = getDateDay(year_input, month_input, day_input);

        String year_form = String.valueOf(year_input);
        year_form = year_form.substring(2, 4);

        if ((month_input + 1) < 10)
            month_form = "0" + month_form;

        if ((day_input) < 10)
            date_form = "0" + date_form;


        startTime.setText(hour_form + " : " + min_form);
        startDay.setText(year_form + "." + month_form + "." + date_form + "(" + day + ")");
        endDay.setText(year_form + "." + month_form + "." + date_form + "(" + day + ")");
        endTime.setText(hour_form + " : " + min_form);


    }

    //일짜로부터 요일 받아오는 메소드.
    public String getDateDay(int year, int month, int date) {

        String day = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }


        return day;
    }

    //요일 지정.
    private void setTestDay() {

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int date = datePicker.getDayOfMonth();

        String day_pre = getDateDay(year, month, date - 1);
        String day_now = getDateDay(year, month, date);
        String day_next = getDateDay(year, month, date + 1);
        String[] days = {day_pre, day_now, day_next};

        text_day_picker.setMinValue(0);
        text_day_picker.setMaxValue(days.length - 1);
        text_day_picker.setValue(1);
        text_day_picker.setDisplayedValues(days);
        text_day_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {

            }
        });
    }

}
