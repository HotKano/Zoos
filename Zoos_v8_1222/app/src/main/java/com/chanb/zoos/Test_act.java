package com.chanb.zoos;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chanb.zoos.utils.Database;


import java.util.Calendar;

public class Test_act extends AppCompatActivity {

    TextView textView;
    RequestQueue requestQueue;
    String text_number;
    String id, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.chanb.zoos.R.layout.test_act);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        int time = Calendar.HOUR_OF_DAY;
        int minute = Calendar.MINUTE;
        time = calendar.get(time);
        minute = calendar.get(minute);


        try {
            kakao_share();
            Intent intent = getIntent();
            text_number = intent.getStringExtra("no");
            String type = intent.getStringExtra("type");
            Log.d("Test_act", text_number);

            if (!text_number.equals("-1")) {
                //viewConnect();
            }

            textView = findViewById(R.id.text_test);
            textView.setText("Type:" + type + "의 " + text_number + "번째 글 받음" + time + ":" + minute);
        } catch (Exception e) {

        }

    }

    private void kakao_share() {
        try {
            String[] temp = new String[2];
            String dbName = "UserDatabase_Zoos";
            int dataBaseVersion = 1;
            Database database = new Database(this, dbName, null, dataBaseVersion);
            database.init();
            temp = database.select("MEMBERINFO");
            Intent intent = getIntent();

            if (intent != null) {
                Uri uri = intent.getData();
                String no = uri.getQueryParameter("no");
                String kind = uri.getQueryParameter("kind");
                String target_id = uri.getQueryParameter("target_id");
                if (uri != null) {
                    id = temp[0];
                    nickname = temp[2];

                    if (kind.equals("today")) {
                        Intent intent1 = new Intent(Test_act.this, Calendar_Diary_Today_Info_Act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("id", target_id);
                        intent1.putExtra("nickname", nickname);
                        intent1.putExtra("kind", "shared");
                        startActivity(intent1);
                        finish();
                    }

                    if (kind.equals("content")) {
                        Intent intent1 = new Intent(Test_act.this, Content_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("id", id);
                        intent1.putExtra("nickname", nickname);
                        intent1.putExtra("no", no);
                        startActivity(intent1);
                        finish();
                    }

                    if (kind.equals("care")) {
                        Intent intent1 = new Intent(Test_act.this, Care_Content_Act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("id", id);
                        intent1.putExtra("nickname", nickname);
                        intent1.putExtra("no", no);
                        startActivity(intent1);
                        finish();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
