package com.phone.ignore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.phone.ignore.activity.PopupActivity;
import com.phone.ignore.constants.BlockNumber;
import com.phone.ignore.constants.DateInterval;
import com.phone.ignore.constants.IgnoreCall;
import com.phone.ignore.pref.BlockNumberPref;
import com.phone.ignore.pref.IgnoreCallPref;
import com.phone.ignore.pref.SchedulePref;
import com.phone.ignore.pref.SettingPref;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Secret on 2015. 9. 1..
 */
public class CallStateReceiver extends BroadcastReceiver {

    private final String LOG_NAME = CallStateReceiver.class.getSimpleName();
    private TelephonyManager telephony;
    private Context context;
    private Intent intent;
    private IgnoreCallPref ignoreCallPref;
    public static boolean isFlag = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        ignoreCallPref = IgnoreCallPref.getInstance(context);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        isFlag = false;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(isFlag)
                            return;
                        isFlag = true;
                        if(!ignoreNumber(incomingNumber))
                            ignoreDate(incomingNumber);

                        break;
                    default:
                        break;
                }
            }
        };
        telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private boolean ignoreNumber(String incomingNumber)
    {
        BlockNumberPref blockNumberPref = BlockNumberPref.getInstance(context);
        ArrayList<BlockNumber> blockNumbers = blockNumberPref.getSavedBlockNumber();

        Log.e("INCOMING", "Incoming :"+ incomingNumber );
        // 차단 번호 비교
        for (final BlockNumber v : blockNumbers) {
            if(v.strPhoneNum.equals(incomingNumber))
            {
                long now = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:00");
                ignoreCallPref.setIgnoreCallData(new IgnoreCall(incomingNumber, sdf.format(now), now));
                endCall();
                return true;
            }
        }
        return false;
    }
    private void ignoreDate(String incomingNumber)
    {

        SchedulePref schedulePref = SchedulePref.getInstance(context);

        ArrayList<DateInterval> ignoreDates = schedulePref.getSavedScheduleMillis();
        
        long now = System.currentTimeMillis();

        for(final DateInterval v : ignoreDates)
        {
            if(v.start <= now && v.end >= now)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:00");
                ignoreCallPref.setIgnoreCallData(new IgnoreCall(incomingNumber, sdf.format(now), now));
                endCall();
                return;
            }
        }

    }

    private void endCall() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_NAME, "전화가 끊긴다아아아아앙아아아아아아아아");
                try{
                    Class clazz = Class.forName(telephony.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);

                    ITelephony telephonyService = (ITelephony) method.invoke(telephony);
                    telephonyService.endCall();
                    //telephonyService.silenceRinger();
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(SettingPref.getPopupSetting(context)){
                    Intent i = new Intent(context, PopupActivity.class);
                    i.putExtras(intent);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                    }
                    context.startActivity(i);
                }
            }
        }, 100);

    }






}
