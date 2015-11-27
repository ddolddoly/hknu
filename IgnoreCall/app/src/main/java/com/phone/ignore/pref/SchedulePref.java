package com.phone.ignore.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.phone.ignore.constants.DateInterval;
import com.phone.ignore.constants.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 8. 26.
 */
public class SchedulePref {

    private SharedPreferences sp = null;
    private static SchedulePref mInstance;
    private Gson gson;
    
    public SchedulePref(Context context) {
        gson = new Gson();
        sp = context.getSharedPreferences("schedule", Context.MODE_MULTI_PROCESS);
    }

    public static SchedulePref getInstance(Context context){

        if(mInstance == null)
            mInstance = new SchedulePref(context);

        return mInstance;
    }

    public void setScheduleData(Schedule schedule){
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(Long.toString(System.currentTimeMillis()), gson.toJson(schedule));
        editor.commit();
    }

    public ArrayList<Schedule> getSavedSchedule() {
        Map<String, ?> scheduleMap = sp.getAll();
        ArrayList<Schedule> array = new ArrayList<>();

        for(Map.Entry<String, ?> entry : scheduleMap.entrySet()){
            array.add(gson.fromJson(entry.getValue().toString(), Schedule.class));
        }
        return array;
    }

    public ArrayList<DateInterval> getSavedScheduleMillis(){
        Map<String, ?> scheduleMap = sp.getAll();
        ArrayList<Schedule> array = new ArrayList<>();
        ArrayList<DateInterval> date = new ArrayList<>();

        for(Map.Entry<String, ?> entry : scheduleMap.entrySet()){
            array.add(gson.fromJson(entry.getValue().toString(), Schedule.class));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:00");

        for(Schedule s : array){
            Date start = null, end = null;

            try{
                start = sdf.parse(String.format("%s %s", s.strDate, s.strStartTime));
                end = sdf.parse(String.format("%s %s", s.strDate, s.strEndTime));
            }catch(ParseException e){
                e.printStackTrace();
            }

            if(start != null && end != null)
                date.add(new DateInterval(start.getTime(), end.getTime()));

        }

        return date;
    }

    public void modifyScheduleData(Schedule before,  Schedule modified){
        SharedPreferences.Editor editor = sp.edit();

        String entryKey = getEntryKey(before);
        editor.remove(entryKey);
        editor.putString(entryKey, gson.toJson(modified));
        editor.commit();
    }

    public void deleteScheduleData(Schedule schedule){
        SharedPreferences.Editor editor = sp.edit();

        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(schedule).equals(entry.getValue().toString()))
                editor.remove(entry.getKey());

        }
        editor.commit();
    }

    public String getEntryKey(Schedule schedule){
        String entryKey = "";

        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(schedule).equals(entry.getValue().toString())){
                entryKey = entry.getKey();
            }else{
                Log.e("Schedule Preference", "Can't find a key");
            }
        }
        return entryKey;
    }
}
