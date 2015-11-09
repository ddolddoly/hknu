package com.example.picar.thread;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.example.picar.MainActivity.UIHandler;
import com.example.picar.Protocol;

public class DistanceThread extends Thread implements Runnable {
	
	private Context mContext;
	private UIHandler handler;
	private boolean flag = true;
	
	
	public DistanceThread(Context context, UIHandler handler) {
		mContext = context;
		this.handler = handler;
	}
	
	@Override
	public void run() {

		while (true) {
			if (flag) {
				try {
					WifiManager wifiManager = (WifiManager) mContext.getSystemService(Activity.WIFI_SERVICE);
					wifiManager.startScan();
					List<ScanResult> apList = wifiManager.getScanResults();
					
					if (wifiManager.getScanResults() != null) {
						for (int i=0; i<apList.size(); i++) {
							ScanResult scanResult = apList.get(i);
							
							if (Protocol.WIFI_SSID.equals(scanResult.SSID)) {
								double distance = calculateDistance((double)scanResult.level, scanResult.frequency);
								distance = Math.round(distance * 100.0) / 100.0;
								
								Message msg = handler.obtainMessage();
								msg.what = Protocol.MESSAGE_DISTANCE;
								msg.obj = distance;
								handler.sendMessage(msg);
								Log.d("DistanceThread", "wifi level : " + scanResult.level
										+ ", frequency : " + scanResult.frequency
										+ ",distance : " + distance + "m");
							}
						}
					}
					
					Thread.sleep(500);
				} catch (Exception e) {
					Log.d("DistanceThread", "error", e);
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				
			}
		}
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public double calculateDistance(double levelInDb, double freqInMHz) {
		double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
		return Math.pow(10.0, exp);
	}
}
