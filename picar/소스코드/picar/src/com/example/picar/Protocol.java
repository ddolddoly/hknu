package com.example.picar;

public class Protocol {

	// PiCar AP
	public static final String PICAR_IP = "10.0.0.1";
	public static final String WIFI_SSID = "PiCar";
	public static final String WIFI_PASSWORD = "****";
	
	// 타입
	public static final byte PROTOCOL_MOTOR = 'M';
	public static final byte PROTOCOL_GPS = 'G';
	
	// 모터 조종 프로토콜
	public static final byte MOTOR_FORWARD = '8';
	public static final byte MOTOR_FORWARD_RIGHT = '9';
	public static final byte MOTOR_RIGHT = '6';
	public static final byte MOTOR_RIGHT_BACKWARD = '3';
	public static final byte MOTOR_BACKWARD = '2';
	public static final byte MOTOR_BACKWARD_LEFT = '1';
	public static final byte MOTOR_LEFT = '4';
	public static final byte MOTOR_LEFT_FORWARD = '7';
	public static final byte MOTOR_STOP = 'S';
	public static final byte MOTOR_SPEED_CHANGE = 'P';
	
	// 핸들러 메시지 프로토콜
	public static final int MESSAGE_GPS = 1;
	public static final int MESSAGE_GPS_CALLBACK = 2;
	public static final String CALLBACK_NON_ACTION = "GN";
	public static final String CALLBACK_BIG_ERROR = "GE";
	public static final int MESSAGE_DISTANCE = 3;
	
	// 마커 구분
	public static final byte MARKER_POINT = 0;
	public static final byte MARKER_ME = 1;
	public static final byte MARKER_CAR = 2;

	// IP, 포트
	public static String WIFI_IP = "0.0.0.0";
	public static String AP_IP = "0.0.0.0";
	public static final int WIFI_RECEIVE_PORT = 2049;
	public static final int AP_SEND_PORT = 2048;
	
	public static void setWifiIp(String ip) {
		WIFI_IP = ip;
	}
	
	public static void setApIp(String ip) {
		AP_IP = ip;
	}
}
