package com.phone.ignore.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.phone.ignore.NumberAddDialog;
import com.phone.ignore.R;
import com.phone.ignore.adapter.BlockedNumberAdapter;
import com.phone.ignore.adapter.IgnoreCallAdapter;
import com.phone.ignore.constants.IgnoreCall;
import com.phone.ignore.pref.IgnoreCallPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class IgnoreListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<IgnoreCall> array;
    IgnoreCallAdapter adapter;
    IgnoreCallPref pref;
    CheckBox cbAll;
    Context context;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_ignore_list);

        context = this;
        setTitle("수신거부된 통화");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = new IgnoreCallPref(this);

        ListView lvIgnore = (ListView)findViewById(R.id.lv_ignore);

        adapter = new IgnoreCallAdapter(getBaseContext(), 0);
        lvIgnore.setAdapter(adapter);
        lvIgnore.setOnItemClickListener(this);
        lvIgnore.setEmptyView(getLayoutInflater().inflate(R.layout.item_nodata, null));

        cbAll = (CheckBox)findViewById(R.id.cb_item_delete);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setAllChecked(isChecked);
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btn_block_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int test = adapter.getCheckedItemPosition().size();
                for(int i=0; i<adapter.getCheckedItemPosition().size(); i++){
                    int delPosition = adapter.getCheckedItemPosition().get(i);
                    array.get(delPosition);
                    pref.deleteIgnoreCallData(array.get(delPosition));
                }

                adapter.setAllChecked(false);
                storeData();
            }
        });
    }

    public void storeData(){
        array = pref.getSavedIgnoreCall();

        Collections.sort(array, new Comparator<IgnoreCall>() {
            @Override
            public int compare(IgnoreCall lhs, IgnoreCall rhs) {
                return (lhs.regTime > rhs.regTime) ? -1: (lhs.regTime < rhs.regTime)? 1:0 ;
            }
        });

        cbAll.setChecked(false);
        adapter.clear();
        for(int i=0; i<array.size(); i++)
            adapter.add(array.get(i));
    }

    @Override
    protected void onResume() {
        super.onResume();
        storeData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setChecked(position);
        adapter.notifyDataSetChanged();
        Log.e("TEST", adapter.getItem(position).strTime);
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
}
