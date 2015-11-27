package com.phone.ignore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.phone.ignore.R;
import com.phone.ignore.ScheduleMenuDialog;
import com.phone.ignore.service.ServiceMonitor;

public class MainActivity extends AppCompatActivity{

    Context context;
    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();

    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        context= this;

        if (!serviceMonitor.isMonitoring()) {
            serviceMonitor.startMonitoring(getApplicationContext());
        }

        findViewById(R.id.btn_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScheduleMenuDialog(context).show();
            }
        });

        findViewById(R.id.btn_ignore_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, IgnoreListActivity.class));
            }
        });

        findViewById(R.id.btn_ignore_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BlockedNumberActivity.class));
            }
        });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SettingActivity.class));
            }
        });
    }

}
