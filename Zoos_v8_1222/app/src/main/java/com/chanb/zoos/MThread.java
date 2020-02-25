package com.chanb.zoos;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class MThread extends Thread {
    public Handler handler;


    public MThread(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        Looper.prepare();
        Looper.loop();
        Log.d("MThread", "ok");
    }

    @SuppressLint("HandlerLeak")
    public Handler mBackHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message resMsg = handler.obtainMessage();
            switch (msg.what) {
                case 0:
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        ;
                    }
                    resMsg.what = 0;
                    break;

                case 1:
                    try {

                    } catch (Exception e) {

                    }
                    resMsg.what = 1;
                    break;
            }
            handler.sendMessage(resMsg);

        }


    };


}
