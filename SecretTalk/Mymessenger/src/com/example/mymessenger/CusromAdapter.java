package com.example.mymessenger;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;



//커스텀 리스트뷰 아답터
@SuppressLint("ViewHolder")
public class CusromAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private Activity m_activity;
    private ArrayList<dataTest> arr;
    private static Context cnt;
    String myid;
    public CusromAdapter(Activity act, ArrayList<dataTest> arr_item,String id) {

        this.m_activity = act;

        arr = arr_item;

        mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        myid = id;
    }
    public void SetContext(Context c)
    {
        cnt = c;
    }


    @Override
    public int getCount() {
        return arr.size();
    }

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
        //

        LinearLayout layout_view =  (LinearLayout)convertView.findViewById(R.id.coverLinear);

        TextView tv = (TextView)convertView.findViewById(R.id.chatmsg);
        dataTest  dt = (dataTest)getItem(position);
        if(tv != null)
        tv.setText(dt.memberList.get(1).toString());
  /*  버튼에 이벤트처리를 하기위해선 setTag를 이용해서 사용할 수 있다.
       *   Button btn 가 있다면, btn.setTag(position)을 활용해서 각 버튼들
       *   이벤트처리를 할 수 있다.   */

        layout_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoIntent(position);
            }
        });

        return convertView;

    }


    public String MakeGuid()
    {
        //var d = new Date().getTime();
        return UUID.randomUUID().toString();
    }
    public void GoIntent(int a){

      //  dataTest ds = arr.get(a);
        //cnt.OpenRoom(m_activity,ds , a);
        Intent intent = new Intent(m_activity, ChattingFrom.class);

        //putExtra 로 선택한 아이템의 정보를 인텐트로 넘겨 줄 수 있다.
        String uuid = MakeGuid();
        intent.putExtra("myid",myid);
        intent.putExtra("MSGList", arr.get(a).messageList);
        intent.putExtra("MemList", arr.get(a).memberList);
        intent.putExtra("UUID",uuid);
        Bundle extras = new Bundle();
       // extras.putSerializable("bean", cnt);
        intent.putExtras(extras);
        m_activity.startActivity(intent);
    }

}





