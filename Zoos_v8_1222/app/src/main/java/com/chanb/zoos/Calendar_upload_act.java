package com.chanb.zoos;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Calendar_upload_act extends AppCompatActivity {

    String id, nickname, year, month, date, hour, minute, title, content, petNo, type, petRace;
    String year_end, month_end, date_end, hour_end, minute_end;
    String uploadFrom;
    int year_insert, month_insert, date_insert, hour_insert, minute_insert, year_end_insert, month_end_insert, date_end_insert,
            hour_end_insert, minute_end_insert, data;
    RequestQueue requestQueue;
    AlarmManager alarmManager;
    NotificationManager manager;
    ArrayList<PreloadItem> preloadItems;
    ArrayList<RepeatItem> repeat;
    final int _id = (int) System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_upload_act);

        try {
            setting();
            viewConnect();
            setting_notification();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setting() {
        try {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            title = intent.getStringExtra("title");
            content = intent.getStringExtra("content");
            petNo = intent.getStringExtra("petNo");
            petRace = intent.getStringExtra("petRace");
            uploadFrom = intent.getStringExtra("uploadFrom");

            year = intent.getStringExtra("year");
            month = intent.getStringExtra("month");
            date = intent.getStringExtra("date");
            hour = intent.getStringExtra("hour");
            minute = intent.getStringExtra("minute");

            year_end = intent.getStringExtra("yearEnd");
            month_end = intent.getStringExtra("monthEnd");
            date_end = intent.getStringExtra("dateEnd");
            hour_end = intent.getStringExtra("hourEnd");
            minute_end = intent.getStringExtra("minuteEnd");

            //반복 정보 가진 array.
            repeat = new ArrayList<>();
            repeat = (ArrayList<RepeatItem>) intent.getSerializableExtra("data_array");

            //사전 알림 정보 가진 array.
            preloadItems = new ArrayList<>();
            preloadItems = (ArrayList<PreloadItem>) intent.getSerializableExtra("type_array");

            year_insert = Integer.valueOf(year);
            month_insert = Integer.valueOf(month);
            date_insert = Integer.valueOf(date);
            hour_insert = Integer.valueOf(hour);
            minute_insert = Integer.valueOf(minute);

            year_end_insert = Integer.valueOf(year_end);
            month_end_insert = Integer.valueOf(month_end);
            date_end_insert = Integer.valueOf(date_end);
            hour_end_insert = Integer.valueOf(hour_end);
            minute_end_insert = Integer.valueOf(minute_end);

            int month_form = Integer.valueOf(month) + 1;
            month = String.valueOf(month_form);

            int month_form_end = Integer.valueOf(month_end) + 1;
            month_end = String.valueOf(month_form_end);
        } catch (Exception e) {
            e.printStackTrace();
        }


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationChannel mChannel = new NotificationChannel("default3",
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(defaultSoundUri, attributes);

        }

    }

    public void viewConnect() {
        String viewUrl = "http://133.186.135.41/zozo_calendar_input.php";

        StringRequest request = new StringRequest(Request.Method.POST, viewUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    String state = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("C_upload_act", state + "****");
                    if (state.equals("sus1")) {
                        Intent intent = new Intent(Calendar_upload_act.this, Main_act.class);
                        intent.putExtra("page", "3");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        Log.d("onResponse", type + data);

                        //단순 푸쉬 알림을 위한 분기.
                        if (uploadFrom.equals("input"))
                            push(title);

                        if (preloadItems != null && uploadFrom.equals("info")) {
                            onTimeSet(year_insert, month_insert, date_insert, hour_insert, minute_insert, year_end_insert, month_end_insert, date_end_insert, hour_end_insert, minute_end_insert);
                            preOnTimeSet(year_insert, month_insert, date_insert, hour_insert, minute_insert, year_end_insert, month_end_insert, date_end_insert, hour_end_insert, minute_end_insert, preloadItems);
                            Toast.makeText(getApplicationContext(), "기억 완료! 설정한 시간에 알람쥬가 잊지 않고 알려드릴게요", Toast.LENGTH_SHORT).show();
                        } else if (uploadFrom.equals("info")) {
                            onTimeSet(year_insert, month_insert, date_insert, hour_insert, minute_insert, year_end_insert, month_end_insert, date_end_insert, hour_end_insert, minute_end_insert);
                            Toast.makeText(getApplicationContext(), "기억 완료! 설정한 시간에 알람쥬가 잊지 않고 알려드릴게요", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d("INTERNET", "sns_update is not working");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Calendar_upload_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                try {
                    parameters.put("id", id);
                    parameters.put("petNo", petNo);
                    parameters.put("title", title);
                    parameters.put("content", content);

                    parameters.put("year", year);
                    parameters.put("month", month);
                    parameters.put("date", date);
                    parameters.put("time", hour + ":" + minute);


                    int year_temp = Integer.valueOf(year);
                    int month_temp = Integer.valueOf(month) - 1;
                    int date_temp = Integer.valueOf(date);
                    int hour_temp = Integer.valueOf(hour);
                    int min_temp = Integer.valueOf(minute);
                    Calendar c = Calendar.getInstance();
                    c.set(year_temp, month_temp, date_temp, hour_temp, min_temp);
                    long code_temp = c.getTimeInMillis();
                    Log.d("TEST_DATA_TEMP", code_temp + "");

                    parameters.put("endYear", year_end);
                    parameters.put("endMonth", month_end);
                    parameters.put("endDate", date_end);
                    parameters.put("endTime", hour_end + ":" + minute_end);
                    parameters.put("code", String.valueOf(code_temp));
                    parameters.put("edit", "false");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return parameters;
            }
        };
        requestQueue.add(request);
    }

    public void onTimeSet(int year, int month, int date, int hourOfDay, int minute, int endYear, int endMonth, int endDate, int endHour, int endMinute) {
        //설정된 시간
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("end_year", endYear);
        intent.putExtra("end_month", endMonth);
        intent.putExtra("end_date", endDate);
        intent.putExtra("end_hour", endHour);
        intent.putExtra("end_min", endMinute);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("id", id);
        intent.putExtra("type", "limit");
        intent.putExtra("petRace", petRace);
        intent.putExtra("code", String.valueOf(_id));
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id, intent, 0);

        //설정된 시간에 기기가 슬립상태에서도 알람이 동작되도록 설정
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("min_receiver_alarm_mils", calendar.getTimeInMillis() + "캘린더.");
    }

    public void preOnTimeSet(int year, int month, int date, int hourOfDay, int minute, int endYear, int endMonth, int endDate, int endHour, int endMinute, ArrayList<PreloadItem> array) {

        int type_form = Calendar.MINUTE;

        for (int i = 0; i < array.size(); i++) {
            String type = array.get(i).getType();
            String data = array.get(i).getData();

            //type
            switch (type) {
                case "min":
                    type_form = Calendar.MINUTE;
                    break;
                case "hour":
                    type_form = Calendar.HOUR_OF_DAY;
                    break;
                case "date":
                    type_form = Calendar.DAY_OF_MONTH;
                    break;
            }

            int data_form = Integer.valueOf(data);
            //설정된 시간
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, month, date);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.add(type_form, -data_form);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("year", endYear);
            intent.putExtra("month", endMonth);
            intent.putExtra("date", endDate);
            intent.putExtra("hourOfDay", endHour);
            intent.putExtra("minute", endMinute);
            intent.putExtra("title", "알람쥬 미리 알림입니다. " + title + " 일정을 준비해주세요!");
            intent.putExtra("content", content);
            intent.putExtra("petRace", petRace);
            intent.putExtra("code", String.valueOf(_id));
            intent.putExtra("type", "once");

            //여기서 브로드캐스트나 서비스를 실행할 수 도 있다.
            //getActivity, getBroadcast, getService 등에 따라 액티비티, 브로드캐스트, 서비스 실행이 가능하다.
            int _id_preload = (_id + i);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), _id_preload, intent, PendingIntent.FLAG_ONE_SHOT);

            //설정된 시간에 기기가 슬립상태에서도 알람이 동작되도록 설정
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Log.d("min_receiver_pre", calendar.getTimeInMillis() + "캘린더.");
        }
    }

    private void push(String title) {

        Intent intent2 = new Intent(this, Story_act.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String msg = "지금 완료한 케어를 스토리에 추억으로 남겨보세요";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default3")
                .setSmallIcon(R.drawable.onlylogo)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(997997 /* ID of notification */, notificationBuilder.build());


    }

}
