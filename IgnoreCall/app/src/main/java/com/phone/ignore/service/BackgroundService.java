package com.phone.ignore.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.os.SystemClock;
import com.phone.ignore.receiver.CallStateReceiver;


public class BackgroundService extends Service {
    private final String LOG_NAME = BackgroundService.class.getSimpleName();

    public static Thread mThread;

    private ActivityManager mActivityManager;

    private boolean serviceRunning = false;

    private CallStateReceiver callReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        serviceRunning = true;

        callReceiver = new CallStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CALL);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(callReceiver, filter);

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(callReceiver);
        serviceRunning = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serviceRunning) {
                        SystemClock.sleep(1000);
                    }
                }
            });

            mThread.start();
        } else if (mThread.isAlive() == false) {
            mThread.start();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
