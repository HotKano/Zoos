package com.chanb.zoos;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmSettingAct extends AppCompatActivity {

    GlobalApplication globalApplication;
    Switch sw;
    Database database;
    ImageButton backButton;
    TimePicker timePicker;
    String time, no, id, hour, minute, code;
    Button submit_setting;
    RequestQueue requestQueue;
    AlarmManager alarmManager;
    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting_act);

        try {
            setting();
            setting_notification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        timePicker = findViewById(R.id.timePicker_setting);
        timePicker.setIs24HourView(true);
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();
        String[] temp = database.select("MEMBERINFO");

        String[] timeTemp = new String[2];
        if (time != null) {
            timeTemp = time.split(":");
            timePicker.setHour(Integer.valueOf(timeTemp[0]));
            timePicker.setMinute(Integer.valueOf(timeTemp[1]));
        }

        submit_setting = findViewById(R.id.submit_setting);

        submit_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
                onBackPressed();
            }
        });

        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        String alarmCheck = temp[3];
        sw = findViewById(R.id.switch_setting);

        if (alarmCheck.equals("true")) {
            sw.setChecked(true);
            //브로드 캐스트 등록.
            ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            sw.setChecked(false);
            //브로드 캐스트 등록 해제.
            ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
            PackageManager pm = this.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckedChanged(sw.isChecked());
            }
        });

        backButton = findViewById(R.id.backButton_care_setting);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setting_notification() {

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //오레오에서는 알림채널을 매니저에 생성해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            manager.createNotificationChannel(new NotificationChannel("default3", "기본채널", NotificationManager.IMPORTANCE_HIGH));
        } else {
            manager.createNotificationChannel(new NotificationChannel("default3", "기본채널", NotificationManager.IMPORTANCE_HIGH));
            NotificationChannel channel = new NotificationChannel("Zoos_channel3", "기본채널", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("기본채널입니다.");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
        }

    }

    public void onCheckedChanged(boolean isChecked) {

        if (isChecked) {
            database.updateAlarm("MEMBERINFO", "true");

            //브로드 캐스트 등록.
            ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } else {
            try {
                database.updateAlarm("MEMBERINFO", "false");

                //브로드 캐스트 등록 해제.
                ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
                PackageManager pm = this.getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //year, month, date, hour_form, min_form, title, alarm_id
    /*public void alarmEditor(int hour, int min) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        Intent intent_time = new Intent(this, AlarmReceiver2.class);
        intent_time.putExtra("title", "알람쥬 오늘의 케어리스트");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent_time, 0);

        long day = 24 * 60 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), day, pendingIntent);
        Toast.makeText(this, "알람 시간이 수정 되었습니다.", Toast.LENGTH_SHORT).show();
    }*/

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);

        int hour = timePicker.getHour();
        int min = timePicker.getMinute();

        intent.putExtra("title", "오늘의 케어리스트 도착입니다.");
        intent.putExtra("type", "todayCareList");
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        long day = 24 * 60 * 60 * 1000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 998998, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
