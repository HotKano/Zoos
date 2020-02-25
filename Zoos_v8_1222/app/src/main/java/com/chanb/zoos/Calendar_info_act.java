package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class Calendar_info_act extends AppCompatActivity {

    GlobalApplication globalApplication;
    ImageView back_btn;
    RequestQueue requestQueue;
    String id, petNo, petName, url, nickname, petRace;
    LinearLayout timeLayout;
    TextView start, end, start_time_view, end_time_view, toDo;
    Button submit_info;
    String title, type;
    ImageView petProfile;
    EditText memo;
    String year_input, month_input, day_input, hour_input, minute_input;
    String year_input_end, month_input_end, day_input_end, hour_input_end, minute_input_end;
    int data;

    ArrayList<RepeatItem> repeat_array;

    TextView wait_info, repeat_text, preload_text;
    ArrayList<PreloadItem> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_info_act);
        try {
            setting();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        repeat_array = new ArrayList<>();
        back_btn = findViewById(R.id.backButton_calendar_info);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(this);
        wait_info = findViewById(R.id.waiting_info);
        memo = findViewById(R.id.calendar_memo_info);
        start = findViewById(R.id.startDate_info);
        start_time_view = findViewById(R.id.startTime_info);
        end_time_view = findViewById(R.id.endTime_info);
        end = findViewById(R.id.endDate_info);
        toDo = findViewById(R.id.petName_Todo_info);
        petProfile = findViewById(R.id.petProfile_info);

        //시간 선택하는 부붑
        timeLayout = findViewById(R.id.test_calendar);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendar_info_act.this, Calendar_select_act.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        id = getIntent().getStringExtra("id");
        petNo = getIntent().getStringExtra("petNo");
        title = getIntent().getStringExtra("title");
        petName = getIntent().getStringExtra("PetName");
        petRace = getIntent().getStringExtra("petRace");
        url = getIntent().getStringExtra("petProfile");
        toDo.setText(petName + " - " + title);

        submit_info = findViewById(R.id.calendar_info_submit);
        submit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo_string = memo.getText().toString();
                Intent intent = new Intent(Calendar_info_act.this, Calendar_upload_act.class);
                intent.putExtra("id", id);

                intent.putExtra("title", title);
                intent.putExtra("content", memo_string);
                intent.putExtra("petNo", petNo);
                intent.putExtra("petRace", petRace);

                intent.putExtra("year", year_input);
                intent.putExtra("month", month_input);
                intent.putExtra("date", day_input);
                intent.putExtra("hour", hour_input);
                intent.putExtra("minute", minute_input);

                intent.putExtra("yearEnd", year_input_end);
                intent.putExtra("monthEnd", month_input_end);
                intent.putExtra("dateEnd", day_input_end);
                intent.putExtra("hourEnd", hour_input_end);
                intent.putExtra("minuteEnd", minute_input_end);

                //알림 사전 알림 관련 ArrayList getType, getData.
                intent.putExtra("type_array", temp);
                intent.putExtra("uploadFrom", "info");

                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(Calendar_info_act.this, "잘못된 접근입니다. 접속 유무를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(title)) {
                    Toast.makeText(Calendar_info_act.this, "잘못된 접근입니다. 접속 유무를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(year_input)) {
                    Toast.makeText(Calendar_info_act.this, "시작 시간 및 종료 시간을 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(year_input_end)) {
                    Toast.makeText(Calendar_info_act.this, "종료 시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                    finish();
                }

            }
        });

        //알람 먼저 알림 액트
        preload_text = findViewById(R.id.preload_setting);
        preload_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendar_info_act.this, AlarmPreload_act.class);
                startActivity(intent);
            }
        });


        String url_connection = "http://133.186.135.41/zozoPetProfile/" + url;

        Glide.with(this)
                .load(url_connection)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .skipMemoryCache(true)
                        .dontTransform()
                        .centerCrop()
                        .circleCrop()
                ).into(petProfile);
    }

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

    private void init() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        String startDay = getDateDay(year, month - 1, date);
        String yearForm = String.valueOf(year);
        String monthForm, dateForm, hourForm, minForm;

        yearForm = yearForm.substring(2, 4);

        if (month < 10)
            monthForm = "0" + String.valueOf(month);
        else
            monthForm = String.valueOf(month);

        if (date < 10)
            dateForm = "0" + String.valueOf(date);
        else
            dateForm = String.valueOf(date);

        if (hour < 10)
            hourForm = "0" + String.valueOf(hour);
        else
            hourForm = String.valueOf(hour);

        if (min < 10)
            minForm = "0" + String.valueOf(min);
        else
            minForm = String.valueOf(min);

        start.setText(yearForm + "." + monthForm + "." + dateForm + "(" + startDay + ")");
        start_time_view.setText(hourForm + ":" + minForm);

    }

    // date to frag.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            try {
                String page = intent.getStringExtra("page");
                if (page.equals("1")) {

                    year_input = intent.getStringExtra("year");
                    month_input = intent.getStringExtra("month");
                    day_input = intent.getStringExtra("date");
                    hour_input = intent.getStringExtra("hour");
                    minute_input = intent.getStringExtra("minute");

                    year_input_end = intent.getStringExtra("endYear");
                    month_input_end = intent.getStringExtra("endMonth");
                    day_input_end = intent.getStringExtra("endDate");
                    hour_input_end = intent.getStringExtra("endHour");
                    minute_input_end = intent.getStringExtra("endMinute");

                    int year = Integer.parseInt(year_input);
                    int month = Integer.parseInt(month_input);
                    int date = Integer.parseInt(day_input);
                    int hour = Integer.parseInt(hour_input);
                    int min = Integer.parseInt(minute_input);

                    int year_end = Integer.parseInt(year_input_end);
                    int month_end = Integer.parseInt(month_input_end);
                    int date_end = Integer.parseInt(day_input_end);
                    int hour_end = Integer.parseInt(hour_input_end);
                    int min_end = Integer.parseInt(minute_input_end);


                    String startDay = getDateDay(year, month, date);
                    String endDay = getDateDay(year_end, month_end, date_end);

                    String yearForm = year_input.substring(2, 4);
                    String monthForm = String.valueOf(month + 1);
                    String dateForm = String.valueOf(date);
                    String hourForm = String.valueOf(hour);
                    String minForm = String.valueOf(min);

                    if (month + 1 < 10)
                        monthForm = "0" + monthForm;

                    if (date < 10)
                        dateForm = "0" + dateForm;

                    if (hour < 10)
                        hourForm = "0" + hourForm;

                    if (min < 10)
                        minForm = "0" + minForm;

                    String yearForm_end = year_input_end.substring(2, 4);
                    String monthForm_end = String.valueOf(month_end + 1);
                    String dateForm_end = String.valueOf(date_end);
                    String hourForm_end = String.valueOf(hour_end);
                    String minForm_end = String.valueOf(min_end);

                    if (month_end + 1 < 10)
                        monthForm_end = "0" + monthForm_end;

                    if (date_end < 10)
                        dateForm_end = "0" + dateForm_end;

                    if (hour_end < 10)
                        hourForm_end = "0" + hourForm_end;

                    if (min_end < 10)
                        minForm_end = "0" + minForm_end;


                    start.setText(yearForm + "." + monthForm + "." + dateForm + "(" + startDay + ")");
                    start_time_view.setText(hourForm + ":" + minForm);

                    end.setText(yearForm_end + "." + monthForm_end + "." + dateForm_end + "(" + endDay + ")");
                    end_time_view.setText(hourForm_end + ":" + minForm_end);

                    end.setVisibility(View.VISIBLE);
                    end_time_view.setVisibility(View.VISIBLE);
                    wait_info.setVisibility(View.GONE);

                } else if (page.equals("2")) {
                    String type_form = intent.getStringExtra("type");
                    temp = (ArrayList<PreloadItem>) intent.getSerializableExtra("type_array");
                    int data_form = intent.getIntExtra("data", -1);
                    type = type_form;
                    data = data_form;

                    ArrayList<String> strings = new ArrayList<>();

                    if (!temp.isEmpty()) {

                        for (int i = 0; i < temp.size(); i++) {
                            String data_form_array = temp.get(i).getData();
                            String type_form_array = temp.get(i).getType();
                            String temp_type = null;
                            switch (type_form_array) {
                                case "min":
                                    temp_type = "분 전";
                                    break;
                                case "hour":
                                    temp_type = "시간 전";
                                    break;
                                case "date":
                                    temp_type = "일 전";
                                    break;
                            }

                            //preload_text.setText(data_form_array + "" + temp_type + " ");
                            String textViewForm = data_form_array + "" + temp_type + " ";
                            strings.add(textViewForm);
                        }

                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < strings.size(); i++) {
                            if (i == strings.size() - 1) {
                                sb.append(strings.get(i));
                            } else {
                                sb.append(strings.get(i)).append(", ");
                            }

                        }

                        preload_text.setText(sb.toString());

                    }

                    switch (type_form) {
                        case "min":
                            type_form = "분 전";
                            break;
                        case "hour":
                            type_form = "시간 전";
                            break;
                        case "date":
                            type_form = "일 전";
                            break;
                    }
                    preload_text.setText(data_form + "" + type_form);

                } else if (page.equals("3")) {
                    String data_form = intent.getStringExtra("data");
                    StringBuffer sb = new StringBuffer();
                    String type_form = null;


                    if (repeat_array.size() > 0) {

                        type_form = repeat_array.get(0).getType();

                        if (type_form.equals("week")) {
                            for (int i = 0; i < repeat_array.size(); i++) {
                                int data = repeat_array.get(i).getData();
                                type_form = repeat_array.get(i).getType();
                                String temp_data = null;
                                switch (data) {
                                    case 1:
                                        temp_data = "일";
                                        break;
                                    case 2:
                                        temp_data = "월";
                                        break;
                                    case 3:
                                        temp_data = "화";
                                        break;
                                    case 4:
                                        temp_data = "수";
                                        break;
                                    case 5:
                                        temp_data = "목";
                                        break;
                                    case 6:
                                        temp_data = "금";
                                        break;
                                    case 7:
                                        temp_data = "토";
                                        break;
                                }
                                sb.append(temp_data);
                            }
                        }

                        if (type_form.equals("month")) {
                            for (int i = 0; i < repeat_array.size(); i++) {
                                int data = repeat_array.get(i).getData();
                                type_form = repeat_array.get(i).getType();
                                String temp_data = null;
                                switch (data) {
                                    case 1:
                                        temp_data = "30일";
                                        break;
                                    case 2:
                                        temp_data = "마지막 금요일";

                                        break;
                                    case 3:
                                        temp_data = "마지막 날";
                                        break;
                                }
                                sb.append(temp_data);
                            }
                        }

                        if (type_form.equals("year")) {
                            for (int i = 0; i < repeat_array.size(); i++) {
                                int data = repeat_array.get(i).getData();
                                type_form = repeat_array.get(i).getType();
                                String temp_data = null;
                                switch (data) {
                                    case 1:
                                        temp_data = "30일";
                                        break;
                                    case 2:
                                        temp_data = "마지막 금요일";

                                        break;
                                    case 3:
                                        temp_data = "마지막 날";
                                        break;
                                }
                                sb.append(temp_data);
                            }
                        }


                        repeat_text.setText(data_form + " " + sb);
                    } else {
                        Toast.makeText(this, "data error", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "dataError", Toast.LENGTH_SHORT).show();
        }

    }


}
