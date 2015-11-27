package com.phone.ignore.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.util.Log;
import android.widget.TextView;
import com.phone.ignore.R;


public class PopupActivity extends Activity {

    private int mDeviceScreenWidth;
    private int mDeviceScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);*/

        setContentView(R.layout.activity_popup);

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDeviceScreenWidth = display.getWidth();
        mDeviceScreenHeight = display.getHeight();

        String number = getIntent().getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        TextView text = (TextView) findViewById(R.id.tv_number);
        text.setText("수신거부됨 : " + number);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int x = (int)event.getX();
            int y = (int)event.getY();

            Bitmap bitmapScreen = Bitmap.createBitmap(mDeviceScreenWidth, mDeviceScreenHeight, Bitmap.Config.ARGB_8888);
            if(x < 0 || y < 0)
                return false;

            int ARGB = bitmapScreen.getPixel(x, y);

            if(Color.alpha(ARGB) == 0) {
                finish();
                super.onTouchEvent(event);
            }

            return true;
        }
        return false;
    }

}
