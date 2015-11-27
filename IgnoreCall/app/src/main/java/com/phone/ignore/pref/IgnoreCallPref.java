package com.phone.ignore.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.phone.ignore.constants.IgnoreCall;
import com.phone.ignore.constants.IgnoreCall;

import java.util.ArrayList;
import java.util.Map;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 8. 26.
 */
public class IgnoreCallPref {

    private SharedPreferences sp = null;
    private static IgnoreCallPref mInstance;
    public IgnoreCall ignoreCall;
    private Gson gson;

    public IgnoreCallPref(Context context) {
        sp = context.getSharedPreferences("ignoreCall", Context.MODE_MULTI_PROCESS);
        gson = new Gson();
    }

    public static IgnoreCallPref getInstance(Context context){

        if(mInstance == null)
            mInstance = new IgnoreCallPref(context);

        return mInstance;
    }

    public void setIgnoreCallData(IgnoreCall ignoreCall){
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(Long.toString(System.currentTimeMillis()), gson.toJson(ignoreCall));
        editor.commit();
    }

    public ArrayList<IgnoreCall> getSavedIgnoreCall() {
        Map<String, ?> ignoreCallMap = sp.getAll();
        ArrayList<IgnoreCall> array = new ArrayList<>();

        for(Map.Entry<String, ?> entry : ignoreCallMap.entrySet()){
            array.add(gson.fromJson(entry.getValue().toString(), IgnoreCall.class));
        }
        return array;
    }

    public void deleteIgnoreCallData(IgnoreCall ignoreCall){
        SharedPreferences.Editor editor = sp.edit();
        String test = ignoreCall.strTime;
        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(ignoreCall).equals(entry.getValue().toString()))
                editor.remove(entry.getKey());

        }
        editor.commit();
    }

    public String getEntryKey(IgnoreCall ignoreCall){
        String entryKey = "";

        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(ignoreCall).equals(entry.getValue().toString())){
                entryKey = entry.getKey();
            }else{
                Log.e("IgnoreCall Preference", "Can't find a key");
            }
        }
        return entryKey;
    }
}
