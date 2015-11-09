package com.example.picar.thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.example.picar.Protocol;

import android.util.Log;

public class SendThread extends Thread implements Runnable {
	
	private byte[] message;
	
	public SendThread(byte[] message) {
		this.message = message;
	}

	@Override
	public void run() {
		try {
			InetAddress address = InetAddress.getByName(Protocol.AP_IP);
			DatagramPacket packet = new DatagramPacket(message, message.length, address, Protocol.AP_SEND_PORT);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			Log.d("SendThread", "send packet : " + Arrays.toString(byteToChar(packet.getData())));
			socket.close();
		} catch (Exception e) {
			Log.d("SendThread", "Error", e);
		}
	}
	
	private char[] byteToChar(byte[] in) {
		char[] out = new char[in.length];
		
		for (int i=0; i<in.length; i++) {
			out[i] = (char) in[i];
		}
		
		return out;
	}
}
