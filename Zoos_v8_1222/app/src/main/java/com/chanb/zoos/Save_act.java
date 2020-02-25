package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.RequestQueue;

public class Save_act extends AppCompatActivity {

    RequestQueue requestQueue;
    String id;
    GlobalApplication globalApplication;
    FrameLayout frameLayout;
    Save_frag save_frag;
    Button back, all, care, story;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_act);

        try {
            setting();
            setFrag();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // 프레그먼트 세팅
    public void setFrag() {
        frameLayout = findViewById(R.id.container_save);
        save_frag = new Save_frag(); // 저장화면


        Intent intent = getIntent();
        if (intent != null) {
            save_frag.refresh_frag_save(intent);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container_save, save_frag).commit();

    }

    public void setting() {

        //상단 시스템 바
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);

        Intent intent = getIntent();
        if (intent != null)
            id = intent.getStringExtra("id");
        else
            id = "null";

        back = findViewById(R.id.backButton_save);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        all = findViewById(R.id.all_save);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_frag.gridConnect("all");
                all.setBackgroundResource(R.drawable.button_save);
                care.setBackgroundResource(R.drawable.button_save_gray);
                story.setBackgroundResource(R.drawable.button_save_gray);
            }
        });
        care = findViewById(R.id.care_save);
        care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_frag.gridConnect("notice");
                all.setBackgroundResource(R.drawable.button_save_gray);
                care.setBackgroundResource(R.drawable.button_save);
                story.setBackgroundResource(R.drawable.button_save_gray);
            }
        });
        story = findViewById(R.id.story_save);
        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_frag.gridConnect("story");
                all.setBackgroundResource(R.drawable.button_save_gray);
                care.setBackgroundResource(R.drawable.button_save_gray);
                story.setBackgroundResource(R.drawable.button_save);
            }
        });

    }

    //Frag 값 전달.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        save_frag.requestQueue = requestQueue;
        save_frag.id = id;
        save_frag.refresh_frag_save(intent);
    }


}
