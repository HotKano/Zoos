package com.chanb.zoos;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Loading_act extends AppCompatActivity {

    ImageView test;
    Animation in, out;
    String id, nickname;
    RequestQueue requestQueue;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_act);
        requestQueue = Volley.newRequestQueue(this);
        test = findViewById(R.id.testImg);
        test.setImageResource(R.drawable.dog);
        try {
            id = getIntent().getStringExtra("id");
            type = getIntent().getStringExtra("type");
            nickname = getIntent().getStringExtra("nickname");
        } catch (Exception e) {
            e.printStackTrace();
        }

        in = AnimationUtils.loadAnimation(
                this, R.anim.in);
        out = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.out);

        startAnim();
    }

    private void startAnim() {
        //강아지 고양이 뼈 태그
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.startAnimation(out);
            }
        }, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.setImageResource(R.drawable.cat);
                test.startAnimation(in);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.startAnimation(out);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.setImageResource(R.drawable.born);
                test.startAnimation(in);
            }
        }, 1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.startAnimation(out);
            }
        }, 2000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test.setImageResource(R.drawable.dogtag);
                test.startAnimation(in);
                if (type.equals("main")) {
                    Intent intent = new Intent(Loading_act.this, Main_act.class);
                    intent.putExtra("id", id);
                    intent.putExtra("nickname", nickname);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Loading_act.this, Save_act.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2500);

    }
}
