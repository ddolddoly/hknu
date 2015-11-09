package com.phantom.tankremotecontrollerapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class TankControlMain extends Activity {
	private static final byte PROTOCOL_MESSAGE_START = 0x01;
	private static final byte PROTOCOL_MESSAGE_END = 0x04;
	private static final byte PROTOCOL_TYPE_MOTOR = 'M';
	private static final byte PROTOCOL_TYPE_SENSOR = 'S';
	private static final byte PROTOCOL_MOTOR_FORWARD = 'F';
	private static final byte PROTOCOL_MOTOR_BACKWARD = 'B';
	private static final byte PROTOCOL_MOTOR_TURN_LEFT = 'L';
	private static final byte PROTOCOL_MOTOR_TURN_RIGHT = 'R';
	private static final byte PROTOCOL_MOTOR_STOP = 'S';
	private static final byte PROTOCOL_SENSOR_ULTRASONIC = 'U';
	
	private static final String TANK_IP_ADDRESS = "192.168.123.103";
	private static final int TANK_PORT = 2048;
	private static final String STREAMING_SERVER_URL = "http://192.168.123.103:8091/";
	
	private DatagramSocket socket;
	private boolean shouldStop = false;
	private Handler handler;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tank_control);	
		
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			Log.d("UDP Socket", "Error", e);
		}
		
		final Button buttonGoForward = (Button)findViewById(R.id.buttonGoForward);
		final Button buttonGoBackward = (Button)findViewById(R.id.buttonGoBackward);
		final Button buttonTurnLeft = (Button)findViewById(R.id.buttonTurnLeft);
		final Button buttonTurnRight = (Button)findViewById(R.id.buttonTurnRight);
		final VideoView videoView = (VideoView)findViewById(R.id.videoViewRPi);
		final TextView textView = (TextView)findViewById(R.id.textViewState);
  
		videoView.setVideoPath(STREAMING_SERVER_URL);
		videoView.setMediaController(new MediaController(this));
		videoView.requestFocus();
		videoView.start();
		
        buttonGoForward.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				byte []message = new byte[] { PROTOCOL_MESSAGE_START, PROTOCOL_TYPE_MOTOR, PROTOCOL_MOTOR_STOP, PROTOCOL_MESSAGE_END };
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					buttonGoForward.setBackgroundResource(R.drawable.forward_on);
					message[2] = PROTOCOL_MOTOR_FORWARD;
					sendUDPMessage(message);
					break;
				case MotionEvent.ACTION_UP:
					buttonGoForward.setBackgroundResource(R.drawable.forward_off);
					sendUDPMessage(message);
					break;
				}
				return true;
			}
        });
        
        buttonGoBackward.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				byte []message = new byte[] { PROTOCOL_MESSAGE_START, PROTOCOL_TYPE_MOTOR, PROTOCOL_MOTOR_STOP, PROTOCOL_MESSAGE_END };
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					buttonGoBackward.setBackgroundResource(R.drawable.backward_on);
					message[2] = PROTOCOL_MOTOR_BACKWARD;
					sendUDPMessage(message);
					break;
				case MotionEvent.ACTION_UP:
					buttonGoBackward.setBackgroundResource(R.drawable.backward_off);
					sendUDPMessage(message);
					break;
				}
				return true;
			}
		});
        
        buttonTurnLeft.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				byte []message = new byte[] { PROTOCOL_MESSAGE_START, PROTOCOL_TYPE_MOTOR, PROTOCOL_MOTOR_STOP, PROTOCOL_MESSAGE_END };
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					buttonTurnLeft.setBackgroundResource(R.drawable.turnleft_on);
					message[2] = PROTOCOL_MOTOR_TURN_LEFT;
					sendUDPMessage(message);
					break;
				case MotionEvent.ACTION_UP:
					buttonTurnLeft.setBackgroundResource(R.drawable.turnleft_off);
					sendUDPMessage(message);
					break;
				}
				return true;
			}
		});
        
        buttonTurnRight.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				byte []message = new byte[] { PROTOCOL_MESSAGE_START, PROTOCOL_TYPE_MOTOR, PROTOCOL_MOTOR_STOP, PROTOCOL_MESSAGE_END };
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					buttonTurnRight.setBackgroundResource(R.drawable.turnright_on);
					message[2] = PROTOCOL_MOTOR_TURN_RIGHT;
					sendUDPMessage(message);
					break;
				case MotionEvent.ACTION_UP:
					buttonTurnRight.setBackgroundResource(R.drawable.turnright_off);
					sendUDPMessage(message);
					break;
				}
				return true;
			}
		});
        
        handler = new Handler() {
        	public void handleMessage(Message message) {
        		int distance = message.getData().getInt("distance");
        		textView.setText("Distance: " + distance + " Cm");
        	}
        };
        
        new Thread(new UDPReceiver()).start();
	} 
	
	protected void onDestroy() {
		shouldStop = true;
	}
	
	private void sendUDPMessage(byte []message) {
		try {
			InetAddress address = InetAddress.getByName(TANK_IP_ADDRESS); 
			DatagramPacket packet = new DatagramPacket(message, message.length, address, TANK_PORT);
			socket.send(packet);
		}
		catch (Exception e) {
			Log.d("UDP Socket", "Error", e);
		}	
	}
	
	private class UDPReceiver implements Runnable {
		public void run() {
			byte []buffer = new byte[4];
			
			while(!shouldStop) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(packet);
					Message message = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putInt("distance", buffer[2]);
					message.setData(bundle);
					handler.sendMessage(message);
				} catch (IOException e) {
					Log.d("UDP Socket", "Error", e);
				}
				
			}
		}
	}
}
