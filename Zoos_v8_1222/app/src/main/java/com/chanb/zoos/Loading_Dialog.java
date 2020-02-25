package com.chanb.zoos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Objects;

public class Loading_Dialog extends Dialog {

    ImageView test;
    Animation in, out;

    public Loading_Dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loading_dialog);
        test = findViewById(R.id.testImg);
        test.setImageResource(R.drawable.dog);
        in = AnimationUtils.loadAnimation(
                getContext(), R.anim.in);
        out = AnimationUtils.loadAnimation(
                getContext(), R.anim.out);
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
            }
        }, 2500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

}
