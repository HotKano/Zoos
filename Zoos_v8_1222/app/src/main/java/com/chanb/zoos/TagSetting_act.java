package com.chanb.zoos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class TagSetting_act extends AppCompatActivity {

    ImageButton back, tag_fixer;
    GlobalApplication globalApplication;
    EditText tag_finder;
    ArrayList<String> tagList;
    TextView tag_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_setting_act);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setting() {
        back = findViewById(R.id.tag_setting_backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tagList = new ArrayList<String>();
        tag_finder = findViewById(R.id.tag_finder);
        tag_fixer = findViewById(R.id.tag_fix_btn);
        tag_show = findViewById(R.id.tag_show_view);
        tag_fixer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = tag_finder.getText().toString();
                tagList.add(tag);
                tag_finder.setText("");

                for (int i = 0; i < tagList.size(); i++) {
                    String test = tagList.get(i);
                    Log.d("tagList", i + "번째 값 : " + test);
                    tag_show.append(test + " ");
                }
            }
        });


        //system bar color
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
    }

    @Override
    public void onBackPressed() {
        String tag = tag_finder.getText().toString();
        Intent intent = new Intent(TagSetting_act.this, More_care_act.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("tag", tag);
        startActivity(intent);
        finish();
    }
}
