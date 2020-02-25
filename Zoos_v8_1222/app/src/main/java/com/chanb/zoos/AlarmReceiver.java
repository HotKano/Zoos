package com.chanb.zoos;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AlarmManager alarmManager;
        NotificationManager nm;
        String TAG = "alarm";


        if (bundle != null) {
            String title = bundle.getString("title");
            String type = bundle.getString("type");
            int temp = title.indexOf("산책");

            String[] msg_array = new String[6];
            String[] msg_title = new String[6];

            String msg_title1 = "쥔님...";
            String msg1 = title + " 해주셔야죠...";

            String msg_title2 = "똑똑..?";
            String msg2 = title + " 도 안해주면서 뭘 나를 키우시겠다고.. 저 쉬운 애 아니거든요";

            String msg_title3 = "흠...";
            String msg3 = "바쁜 거 아는데 " + title + " 은 해줘야쥬...";

            String msg_title4 = "자기야...";
            String msg4 = "놀랐쥬? " + "저 " + title + " 좀 챙겨주세유";

            String msg_title5 = "너는 지금 뭐해 자니";
            String msg5 = title + " 빨리 좀 해줘유";

            String msg_title6 = "멍멍냐옹왈멍!";
            String msg6 = "(까먹은 거 없어유?)";

            String msg_title7 = "끙..";
            String msg7 = "쥔님 저 배변봉투 + 목줄 필수템인 거 아시쥬?";

            msg_array[0] = msg1;
            msg_array[1] = msg2;
            msg_array[2] = msg3;
            msg_array[3] = msg4;
            msg_array[4] = msg5;
            msg_array[5] = msg6;

            msg_title[0] = msg_title1;
            msg_title[1] = msg_title2;
            msg_title[2] = msg_title3;
            msg_title[3] = msg_title4;
            msg_title[4] = msg_title5;
            msg_title[5] = msg_title6;


            Random r = new Random();
            int number = r.nextInt(msg_array.length);


            alarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (!TextUtils.isEmpty(type)) {
                if (type.equals("todayCareList")) {
                    Intent intent2 = new Intent(context, Calendar_Diary_Today_Info_Act.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent2, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "default3");
                    builder.setSmallIcon(R.drawable.onlylogo);
                    builder.setContentTitle("오늘의 케어 소식");
                    builder.setContentText(title);
                    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    builder.setContentIntent(pendingIntent1);
                    nm.notify(0, builder.build());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);

                    Intent intent3 = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 998998, intent3, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d(TAG, "alarm active");

                } else if (type.equals("limit")) {
                    Log.d(TAG, "limit act");
                    int year_end = intent.getIntExtra("end_year", -1);
                    int month_end = intent.getIntExtra("end_month", -1);
                    int date_end = intent.getIntExtra("end_date", -1);
                    int hour_end = intent.getIntExtra("end_hour", -1);
                    int min_end = intent.getIntExtra("end_min", -1);
                    String code = intent.getStringExtra("code");
                    int code_temp = Integer.valueOf(code);

                    Calendar target_calendar = Calendar.getInstance();
                    target_calendar.set(year_end, month_end, date_end);
                    target_calendar.set(Calendar.HOUR_OF_DAY, hour_end);
                    target_calendar.set(Calendar.MINUTE, min_end);

                    long now = System.currentTimeMillis();
                    long target_time = target_calendar.getTimeInMillis();

                    Intent intent3 = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, code_temp, intent3, 0);

                    if (now > target_time) {
                        alarmManager.cancel(pendingIntent);
                        Log.d(TAG, "alarm active limit finish now : " + now + " target : " + target_time);
                    } else {
                        Intent intent2 = new Intent(context, Calendar_Diary_Today_Info_Act.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, code_temp, intent2, 0);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "default3");
                        builder.setSmallIcon(R.drawable.onlylogo);
                        builder.setAutoCancel(true);
                        if (temp != -1) {
                            builder.setContentText(msg_title7);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg_title7 + "\n" + msg7));
                        } else {
                            builder.setContentText(msg_title[number]);
                            builder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg_title[number] + "\n" + msg_array[number]));
                        }

                        builder.setContentIntent(pendingIntent1);
                        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        nm.notify(code_temp, builder.build());

                        //반복 알림을 위한 부분.
/*
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 1);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);*/
                        Log.d(TAG, "alarm active limit Doing now : " + now + " target : " + target_time);
                    }
                } else if (type.equals("once")) {
                    String code = intent.getStringExtra("code");
                    int code_temp = Integer.valueOf(code);

                    Intent intent2 = new Intent(context, Calendar_Diary_Today_Info_Act.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent1 = PendingIntent.getActivity(context, code_temp, intent2, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "default3");
                    builder.setSmallIcon(R.drawable.onlylogo);
                    builder.setContentTitle(title);
                    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    builder.setContentIntent(pendingIntent1);
                    nm.notify(code_temp, builder.build());
                    Log.d(TAG, "alarm active once");
                }

            }


        }
    }

}
