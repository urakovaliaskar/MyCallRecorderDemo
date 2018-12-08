package com.aliaskarurakov.android.mycallrecorderdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {

    static final String TAG="State";
    static final String TAG1=" Inside State";
    static Boolean recordStarted;
    public static String number;
    public static String name;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            Bundle extras = intent.getExtras();
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.i(TAG, "onReceive: first state = " + state);
            Toast.makeText(context, "Call detected(Incoming/Outgoing) " + state, Toast.LENGTH_SHORT).show();

            if (extras != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    Log.i(TAG, "onReceive: state = " + state);

                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                    Log.i(TAG, "onReceive: state = " + state);
                    number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.i(TAG, "onReceive: number = " + number);
                    Intent service = new Intent(context, RecorderService.class);
                    service.putExtra("number", number);
                    context.startService(service);

                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    Log.i(TAG, "onReceive: state = " + state);

                    context.stopService(new Intent(context, RecorderService.class));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
