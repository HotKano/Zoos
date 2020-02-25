package com.chanb.zoos;

import android.os.Looper;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLoginHandler;


public class N_Handler_Logo extends OAuthLoginHandler {

    Logo_act logo_act;


    public N_Handler_Logo() {
        logo_act = (Logo_act) Logo_act._logo_act;
    }

    public N_Handler_Logo(Looper mainLooper) {
        super(mainLooper);
    }

    @Override
    public void run(boolean b) {
        if (b) {
            Log.d("ini_view", "s");
            new Logo_act.RequestApiTask().execute();
        } else {
            Log.d("ini_view", "f");
        }
    }


}
