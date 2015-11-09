package com.example.picar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

public class Helper {

	private Context mContext;
	private DisplayMetrics displayMetrics;
	

	public Helper(Context context) {
		mContext = context;
		displayMetrics = mContext.getResources().getDisplayMetrics();
	}

	@SuppressLint("DefaultLocale")
	public void wifiSetting() {
		// IP 얻어오기
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		
		wifiManager.getConnectionInfo().getRssi();
		
		// 접속한 AP의 IP
		int client = dhcpInfo.gateway;
		String apIp = String.format("%d.%d.%d.%d", client & 0xff, client >> 8 & 0xff, client >> 16 & 0xff, client >> 24 & 0xff);
		if (apIp.equals("0.0.0.0")) {
			apIp = Protocol.PICAR_IP;
		}
		Protocol.setApIp(apIp);
		//Protocol.setApIp("192.168.123.102"); //TODO
		Log.d("IP Address", "AP IP : " + Protocol.AP_IP);
		
		// 단말기의 WIFI IP
		int server = dhcpInfo.ipAddress;
		String wifiIp = String.format("%d.%d.%d.%d", server & 0xff, server >> 8 & 0xff, server >> 16 & 0xff, server >> 24 & 0xff);
		Protocol.setWifiIp(wifiIp);
		Log.d("IP Address", "WIFI IP : " + Protocol.WIFI_IP);
	}
	
	public JoyStickView getJoyStick(RelativeLayout layout) {
        JoyStickView js = new JoyStickView(mContext, layout, R.drawable.image_control_joystick);
        js.setStickSize(dpToPx(60), dpToPx(60));
        js.setLayoutAlpha(200);
        js.setStickAlpha(200);
        js.setOffset(dpToPx(50));
        js.setMinimumDistance(dpToPx(20));
        js.drawStick();
        
        return js;
	}
	
	// 해상도마다 다른 dp를 픽셀로 변환
	public int dpToPx(int dp) {
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));   
        
        return px;
    }
	
	// 정수형을 4바이트 바이트배열로 변환
	public byte[] intToByte(int a) {
		byte[] array = new byte[4];
		array[0] |= (byte)((a&0xFF000000)>>24);
		array[1] |= (byte)((a&0xFF0000)>>16);
		array[2] |= (byte)((a&0xFF00)>>8);
		array[3] |= (byte)(a&0xFF);
		
		return array;
	}
}