package com.phone.ignore.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.phone.ignore.constants.BlockNumber;

import java.util.ArrayList;
import java.util.Map;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 8. 26.
 */
public class BlockNumberPref {

    private SharedPreferences sp = null;
    private static BlockNumberPref mInstance;
    private Gson gson;

    public BlockNumberPref(Context context) {
        gson = new Gson();
        sp = context.getSharedPreferences("block_number", Context.MODE_MULTI_PROCESS);
    }

    public static BlockNumberPref getInstance(Context context){

        if(mInstance == null)
            mInstance = new BlockNumberPref(context);

        return mInstance;
    }

    public void setBlockNumberData(BlockNumber blockNumber){
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(Long.toString(System.currentTimeMillis()), gson.toJson(blockNumber));
        editor.commit();
    }

    public ArrayList<BlockNumber> getSavedBlockNumber() {
        Map<String, ?> blockNumberMap = sp.getAll();
        ArrayList<BlockNumber> array = new ArrayList<>();

        for(Map.Entry<String, ?> entry : blockNumberMap.entrySet()){
            array.add(gson.fromJson(entry.getValue().toString(), BlockNumber.class));
        }
        return array;
    }

    public void modifyBlockNumberData(BlockNumber before,  BlockNumber modified){
        SharedPreferences.Editor editor = sp.edit();

        String entryKey = getEntryKey(before);
        editor.remove(entryKey);
        editor.putString(entryKey, gson.toJson(modified));
        editor.commit();
    }

    public void deleteBlockNumberData(BlockNumber blockNumber){
        SharedPreferences.Editor editor = sp.edit();

        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(blockNumber).equals(entry.getValue().toString()))
                editor.remove(entry.getKey());

        }
        editor.commit();
    }

    public String getEntryKey(BlockNumber blockNumber){
        String entryKey = "";

        Map<String, ?> entryMap = sp.getAll();
        for(Map.Entry<String, ?> entry : entryMap.entrySet()){
            if(gson.toJson(blockNumber).equals(entry.getValue().toString())){
                entryKey = entry.getKey();
            }else{
                Log.e("BlockNumber Preference", "Can't find a key");
            }
        }
        return entryKey;
    }
}
