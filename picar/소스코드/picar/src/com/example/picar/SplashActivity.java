package com.example.picar;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	private String message = ""; 
	private WifiManager wifiManager;
	long time = 4000;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		
		// 로딩 이미지, 애니메이션 적용
		ImageView image = (ImageView) findViewById(R.id.image_loading);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.loading_alpha);
		image.startAnimation(anim);
		
		
		// WiFi 설정
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			message = "PiCar 연결을 위해 Wi-Fi를 설정합니다.\n";
			time = 6000;
		}
		
		
		// PiCar 연결
		wifiManager.startScan();
		searchWifi();

		
		// 연결 후 메인 화면으로
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		}, time);
	}
	
	private void searchWifi() {
		boolean connected = false;
		List<ScanResult> apList = wifiManager.getScanResults();
		
		if (wifiManager.getScanResults() != null) {
			for (int i=0; i<apList.size(); i++) {
				ScanResult scanResult = apList.get(i);
				
				if (Protocol.WIFI_SSID.equals(scanResult.SSID)) {
					connected = true;
					WifiConfiguration wfc = new WifiConfiguration();
					wfc.SSID = "\"".concat(Protocol.WIFI_SSID).concat("\"");
					wfc.status = WifiConfiguration.Status.DISABLED;
					wfc.priority = 40;
					
					// WPA or WPA2
					wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
					wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
					wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
					wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
					wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
					wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
					wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
					wfc.preSharedKey = "\"".concat(Protocol.WIFI_PASSWORD).concat("\"");
					
					int networkId = wifiManager.addNetwork(wfc);
					Log.d("SplashActivity", "try connect, networkId : " + scanResult.SSID);
					
					// 연결
					if (networkId != -1) {
						wifiManager.enableNetwork(networkId, true);		
						message += "PiCar Wi-Fi에 연결합니다.";
					}
				}
			}
		}
		
		if (!connected) {
			message += "PiCar Wi-Fi를 찾을 수 없습니다.";
		}
	}
}

