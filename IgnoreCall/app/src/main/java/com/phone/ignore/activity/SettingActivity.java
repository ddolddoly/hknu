package com.phone.ignore.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;
import com.phone.ignore.R;
import com.phone.ignore.pref.SettingPref;

/**
 * Copyright 2015 Onbi. All rights reserved.
 *
 * @author Shin Gwangsu (maruroid@gmail.com)
 * @since 15. 9. 2.
 */
public class SettingActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = this;

        setTitle("환경설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SwitchCompat switchCompat = (SwitchCompat)findViewById(R.id.sw_popup);
        switchCompat.setChecked(SettingPref.getPopupSetting(context));

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingPref.setPopup(context, isChecked);
            }
        });
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
