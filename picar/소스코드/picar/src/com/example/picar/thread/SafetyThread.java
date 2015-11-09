package com.example.picar.thread;

import com.example.picar.Protocol;

public class SafetyThread extends Thread implements Runnable {
	
	private boolean flag = true;
	
	@Override
	public void run() {
		byte[] message = null;

		while (true) {
			if (flag) {
				try {
					Thread.sleep(50);
					message = new byte[] {Protocol.PROTOCOL_MOTOR, Protocol.MOTOR_STOP};
					new SendThread(message).start();
					Thread.sleep(450);
				} catch (Exception e) {
					
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				
			}
		}
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
