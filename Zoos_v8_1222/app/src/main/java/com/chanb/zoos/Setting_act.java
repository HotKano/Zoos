package com.chanb.zoos;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chanb.zoos.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class Setting_act extends AppCompatActivity {
    GlobalApplication globalApplication;
    TextView settingView, deleteView, logoutView;
    String id;
    FirebaseAuth firebaseAuth;
    Database database;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_act);

        try {
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        //db init
        String dbName = "UserDatabase_Zoos";
        int dataBaseVersion = 1;
        database = new Database(this, dbName, null, dataBaseVersion);
        database.init();

        id = getIntent().getStringExtra("id");
        firebaseAuth = FirebaseAuth.getInstance();
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);

        settingView = findViewById(R.id.alarm_setting);
        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_act.this, AlarmSettingAct.class);
                intent.putExtra("id", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        deleteView = findViewById(R.id.delete_setting);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = findViewById(R.id.setting_frame);
                Snackbar snackbar = Snackbar
                        .make(view, "정말로 탈퇴하시겠습니까?", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .setAction("예", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Setting_act.this, Logo_act.class);
                                startActivity(intent);
                                firebaseAuth.signOut();
                                fireBaseDelete();
                                database.delete("MEMBERINFO");
                                finish();
                                Main_act._Main_act.finish();
                                Story_act._story_act.finish();
                            }
                        });
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start_with_phone_color));
                snackbar.show();
            }
        });

        logoutView = findViewById(R.id.setting_logout);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_act.this, Logo_act.class);
                startActivity(intent);
                firebaseAuth.signOut();
                database.delete("MEMBERINFO");
                finish();
                Main_act._Main_act.finish();
                Story_act._story_act.finish();
                kakao_logout();
            }
        });

        backButton = findViewById(R.id.backbutton_setting);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void kakao_logout() {
        if (UserManagement.getInstance() != null) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {

                }
            });
        }
    }

    private void fireBaseDelete() {
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.delete();
    }

}
