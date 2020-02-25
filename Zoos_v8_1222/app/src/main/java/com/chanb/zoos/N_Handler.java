package com.chanb.zoos;

import android.os.Looper;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLoginHandler;


public class N_Handler extends OAuthLoginHandler {

    Login_act login_act;


    public N_Handler() {
        login_act = Login_act._Login_act;
    }

    public N_Handler(Looper mainLooper) {
        super(mainLooper);
    }

    @Override
    public void run(boolean b) {
        if (b) {
            Log.d("ini_view", "s");
            new Login_act.RequestApiTask().execute();
        } else {
            Log.d("ini_view", "f");
        }
    }


}
