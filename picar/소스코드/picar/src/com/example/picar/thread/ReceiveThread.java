package com.example.picar.thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.Message;
import android.util.Log;

import com.example.picar.MainActivity.UIHandler;
import com.example.picar.Protocol;

public class ReceiveThread extends Thread implements Runnable {
	
	private DatagramSocket socket;
	private UIHandler handler;
	private boolean run = false;
	private byte[] buffer = new byte[50];
	
	public ReceiveThread(UIHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			InetAddress address = InetAddress.getByName(Protocol.WIFI_IP);
			socket = new DatagramSocket(Protocol.WIFI_RECEIVE_PORT, address);
		} catch (Exception e) {
			Log.d("ReceiveThread", "Create Error", e);
		}
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		String receiveString;
		
		while (run) {
			// 초기화
			receiveString = "";
			for (int i=0; i<buffer.length; i++) {
				buffer[i] = -1;
			}
			
			// 패킷 수신 후 문자열로 변환
			try {
				socket.receive(packet);
				
				for (int i=0; i<buffer.length; i++) {
					if (buffer[i] == -1) {
						break;
					}
					char a = (char) buffer[i];
					receiveString += a;
				}
				
				// 핸들러로 메시지 전송
				Message msg = handler.obtainMessage();
				if ('G' == receiveString.charAt(0))
					msg.what = Protocol.MESSAGE_GPS_CALLBACK;
				else
					msg.what = Protocol.MESSAGE_GPS;
				msg.obj = receiveString;
				handler.sendMessage(msg);
				Log.d("ReceiveTask", "receive packet : " + receiveString);
				Thread.sleep(50);
			} catch (Exception e) {
				Log.d("ReceiveTask", "error", e);
			}
		}
	}
	
	public void setFlag(boolean run) {
		this.run = run;
	}
	
	public void finish() {
		if (socket != null) {
			socket.disconnect();
			socket.close();
		}
		interrupt();
	}
}
