package com.chanb.zoos;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.kakao.auth.KakaoSDK;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

import java.util.Calendar;

public class GlobalApplication extends Application { // 카카오 로그인을 위한 글로벌 세션
    private static GlobalApplication instance;

    public static GlobalApplication getGlobalApplicationContext() {

        if (instance == null) {

            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");

        }

        return instance;

    }


    @Override

    public void onCreate() {

        super.onCreate();
        instance = this;


        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    // SystemBar Layout
    // 스테이터스 바 색상에 안묻히겠끔하는 역할. 검정으로 설정할떄는 역으로 풀어야함.
    public void getWindow(Activity act) {
        act.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public String getDateDay(int year, int month, int date) {


        String day = "";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, date);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }


        return day;
    }


    @Override

    public void onTerminate() {

        super.onTerminate();

        instance = null;
    }

}

