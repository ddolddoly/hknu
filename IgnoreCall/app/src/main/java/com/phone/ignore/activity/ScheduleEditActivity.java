package com.phone.ignore.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import com.phone.ignore.R;
import com.phone.ignore.adapter.ScheduleEditAdapter;
import com.phone.ignore.ScheduleMenuDialog;
import com.phone.ignore.constants.Schedule;
import com.phone.ignore.pref.SchedulePref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ScheduleEditActivity extends AppCompatActivity {

    ScheduleEditAdapter adapter;
    int year, month, dayOfMonth;
    int editMode = -1;
    Schedule beforeSchedule;
    SchedulePref schedulePref;
    
    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_schedule_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        schedulePref = SchedulePref.getInstance(getBaseContext());
        final EditText edtTitle = (EditText)findViewById(R.id.edt_schedule_name);
        final EditText edtStartHour = (EditText)findViewById(R.id.edt_schedule_start_hour);
        final EditText edtStartMin = (EditText)findViewById(R.id.edt_schedule_start_min);
        final EditText edtEndHour = (EditText)findViewById(R.id.edt_schedule_end_hour);
        final EditText edtEndMin = (EditText)findViewById(R.id.edt_schedule_end_min);

        editMode = getIntent().getIntExtra("edit_mode", -1);
        if(editMode == ScheduleMenuDialog.MODE_MODIFY){

            setTitle("일정 수정");
            int index = getIntent().getIntExtra("index", 0);
            
           beforeSchedule  = schedulePref.getSavedSchedule().get(index);
            
            String title = beforeSchedule.strTitle;
            String date = beforeSchedule.strDate;
            String start = beforeSchedule.strStartTime;
            String end = beforeSchedule.strEndTime;

            String[] dateSplit = date.split("-");
            year = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]);
            dayOfMonth = Integer.parseInt(dateSplit[2]);
            
            edtTitle.setText(getIntent().getStringExtra("title"));

            edtStartHour.setText(getIntent().getStringExtra("start").split(":")[0]);
            edtStartMin.setText(getIntent().getStringExtra("start").split(":")[1]);

            edtEndHour.setText(getIntent().getStringExtra("end").split(":")[0]);
            edtEndMin.setText(getIntent().getStringExtra("end").split(":")[1]);

        }else{
            setTitle("일정 등록");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        CalendarView calendarView = (CalendarView)findViewById(R.id.calendar);
        calendarView.setShowWeekNumber(false);
        try{
            calendarView.setDate(sdf.parse(String.format("%d-%02d-%02d", year, month, dayOfMonth)).getTime());
        }catch(ParseException e){
            e.printStackTrace();
            calendarView.setDate(new Date().getTime());
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int _year, int _month, int _dayOfMonth) {
                year = _year;
                month = _month + 1;
                dayOfMonth = _dayOfMonth;
            }
        });


        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtTitle.getText().toString().isEmpty() || edtStartHour.getText().toString().isEmpty() || edtStartMin.getText().toString().isEmpty()
                        || edtEndHour.getText().toString().isEmpty() || edtEndHour.getText().toString().isEmpty()){

                    Toast.makeText(getBaseContext(), "모든 칸을 입력해주세요.", 0).show();
                }else{

                    String strDate = String.format("%4d-%02d-%02d", year, month, dayOfMonth);
                    String strStartTime = String.format("%s:%s:00", edtStartHour.getText().toString(), edtStartMin.getText().toString());
                    String strEndTime = String.format("%s:%s:00", edtEndHour.getText().toString(), edtEndMin.getText().toString());


                    if(editMode == ScheduleMenuDialog.MODE_MODIFY){
                        schedulePref.modifyScheduleData(beforeSchedule, new Schedule(edtTitle.getText().toString(), strDate, strStartTime, strEndTime, System.currentTimeMillis()));
                    }else{
                        schedulePref.setScheduleData(new Schedule(edtTitle.getText().toString(), strDate, strStartTime, strEndTime, System.currentTimeMillis()));
                    }
                    finish();
                }
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
