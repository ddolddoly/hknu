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
import com.phone.ignore.service.ServiceMonitor;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Secret on 2015. 9. 1..
 */
public class BootReceiver extends BroadcastReceiver {

    private final String LOG_NAME = BootReceiver.class.getSimpleName();
    private TelephonyManager telephony;
    private Context context;
    private Intent intent;
    private IgnoreCallPref ignoreCallPref;
    public static boolean isFlag = false;
    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (!serviceMonitor.isMonitoring()) {
                serviceMonitor.startMonitoring(context);
            }
        }
    }
}