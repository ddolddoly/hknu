package com.phone.ignore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.phone.ignore.R;
import com.phone.ignore.adapter.ScheduleEditAdapter;
import com.phone.ignore.ScheduleMenuDialog;
import com.phone.ignore.constants.Schedule;
import com.phone.ignore.pref.SchedulePref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 8. 26.
 */
public class ScheduleListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    int editMode;
    ScheduleEditAdapter adapter;
    ArrayList<Schedule> array;
    Menu menu;
    SchedulePref schedulePref;
    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_ignore_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        schedulePref = SchedulePref.getInstance(this);
        editMode = getIntent().getIntExtra("edit_mode", ScheduleMenuDialog.MODE_MODIFY);
        adapter = new ScheduleEditAdapter(this, 0);
        adapter.setEditMode(editMode);
        setTitle(editMode == ScheduleMenuDialog.MODE_MODIFY ? "수정할 일정 선택" : "일정 삭제");

       storeData();

        ((CheckBox)findViewById(R.id.cb_item_delete)).setVisibility(View.GONE);

        ListView lvIgnore = (ListView)findViewById(R.id.lv_ignore);
        lvIgnore.setAdapter(adapter);
        lvIgnore.setOnItemClickListener(this);
        lvIgnore.setEmptyView(getLayoutInflater().inflate(R.layout.item_nodata, null));

        findViewById(R.id.btn_block_add).setVisibility(View.GONE);

        findViewById(R.id.btn_block_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                int count = adapter.getCheckedItemPosition().size();

                for(int i=0; i<count; i++){
                    int delPosition = adapter.getCheckedItemPosition().get(i);
                    schedulePref.deleteScheduleData(array.get(delPosition));
                }

                adapter.setAllChecked(false);
                storeData();
                adapter.notifyDataSetChanged();

                Toast.makeText(getBaseContext(), count+"개 일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(editMode == ScheduleMenuDialog.MODE_MODIFY){
            Intent intent = new Intent(getBaseContext(), ScheduleEditActivity.class);
            intent.putExtra("edit_mode", editMode);
            intent.putExtra("title", array.get(position).strTitle);
            intent.putExtra("date", array.get(position).strDate);
            intent.putExtra("start", array.get(position).strStartTime);
            intent.putExtra("end", array.get(position).strEndTime);
            startActivity(intent);
        }else{
            adapter.setChecked(position);
            adapter.notifyDataSetChanged();
        }
    }

    public void storeData(){
        array = schedulePref.getSavedSchedule();
        
        Collections.sort(array, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule lhs, Schedule rhs) {
                return (lhs.regTime > rhs.regTime) ? -1 : (lhs.regTime < rhs.regTime) ? 1 : 0;
            }
        });
        
        adapter.clear();
        for(int i=0; i<array.size(); i++)
            adapter.add(array.get(i));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        storeData();
        adapter.notifyDataSetChanged();
    }
}
