package com.chanb.zoos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


//today care calling later custom view
public class CustomDialog_Main extends BottomSheetDialog {

    TextView min10, min30, min60, min120;
    Button submit;
    String TAG = "HappyNaru";
    int min;
    LinearLayout buttonZone;

    public CustomDialog_Main(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        buttonZone = findViewById(R.id.button_zone_dialog);
        min10 = findViewById(R.id.later_10_min);
        min30 = findViewById(R.id.later_30_min);
        min60 = findViewById(R.id.later_60_min);
        min120 = findViewById(R.id.later_120_min);
        submit = findViewById(R.id.custom_dialog_submit);

        final ColorStateList textColor = min10.getTextColors();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        min10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min = 0;
                min = 10;
                Log.d(TAG, min + "dataFromDialog");


                for (int i = 0; i < 4; i++) {
                    Button test = (Button) buttonZone.getChildAt(i);
                    test.setBackgroundResource(R.drawable.round_gray);
                    test.setTextColor(textColor);
                }

                min10.setBackgroundResource(R.drawable.round);
                min10.setTextColor(Color.WHITE);


            }
        });

        min30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min = 0;
                min = 30;
                Log.d(TAG, min + "dataFromDialog");

                for (int i = 0; i < 4; i++) {
                    Button test = (Button) buttonZone.getChildAt(i);
                    test.setBackgroundResource(R.drawable.round_gray);
                    test.setTextColor(textColor);
                }

                min30.setBackgroundResource(R.drawable.round);
                min30.setTextColor(Color.WHITE);


            }
        });

        min60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min = 0;
                min = 60;
                Log.d(TAG, min + "dataFromDialog");

                for (int i = 0; i < 4; i++) {
                    Button test = (Button) buttonZone.getChildAt(i);
                    test.setBackgroundResource(R.drawable.round_gray);
                    test.setTextColor(textColor);
                }

                min60.setBackgroundResource(R.drawable.round);
                min60.setTextColor(Color.WHITE);
            }
        });

        min120.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min = 0;
                min = 120;
                Log.d(TAG, min + "dataFromDialog");

                for (int i = 0; i < 4; i++) {
                    Button test = (Button) buttonZone.getChildAt(i);
                    test.setBackgroundResource(R.drawable.round_gray);
                    test.setTextColor(textColor);
                }

                min120.setBackgroundResource(R.drawable.round);
                min120.setTextColor(Color.WHITE);
            }
        });
    }

    public int getMin() {
        return min;
    }

    //width를 시작할 때 정의하지 않으면 가득 차지 않는다. 앞으로도 주의할 것.
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT /*our width*/, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}