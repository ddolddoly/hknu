package com.example.picar;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.picar.anim.JoyStickDirectionAnimation;
import com.example.picar.thread.DistanceThread;
import com.example.picar.thread.ReceiveThread;
import com.example.picar.thread.SafetyThread;
import com.example.picar.thread.SendThread;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapLocationManager.OnLocationChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;

public class MainActivity extends FragmentActivity {
	
	private Context mContext;
	private Helper helper;

	
	// 쓰레드
	private SafetyThread safetyThread;
	private DistanceThread distanceThread;
	
	
	// 통신
	private ReceiveThread receiveThread;
	
	
	// 제어 변수
	private boolean isCamera = false;
	private boolean isMapLongClicked = false;
	private boolean isFavoriteClicked = false;
	private boolean isHelpClicked = false;
	private int motorSpeed = 3;
	private long now = 0;
	
	
	// 맵
	private GoogleMap mapView;
	private NMapLocationManager nMapLocationManager;
	private MyLocationListener locationListener;
	private MarkerOptions markerPoint;
	private MarkerOptions markerMyLocation;
	private MarkerOptions markerCar;
	private LocationManager locationManager;
	// 맵 제어
	private boolean markerPointSearchFlag = false;
	private boolean markerMyLocationCenterFlag = false;
	private boolean markerMyLocationSearchFlag = false;
	private boolean markerCarSearchFlag = false;
	
	
	// DB
	private DBManager dbManager;
	// 콜렉션
	private ArrayList<LatLng> latLngList;
	// 페이저 어댑터
	private PagerAdapterClass pagerAdapter;
	
	
	// 뷰
	private RelativeLayout layoutPager;
	private ViewPager pager;
	private WebView webCamera;
	private TextView textviewDistance;
	private ImageButton btnCamera;
	private ImageButton btnFavorite;
	private ImageButton btnHelp;
	private LayoutInflater inflater;
	private LinearLayout layoutMap;
	private LinearLayout layoutMapBottom;
	private RelativeLayout containerFavorite;
	private LinearLayout layoutFavorite;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        // 기본 설정
		mContext = getApplicationContext();
		helper = new Helper(mContext);
		inflater = getLayoutInflater();
		
		
		
		/***************************
		 ******** 뷰 초기화 ********
		 ***************************/
		RelativeLayout layoutJoystick = (RelativeLayout) findViewById(R.id.layout_joystick);
		ImageButton btnAdjust = (ImageButton) findViewById(R.id.button_adjust);
		ImageButton btnSpeedUp = (ImageButton) findViewById(R.id.button_speed_up);
		ImageButton btnSpeedDown = (ImageButton) findViewById(R.id.button_speed_down);
		Button btnFavoriteSave = (Button) findViewById(R.id.button_favorite_save);
		
		layoutPager = (RelativeLayout) findViewById(R.id.layout_pager);
		pager = (ViewPager) findViewById(R.id.pager_tutorial);
		webCamera = (WebView) findViewById(R.id.webview_camera);
		textviewDistance = (TextView) findViewById(R.id.textview_distance);
		btnCamera = (ImageButton) findViewById(R.id.button_camera);
		btnFavorite = (ImageButton) findViewById(R.id.button_favorite);
		btnHelp = (ImageButton) findViewById(R.id.button_help);
		layoutMap = (LinearLayout) findViewById(R.id.layout_map);
		layoutMapBottom = (LinearLayout) findViewById(R.id.layout_map_bottom);
		containerFavorite = (RelativeLayout) findViewById(R.id.container_favorite);
		layoutFavorite = (LinearLayout) findViewById(R.id.layout_favorite);

		mapView = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		

		
		/**********************************
		 ******** 뷰 이벤트 바인딩 ********
		 **********************************/
		layoutJoystick.setOnTouchListener(new JoyStickEvent(helper.getJoyStick(layoutJoystick)));
		btnAdjust.setOnClickListener(new AdjustButtonEvent());
		btnSpeedUp.setOnClickListener(new MotorSpeedControlEvent());
		btnSpeedDown.setOnClickListener(new MotorSpeedControlEvent());
		btnFavoriteSave.setOnClickListener(new FavoriteSaveEvent());
		
		pagerAdapter = new PagerAdapterClass(mContext);
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new ViewPagerChangeListener());
		btnCamera.setOnClickListener(new CameraButtonEvent());
		btnFavorite.setOnClickListener(new FavoriteButtonEvent());
		btnHelp.setOnClickListener(new HelpButtonEvent());
		
		mapView.setOnMapClickListener(new GoogleMapClickEvent());
		mapView.setOnMapLongClickListener(new GoogleMapMarkerEvent());
		mapView.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
		mapView.setOnInfoWindowClickListener(new InfoWindowClickEvent());
		mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return true; // true : 구글 마커의 default control off
			}
		});
		
		
		
		/****************************
		 ******* 기타 초기화 ********
		 ****************************/
		init();
	}
	
	// 초기화
	private void init() {
		/*************************
		 ******* 통신 설정 *******
		 *************************/
		
		// WiFi 세팅
		helper.wifiSetting();
		
		
		
		/**************************
		 ******** 맵 설정 *********
		 **************************/
		// 맵 시작위치 설정
		LatLng startPoint = new LatLng(37.01129, 127.26420); // 학교
		mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 17));
		
		// 마커 설정
		markerPoint = new MarkerOptions();
		markerMyLocation = new MarkerOptions();
		markerCar = new MarkerOptions();
		markerPoint.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point));
		markerMyLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me));
		markerCar.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car));
		markerMyLocationCenterFlag = true; // 최초 호출 시 내 위치로 이동
		
		// GPS 설정... 내 위치 검색은 네이버맵 API 이용
		nMapLocationManager = new NMapLocationManager(mContext);
		locationListener = new MyLocationListener();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        
		
        /*************************
         ******* 기타 설정 *******
         *************************/
		
		// 안전장치 쓰레드 할당
		safetyThread = new SafetyThread();
		safetyThread.start();
		
		// wifi 거리계산 쓰레드 할당
		distanceThread = new DistanceThread(mContext, new UIHandler());
		distanceThread.start();
		
		// db 설정, 콜렉션 초기화
		dbManager = new DBManager(this, "point.db", null, 1);
		latLngList = new ArrayList<LatLng>();
		
		// 저장된 즐겨찾기 포인트 조회
		selectAll();
		
		// 웹뷰 설정
		webCamera.setWebChromeClient(new WebChromeClient());
		webCamera.loadUrl("http://" + Protocol.AP_IP + ":8080/stream_simple.html");
	}
	
	
	
	
	/****************************
	 *******  메소드 TODO *******
	 ****************************/
	 
	 // 저장된 즐겨찾기 포인트 조회
	 private void selectAll() {
		ArrayList<ArrayList<Object>> list = dbManager.select();
		Log.d("select", "" + list.size() + "\n" + list);
		
		if (list.size() > 0) {
			long oId = Long.parseLong(list.get(0).get(0).toString());
			String oName = list.get(0).get(1).toString();
			String s = "";
			
			for (int i=0; i<list.size(); i++) {
				ArrayList<Object> values = list.get(i);
				long id = Long.parseLong(values.get(0).toString());
				String name = values.get(1).toString();
				
				if (oId != id) {
					addFavorite(oId, oName, s);
					s = "";
				}
				
				double latitude = Double.parseDouble(values.get(2).toString());
				double longitude = Double.parseDouble(values.get(3).toString());
				LatLng latLng = new LatLng(latitude, longitude);
				s += String.valueOf(latLng.latitude) + ",";
				s += String.valueOf(latLng.longitude) + "/";
				
				if (i == list.size() -1) {
					addFavorite(id, name, s);
				}
				
				oId = id;
				oName = name;
			}
		}
	}
	
	// 즐겨찾기 포인트 추가
	private void addFavorite(long id, String name, String latLngList) {
		// 즐겨찾기 추가
		@SuppressLint("InflateParams")
		View view = inflater.inflate(R.layout.favorite_list, null);
		
		TextView tv1 = (TextView) view.findViewById(R.id.textview_favorite_id);
		TextView tv2 = (TextView) view.findViewById(R.id.textview_favorite_name);
		TextView tv3 = (TextView) view.findViewById(R.id.textview_favorite_latlng);
		tv1.setText(String.valueOf(id));
		tv2.setText(name);
		tv3.setText(latLngList);

		// 해당 뷰 클릭 이벤트 
		view.setOnClickListener(new FavoriteLayoutEvent());
		
		// 해당 뷰 수정 이벤트
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				TextView tv1 = (TextView) view.findViewById(R.id.textview_favorite_id);
				TextView tv2 = (TextView) view.findViewById(R.id.textview_favorite_name);
				
				long id = Long.parseLong(tv1.getText().toString());
				String name = tv2.getText().toString();
				
				showModifyFavoriteDialog(view, id, name);
				return false;
			}
		});
		
		Log.d("addFavorite", id + ", " + name + ", " + latLngList);
		layoutFavorite.addView(view);
	}
	
	// 마커 생성
	private void createMarker(LatLng latLng, int what, boolean moveCenter) {
		// 마커 위치 설정
		switch (what) {
		case Protocol.MARKER_ME :
			markerMyLocation.position(latLng);
			break;
		case Protocol.MARKER_CAR :
			markerCar.position(latLng);
			break;
		}
		
		// 포인트, 내 위치, picar 위치에 대한 마커 생성
		mapView.clear();
		if (markerPointSearchFlag) {
			ArrayList<MarkerOptions> list = new ArrayList<MarkerOptions>();
			for (int i=0; i<latLngList.size(); i++) {
				MarkerOptions marker = new MarkerOptions();
				marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point));
				marker.position(latLngList.get(i));
				list.add(marker);
			}
			
			for (int i=0; i<list.size(); i++) {
				mapView.addMarker(list.get(i)).showInfoWindow();
			}
		}
		
		if (markerMyLocationSearchFlag) {
			mapView.addMarker(markerMyLocation);
		}
		
		if (markerCarSearchFlag) {
			mapView.addMarker(markerCar);
		}
		
		// 마커 생성위치로 카메라 이동
		if (moveCenter) {
			mapView.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		}
	}
	
	// 위치 서비스 사용 설정
	private void checkGPSEnable() {
		if (null != locationManager) {
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if (!isGPSEnabled) {
				showSettingsDialog();
			} else {
				if (!nMapLocationManager.isMyLocationEnabled()) {
					nMapLocationManager.enableMyLocation(true);
					nMapLocationManager.setOnLocationChangeListener(locationListener);
				}
			}
		}
	}
	
	
	
	
	/*****************************
	 ******* 대화상자 TODO *******
	 *****************************/
	
	// 위치 서비스 사용 설정 대화상자
	private void showSettingsDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("위치 서비스 사용 설정");
		alertDialog.setMessage("PiCar 지도에서 위치 정보를 사용하려면\n위치 서비스 권한을 허용해주세요.");
		
		alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});
		
		alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Toast.makeText(mContext, "위치 서비스 미동의\n내 위치를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
			}
		});
		
		alertDialog.show();
	}
	
	// 즐겨찾기 포인트 대화상자
	private void showModifyFavoriteDialog(final View view, final long id, final String name) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("저장된 포인트 변경");
		alertDialog.setMessage("'" + name + "' 변경하시겠습니까?");
		
		alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				view.setOnClickListener(null);
				view.setVisibility(View.GONE);
				dbManager.delete(id);
				Toast.makeText(mContext, "'" + name + "' 삭제되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
		alertDialog.setNeutralButton("이름 변경", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				showRenameFavoriteDialog(view, id, name);
			}
		});
		alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
	
	// 즐겨찾기 포인트 이름 변경 대화상자
	private void showRenameFavoriteDialog(final View view, final long id, final String name) {
		final EditText edit = new EditText(this);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("'" + name + "' 이름 변경");
		alertDialog.setView(edit);
		
		alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String changedName = edit.getText().toString();
				TextView tv1 = (TextView) view.findViewById(R.id.textview_favorite_name);
				tv1.setText(changedName);
				dbManager.update(id, changedName);
				Toast.makeText(mContext, "'" + name + "' 에서 '" + changedName + "' 으로\n"
														+ "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
		alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
	
	
	
	
	/***************************
	 ******* 리스너 TODO *******
	 ***************************/
	
	// 네이버맵 내 위치 위치 검색 이벤트
	private class MyLocationListener implements OnLocationChangeListener {
		@Override
		public boolean onLocationChanged(NMapLocationManager manager, NGeoPoint point) {
			markerMyLocationSearchFlag = true;
			double lat = Math.round(point.getLatitude() * 100000.0) / 100000.0;
			double lon = Math.round(point.getLongitude() * 100000.0) / 100000.0;
			LatLng latLng = new LatLng(lat, lon);
			createMarker(latLng, Protocol.MARKER_ME, markerMyLocationCenterFlag);
			markerMyLocationCenterFlag = false;
			
			return true; // true : 계속해서 내 위치 검색
		}

		@Override
		public void onLocationUnavailableArea(NMapLocationManager arg0,	NGeoPoint arg1) { }

		@Override
		public void onLocationUpdateTimeout(NMapLocationManager arg0) { }
	}
	
	// 구글맵 클릭 이벤트
	private class GoogleMapClickEvent implements OnMapClickListener {
		@Override
		public void onMapClick(LatLng latLng) {
			if (isMapLongClicked) {
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_map_bottom_gone);
				layoutMapBottom.startAnimation(animation);
				layoutMapBottom.setVisibility(View.GONE);
				isMapLongClicked = false;
				
			}
			
			// 포인트 마커 제거
			markerPointSearchFlag = false;
			latLngList.clear();
			createMarker(null, -1, false);
		}
	}
	
	// 구글맵 롱클릭 이벤트 : 마커 생성 이벤트
	private class GoogleMapMarkerEvent implements OnMapLongClickListener {
		@Override
		public void onMapLongClick(LatLng latLng) {
			// 맵 하단부 오픈
			isMapLongClicked = true;
			layoutMapBottom.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_map_bottom_visible);
			layoutMapBottom.startAnimation(animation);

			// 콜렉션 추가
//			double lat = Math.round(latLng.latitude*100000.0)/100000.0;
//			double lon = Math.round(latLng.longitude*100000.0)/100000.0;
			latLngList.add(latLng);
			
			// 마커 생성
			markerPointSearchFlag = true;
			createMarker(null, Protocol.MARKER_POINT, false);
		}
	}

	// 마커 말풍선 어댑터 (말풍선 커스텀) - 말풍선 == 이미지
	private class MarkerInfoWindowAdapter implements InfoWindowAdapter {
		@Override
		public View getInfoContents(Marker marker) {
			View v = inflater.inflate(R.layout.marker_info_window, (ViewGroup) findViewById(R.id.layout_info_window_root));
			return v;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
	}
	
	// 말풍선 클릭 이벤트 (PiCar 이동)
	private class InfoWindowClickEvent implements OnInfoWindowClickListener {
		@Override
		public void onInfoWindowClick(Marker marker) {
			markerCarSearchFlag = true;
			if (markerCarSearchFlag) {
				// 마커의 좌표
				String s = "";
				for (int i=0; i<latLngList.size(); i++) {
					LatLng latLng = latLngList.get(i);
					s += latLng.latitude + ",";
					s += latLng.longitude + "/";
				}
				
				Log.d("movePicar", s);
				
				// 임시 메시지 작성
				byte[] temp = new byte[200];
				temp[0] = Protocol.PROTOCOL_GPS;
				
				for (int i=0; i<s.length(); i++) {
					temp[i+1] = (byte) s.charAt(i);
				}
				
				// 메시지 크기 조절
				byte[] message = new byte[s.length()+1];
				for (int i=0; i<message.length; i++) {
					message[i] = temp[i];
				}
				
				// 전송
				safetyThread.setFlag(false);
				new SendThread(message).start();
			} else {
				Toast.makeText(mContext, "PiCar가 GPS 신호를 받을 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
	
	/********************************
	 ******* 기타 버튼 이벤트 *******
	 ********************************/
	
	// 카메라 버튼 이벤트
	private class CameraButtonEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			isCamera = !isCamera;
			if (isCamera) {
				layoutMap.setVisibility(View.GONE);
				webCamera.setVisibility(View.VISIBLE);
				webCamera.loadUrl("http://" + Protocol.AP_IP + ":8080/stream_simple.html");
				// Log.d("webCamera size : ", "X = " + webCamera.getWidth() + ", Y = " + webCamera.getHeight());
				btnCamera.setImageResource(R.drawable.btn_camera_on);
			}
			else {
				layoutMap.setVisibility(View.VISIBLE);
				webCamera.setVisibility(View.GONE);
				btnCamera.setImageResource(R.drawable.btn_camera_off);
				checkGPSEnable();
			}
		}
	}
	
	// 위치 보정 버튼 이벤트
	private class AdjustButtonEvent implements OnClickListener {
		@Override
		public void onClick(View v) {

		}
	}
	
	// 도움말 버튼 이벤트
	private class HelpButtonEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			isHelpClicked = !isHelpClicked;
			if (isHelpClicked) {
				btnFavorite.setClickable(false);
				btnHelp.setBackgroundResource(R.drawable.btn_help_c);
				layoutPager.setVisibility(View.VISIBLE);
			} else {
				btnFavorite.setClickable(true);
				btnHelp.setBackgroundResource(R.drawable.btn_help);
				layoutPager.setVisibility(View.GONE);
			}
		}
	}
	
	// 즐겨찾기 버튼 이벤트
	private class FavoriteButtonEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			isFavoriteClicked = !isFavoriteClicked;
			if (isFavoriteClicked) {
				btnFavorite.setBackgroundResource(R.drawable.btn_favorite_c);
				containerFavorite.setVisibility(View.VISIBLE);
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.container_favorite_visible);
				containerFavorite.startAnimation(animation);
			} else {
				btnFavorite.setBackgroundResource(R.drawable.btn_favorite);
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.container_favorite_gone);
				containerFavorite.startAnimation(animation);
				containerFavorite.setVisibility(View.GONE);
			}
		}
	}
	
	// 즐겨찾기 포인트 클릭 이벤트
	private class FavoriteLayoutEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			latLngList.clear();
			RelativeLayout layout = (RelativeLayout) v;
			TextView textView = (TextView) layout.findViewById(R.id.textview_favorite_latlng);
			
			// Original : s == lat/lng: (lat,lng)
			String s = textView.getText().toString(); // s == lat,lng/lat,lng/...
			Log.d("FavoriteLayoutEvent", s);
			String[] sa = s.split("/");
			
			for (int i=0; i<sa.length; i++) {
				double lat = Double.parseDouble(sa[i].split(",")[0]);
				double lng = Double.parseDouble(sa[i].split(",")[1]);
				LatLng latLng = new LatLng(lat, lng);
				latLngList.add(latLng);
			}

			// 마커 생성
			markerPointSearchFlag = true;
			createMarker(latLngList.get(latLngList.size()-1), Protocol.MARKER_POINT, true);
		}
	}
	
	// 즐겨찾기 저장 클릭 이벤트
	private class FavoriteSaveEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			long id = System.currentTimeMillis();
			String name = "이름 없는 포인트";
			
			String s = "";
			for (int i=0; i<latLngList.size(); i++) {
				LatLng latLng = latLngList.get(i);
				// DB 추가
				dbManager.insert(id, name, latLng.latitude, latLng.longitude);
				
				s += String.valueOf(latLng.latitude);
				s += ",";
				s += String.valueOf(latLng.longitude);
				s += "/";
			}
			
			// 즐겨찾기 추가
			addFavorite(id, name, s);
		}
	}
	
	private class ViewPagerChangeListener implements OnPageChangeListener {
		
		ImageView[] views;
		
		public ViewPagerChangeListener() {
			views = new ImageView[pagerAdapter.getCount()];
			views[0] = (ImageView) findViewById(R.id.imageview_pager_indicator1);
			views[1] = (ImageView) findViewById(R.id.imageview_pager_indicator2);
			views[2] = (ImageView) findViewById(R.id.imageview_pager_indicator3);
			views[3] = (ImageView) findViewById(R.id.imageview_pager_indicator4);
			views[4] = (ImageView) findViewById(R.id.imageview_pager_indicator5);
			views[5] = (ImageView) findViewById(R.id.imageview_pager_indicator6);
			views[6] = (ImageView) findViewById(R.id.imageview_pager_indicator7);
			views[7] = (ImageView) findViewById(R.id.imageview_pager_indicator8);
			views[8] = (ImageView) findViewById(R.id.imageview_pager_indicator9);
			
			for (int i=0; i<views.length; i++) {
				final int position = i;
				views[i].setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						pager.setCurrentItem(position);
					}
				});
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) { }

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) { }

		@Override
		public void onPageSelected(int position) {
			for (ImageView view : views)
				view.setImageResource(R.drawable.dot_gray);
			
			views[position].setImageResource(R.drawable.dot_blue);
		}
			
	}
	
	
	
	
	/********************************
	 ******* PI CAR 제어 TODO *******
	 ********************************/
	
	// 모터 제어 조이스틱 이벤트
	private class JoyStickEvent implements OnTouchListener {
		
		private JoyStickView joyStick;
		private ArrayList<View> viewList;
		private boolean[] flagList;
		private ArrayList<Animation> animList;
		
		public JoyStickEvent(JoyStickView joyStick) {
			this.joyStick = joyStick;
			viewList = new ArrayList<View>();
			viewList.add(findViewById(R.id.button_forward_animation));
			viewList.add(findViewById(R.id.button_right_animation));
			viewList.add(findViewById(R.id.button_backward_animation));
			viewList.add(findViewById(R.id.button_left_animation));
			
			flagList = new boolean[viewList.size()];
			for (int i=0; i<viewList.size(); i++) {
				flagList[i] = false;
			}
			
			animList = new ArrayList<Animation>();
			for (int i=0; i<viewList.size(); i++) {
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.btn_control_alpha);
				animation.setAnimationListener(new JoyStickDirectionAnimation(viewList.get(i)));
				animList.add(animation);
			}
		}
		
		@Override
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouch(View v, MotionEvent e) {
			joyStick.drawStick(e);
			byte[] message = null;
			byte[] temp1 = new byte[] {Protocol.PROTOCOL_MOTOR, Protocol.MOTOR_STOP};
			
            if(e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            	switch (joyStick.get8Direction()) {
            	case JoyStickView.STICK_UP:
            		temp1[1] = Protocol.MOTOR_FORWARD;
            		selectDirection(new int[]{0}, new int[]{1, 2, 3});
            		break;
            	case JoyStickView.STICK_UPRIGHT:
            		temp1[1] = Protocol.MOTOR_FORWARD_RIGHT;
            		selectDirection(new int[]{0, 1}, new int[]{2, 3});
            		break;
            	case JoyStickView.STICK_RIGHT:
            		temp1[1] = Protocol.MOTOR_RIGHT;
            		selectDirection(new int[]{1}, new int[]{0, 2, 3});
            		break;
            	case JoyStickView.STICK_RIGHTDOWN:
            		temp1[1] = Protocol.MOTOR_RIGHT_BACKWARD;
            		selectDirection(new int[]{1, 2}, new int[]{0, 3});
            		break;
            	case JoyStickView.STICK_DOWN:
            		temp1[1] = Protocol.MOTOR_BACKWARD;
            		selectDirection(new int[]{2}, new int[]{0, 1, 3});
            		break;
            	case JoyStickView.STICK_DOWNLEFT:
            		temp1[1] = Protocol.MOTOR_BACKWARD_LEFT;
            		selectDirection(new int[]{2, 3}, new int[]{0, 1});
            		break;
            	case JoyStickView.STICK_LEFT:
            		temp1[1] = Protocol.MOTOR_LEFT;
            		selectDirection(new int[]{3}, new int[]{0, 1, 2, });
            		break;
            	case JoyStickView.STICK_LEFTUP:
            		temp1[1] = Protocol.MOTOR_LEFT_FORWARD;
            		selectDirection(new int[]{0, 3}, new int[]{1, 2});
            		break;
            	case JoyStickView.STICK_NONE:
            		temp1[1] = Protocol.MOTOR_STOP;
            		selectDirection(new int[]{}, new int[]{0, 1, 2, 3});
            		break;
            	}
				
            	String temp2 = String.valueOf(joyStick.getDistancePercentage());
            	message = new byte[temp1.length + temp2.length()];
            	
            	int i = 0;
            	for (i=0; i<temp1.length; i++) {
            		message[i] = temp1[i];
            	}
            	
            	for (int j=0; j<temp2.length(); j++) {
            		message[i+j] = (byte) temp2.charAt(j);
            	}
            	
				safetyThread.setFlag(false);
				if (now + 100 < System.currentTimeMillis()) {
					new SendThread(message).start();
					now = System.currentTimeMillis();
				}
				
            } else if(e.getAction() == MotionEvent.ACTION_UP) {
            	safetyThread.setFlag(false);
            	safetyThread = new SafetyThread();
            	safetyThread.start();
            	selectDirection(new int[]{}, new int[]{0, 1, 2, 3});
            	new SendThread(temp1).start();
            }

            return true;
		}
		
		private void selectDirection(int[] selected, int[] unSelected) {
			for (int i=0; i<selected.length; i++) {
				viewList.get(selected[i]).clearAnimation();
				viewList.get(selected[i]).setPressed(true);
				flagList[selected[i]] = true;
			}
			
			for (int i=0; i<unSelected.length; i++) {
				if (flagList[unSelected[i]]) {
					viewList.get(unSelected[i]).startAnimation(animList.get(unSelected[i]));
					flagList[unSelected[i]] = false;
				}
			}
		}
	}
	
	// 모터 속도 제어 이벤트
	private class MotorSpeedControlEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_speed_up :
				if (motorSpeed < 6) {
					motorSpeed++;
				}
				break;
			case R.id.button_speed_down :
				if (motorSpeed > 1) {
					motorSpeed--;
				}
				break;
			}
	
			Toast.makeText(mContext, "최고 속도 : " + motorSpeed + " / 6", Toast.LENGTH_SHORT).show();
			
			byte value = (byte) (motorSpeed + 48);
			byte[] message = new byte[] {Protocol.PROTOCOL_MOTOR, Protocol.MOTOR_SPEED_CHANGE, value};
			new SendThread(message).start();
		}
	}
	
	
	
	
	/**********************
	 ******* 핸들러 *******
	 **********************/

	// UDP 패킷 수신 핸들러 : 수신 쓰레드 → 핸들러 → 메인 쓰레드로 메시지 전달
	@SuppressLint("HandlerLeak")
	public class UIHandler extends Handler {
		int gps_error_count = 0;
		@Override
		public void handleMessage(Message msg) {
			String receiveString = String.valueOf(msg.obj);
			switch (msg.what) {
			// 수신받은 메시지에서 좌표 추출
			case Protocol.MESSAGE_GPS:
				if (!"".equals(receiveString)) {
					double latitude = Double.parseDouble(receiveString.split(",")[0]);
					double longitude = Double.parseDouble(receiveString.split(",")[1]);
					LatLng latLng = new LatLng(latitude, longitude);
					
					// 지도에 마커 생성
					markerCarSearchFlag = true;
					createMarker(latLng, Protocol.MARKER_CAR, false);
					gps_error_count = 0;
				}
				break;
			case Protocol.MESSAGE_GPS_CALLBACK:
				if (receiveString.equals(Protocol.CALLBACK_NON_ACTION)) {
					Toast.makeText(mContext, "PiCar GPS 오류. GPS 모듈을 확인해주세요.", Toast.LENGTH_LONG).show();
				} else if (receiveString.equals(Protocol.CALLBACK_BIG_ERROR)) {
					if (gps_error_count++ > 10) {
						Toast.makeText(mContext, "PiCar의 GPS 오차가 너무 큽니다.", Toast.LENGTH_LONG).show();
						gps_error_count = 0;
					}
				}
				break;
			case Protocol.MESSAGE_DISTANCE:
				textviewDistance.setText("distance : " + receiveString + "m");
				break;
			}

			super.handleMessage(msg);
		}
	}
	
	
	
	
	/***********************
	 ******* 앱 제어 *******
	 ***********************/
	
	@Override
	protected void onResume() {
		checkGPSEnable();
		markerMyLocationCenterFlag = true;
		receiveThread = new ReceiveThread(new UIHandler());
		receiveThread.setFlag(true);
		receiveThread.start();
		safetyThread.setFlag(true);
		distanceThread.setFlag(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		nMapLocationManager.disableMyLocation();
		receiveThread.setFlag(false);
		receiveThread.finish();
		safetyThread.setFlag(false);
		distanceThread.setFlag(false);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		nMapLocationManager.disableMyLocation();
		receiveThread.setFlag(false);
		receiveThread.finish();
		safetyThread.setFlag(false);
		distanceThread.setFlag(false);
		super.onDestroy();
	}
}