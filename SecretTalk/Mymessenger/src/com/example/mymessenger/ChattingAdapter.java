package com.example.mymessenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;





//커스텀 리스트뷰 아답터
@SuppressLint("ViewHolder")
public class ChattingAdapter  extends BaseAdapter {
    private String myid;
    private LayoutInflater mInflater;
    private Activity m_activity;
    private ArrayList<ChattingData> arr;
    private static Context cnt;

    public ChattingAdapter(Activity act, ArrayList<ChattingData> arr_item,String id) {

        this.m_activity = act;
        arr = arr_item;
        mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        myid = id;

    }

    public void SetContext(Context c)
    {
        cnt = c;
    }
    // 어댑터에서 참조하는 ArrayList 가 가진 데이터의 개수를 반환하는 함수
    @Override
    public int getCount() {
        return arr.size();
    }
     // 인자로 넘어온 값에 해당하는 데이터를 반환하는 함수

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            int res = 0;
            res = R.layout.activity_chattingmsg;
            convertView = mInflater.inflate(res, parent, false);
        }
        // 리스트뷰로 보이는 부분

        //내 아이디가 아니라면 우측정렬
        ChattingData  dt = (ChattingData)getItem(position);
        if(dt != null)
        {
            String str = dt.m_id;
            TextView user_tv = (TextView) convertView.findViewById(R.id.chatmsg);
            user_tv.setText(str +" : " +dt.m_msg);
            if(myid.equals(str))
            {
                user_tv.setGravity(Gravity.RIGHT);
            }
            else
            {
                user_tv.setGravity(Gravity.LEFT);
            }

        }
        return convertView  ;
    }



}



