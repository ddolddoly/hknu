package com.phone.ignore.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 9. 2.
 */
public class SettingPref {

    public static final String prefName = "setting";

    public static void setPopup(Context context, boolean status){
        SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("popup", status);
        editor.commit();
    }

    public static void setNotification(Context context, boolean status){
        SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("after_noti", status);
        editor.commit();
    }

    public static boolean getPopupSetting(Context context){
        SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_MULTI_PROCESS);

        return sp.getBoolean("popup", true);
    }

    public static boolean getNotificationSetting(Context context){
        SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_MULTI_PROCESS);

        return sp.getBoolean("after_noti", true);
    }

}
