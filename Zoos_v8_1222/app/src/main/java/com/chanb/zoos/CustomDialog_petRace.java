package com.chanb.zoos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomDialog_petRace extends Dialog {

    TextView text1, text2, text3;
    String petRace, petGender, petNeutral;
    ImageButton dog, cat, etc;
    Button submit, cancel;
    String kind;

    public CustomDialog_petRace(@NonNull Context context, String kind) {
        super(context);
        this.kind = kind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.welcome);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {

        final int startColor = getContext().getResources().getColor(R.color.start_with_phone_color);
        final int normalColor = getContext().getResources().getColor(R.color.sns_act_color);

        petRace = "개";
        petGender = "1";
        petNeutral = "1";

        dog = findViewById(R.id.petRace_Dog);
        text1 = findViewById(R.id.text1);

        cat = findViewById(R.id.petRace_Cat);
        text2 = findViewById(R.id.text2);

        etc = findViewById(R.id.petRace_ETC);
        text3 = findViewById(R.id.text3);

        if (!kind.equals("race")) {
            etc.setVisibility(View.GONE);
            text3.setVisibility(View.GONE);

            if (kind.equals("gender")) {
                text1.setText("수컷");
                text2.setText("암컷");
            }

            if (kind.equals("neutral")) {
                text1.setText("실시");
                text2.setText("미실시");
            }


        }


        dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petRace = "개";
                petGender = "1";
                petNeutral = "1";

                dog.setImageResource(R.drawable.check);
                cat.setImageResource(R.drawable.check_2);
                etc.setImageResource(R.drawable.check_2);

                text1.setTextColor(startColor);
                text2.setTextColor(normalColor);
                text3.setTextColor(normalColor);

            }
        });

        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petRace = "고양이";
                petGender = "0";
                petNeutral = "0";

                dog.setImageResource(R.drawable.check_2);
                cat.setImageResource(R.drawable.check);
                etc.setImageResource(R.drawable.check_2);

                text1.setTextColor(normalColor);
                text2.setTextColor(startColor);
                text3.setTextColor(normalColor);

            }
        });

        etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petRace = "기타";
                dog.setImageResource(R.drawable.check_2);
                cat.setImageResource(R.drawable.check_2);
                etc.setImageResource(R.drawable.check);

                text1.setTextColor(normalColor);
                text2.setTextColor(normalColor);
                text3.setTextColor(startColor);

            }
        });

        submit = findViewById(R.id.petRace_Submit);
        cancel = findViewById(R.id.petRace_Cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (kind) {
                    case "race":
                        setPetRace(petRace);
                        break;
                    case "gender":
                        setPetGender(petGender);
                        break;

                    case "neutral":
                        setPetNeutral(petNeutral);
                        break;
                }

                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public String getPetRace() {
        return petRace;
    }

    public void setPetRace(String petRace) {
        this.petRace = petRace;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetNeutral() {
        return petGender;
    }

    public void setPetNeutral(String petNeutral) {
        this.petNeutral = petNeutral;
    }


}
