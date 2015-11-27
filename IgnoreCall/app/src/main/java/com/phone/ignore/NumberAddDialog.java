package com.phone.ignore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import com.phone.ignore.activity.ScheduleEditActivity;
import com.phone.ignore.activity.ScheduleListActivity;
import com.phone.ignore.constants.BlockNumber;
import com.phone.ignore.pref.BlockNumberPref;

public class NumberAddDialog extends Dialog {

    public static final int MODE_MODIFY = 0;
    public static final int MODE_DELETE = 1;

    Context context;

    public NumberAddDialog(Context context) {
        super(context, R.style.Dialog);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.context = context;
        setTitle("차단번호 등록");
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_number);

        final EditText edtPhoneNum = (EditText)findViewById(R.id.edt_phone_num);
        final BlockNumberPref pref = BlockNumberPref.getInstance(context);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setBlockNumberData(new BlockNumber(edtPhoneNum.getText().toString(), "", System.currentTimeMillis()));
                dismiss();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
