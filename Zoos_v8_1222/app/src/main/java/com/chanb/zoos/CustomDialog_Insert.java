package com.chanb.zoos;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog_Insert extends Dialog {

    private String dataKind, dataAge, dataPetNumber;
    private EditText editText, editText_number;
    String kind;

    public CustomDialog_Insert(@NonNull Context context, String kind) {
        super(context);
        this.kind = kind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.insert_story_custom_dialog);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setting() {
        Button submit_insert = findViewById(R.id.petInsert_Submit);
        Button cancel_insert = findViewById(R.id.petInsert_Cancel);
        editText = findViewById(R.id.info_insert);
        editText_number = findViewById(R.id.info_insert_number);
        editText.setText("");
        editText_number.setText("");

        if (kind.equals("kind") || kind.equals("number")) {
            editText.setVisibility(View.VISIBLE);
            editText_number.setVisibility(View.GONE);
        } else if (kind.equals("age") || kind.equals("weight")) {
            editText.setVisibility(View.GONE);
            editText_number.setVisibility(View.VISIBLE);
        }

        submit_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                String number = editText_number.getText().toString();

                switch (kind) {
                    case "kind":
                        setKind(text);
                        break;
                    case "age":
                        setAge(number);
                        break;

                    case "number":
                        setNumber(text);
                        break;
                }

                dismiss();
            }
        });

        cancel_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }


    public String getKind() {
        return dataKind;
    }

    public String getAge() {
        return dataAge;
    }

    public String getNumber() {
        return dataPetNumber;
    }

    public void setKind(String dataKind) {
        this.dataKind = dataKind;
    }

    public void setAge(String dataAge) {
        this.dataAge = dataAge;
    }

    public void setNumber(String dataPetNumber) {
        this.dataPetNumber = dataPetNumber;
    }

}
