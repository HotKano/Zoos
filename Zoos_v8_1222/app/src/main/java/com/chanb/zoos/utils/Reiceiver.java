package com.chanb.zoos.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

public class Reiceiver {
    // NETWORK RECEVIER 클래스
    public final static String NETWORK_RECEVIER_CLASS =
            "com.xxx.xxx.ui.activity.common.ULNetworkReceiver";

    /*
     * recevier를 등록한다.
     * @param activity	 		recevier를 등록할 activity
     * @param recevierName		호출될 BroadcastReceiver 클래스
     * @param recevierEventName	recevier event 이름
     * @return BroadcastReceiver
     */
    public static BroadcastReceiver registerReceiver(Activity activity, String recevierName,
                                                     String recevierEventName) {
        IntentFilter filter = null;
        Class recevierClass = null;
        BroadcastReceiver receiver = null;

        if (receiver == null) {
            try {
                filter = new IntentFilter(recevierEventName);
                recevierClass = Class.forName(recevierName);
                receiver = (BroadcastReceiver) recevierClass.newInstance();
                activity.registerReceiver(receiver, filter);
                Log.d("receiver", "registed BroadcastReceiver...");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                filter = null;
                recevierClass = null;
            }
        }

        return receiver;
    }

	/*
            *
    recevier를 해제한다.
            *
    @param
    activity
	 *
    @param
    receiver
	 */

    public static void unregisterReceiver(Activity activity, BroadcastReceiver receiver) {
        if (receiver != null) {
            activity.unregisterReceiver(receiver);

            Log.d("receiver", "unregisted BroadcastReceiver...");
        }
    }
}


