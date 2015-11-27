package com.phone.ignore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import com.phone.ignore.activity.ScheduleEditActivity;
import com.phone.ignore.activity.ScheduleListActivity;

public class ScheduleMenuDialog extends Dialog {

    public static final int MODE_MODIFY = 0;
    public static final int MODE_DELETE = 1;

    Context context;

    public ScheduleMenuDialog(Context context) {
        super(context, R.style.Dialog);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.context = context;
        setTitle("일정");
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_schedule_menu);

        findViewById(R.id.btn_schedule_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ScheduleEditActivity.class));
                dismiss();
            }
        });

        findViewById(R.id.btn_schedule_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ScheduleListActivity.class).putExtra("edit_mode", MODE_MODIFY));
                dismiss();
            }
        });

        findViewById(R.id.btn_schedule_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ScheduleListActivity.class).putExtra("edit_mode", MODE_DELETE));
                dismiss();
            }
        });

    }
}
