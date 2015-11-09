#include <Makeblock.h>
#include <SoftwareSerial.h>
#include <Wire.h>

// �ϵ���� �߻�ȭ
MeDCMotor MotorL(M1);
MeDCMotor MotorR(M2);
MeUltrasonicSensor UltrasonicSensor(PORT_3);

// ���� ���� ����
int moveSpeed = 160;
int minSpeed = 45;
int factor = 23;

boolean isAuto = false;
boolean leftflag, rightflag;
int distance = 0;
int randnum = 0;

// �Ƶ��̳� �ʱ�ȭ - ���� 1ȸ ����
void setup() {
	leftflag = false;
	rightflag = false;
	randomSeed(analogRead(0));
	Serial.begin(9600);
}

// �Ƶ��̳� ���� �� �ݺ� ����
void loop() {
	// �Է��� �ִ� ���
	if (Serial.available()) {
		// �Է� ���ڸ� ���ڿ��� ��ģ��
		String readString = "";
		char[] ca = new char[10];
		while (Serial.available()) {
			delay(3);
			if (Serial.available() > 0) {
				char c = Serial.read();
				readString += c;
			}
		}

		// ���� ���� ���������� MXXXX ==> ex) M8100 : motorForward = ���� 100% �Ŀ�
		if (readString.charAt(0) == 'M') {
			int power = getPower(readString);
			switch (readString.charAt(1)) {
			case '8': {
				motorForward(power);
				break;
			}
			case '9': {
				motorForwardRight(power);
				break;
			}
			case '6': {
				motorRight(power);
				break;
			}
			case '3': {
				motorRightBackward(power);
				break;
			}
			case '2': {
				motorBackward(power);
				break;
			}
			case '1': {
				motorBackwardLeft(power);
				break;
			}
			case '4': {
				motorLeft(power);
				break;
			}
			case '7': {
				motorLeftForward(power);
				break;
			}
			
			case 'S': {
				motorStop();
			}
			case 'P':
				char c = readString.charAt(2);
				ChangeSpeed(c * factor + minSpeed);
				break;
			}
		}
	}
}

// �Ŀ� �ۼ�Ƽ�� ���ϱ�
int getPower(String s) {
	String s2 = "";
	for (int i=2; i<s.length(); i++)
		s2 += s.charAt(i);
	return s2.toInt();
}

// ����
void motorForward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(speed);
}

// ���� + ��ȸ��
void motorForwardRight(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(speed / 2);
}

// ��ȸ��
void motorRight(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(-speed);
}

// ��ȸ�� + ����
void motorRightBackward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(-speed / 2);
}

// ����
void motorBackward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(-speed);
}

// ���� + ��ȸ��
void motorBackwardLeft(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed / 2);
	MotorR.run(-speed);
}

// ��ȸ��
void motorLeft(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(speed);
}

// ��ȸ�� + ����
void motorLeftForward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed / 2);
	MotorR.run(speed);
}


// ����
void motorStop() {
	MotorL.run(0);
	MotorR.run(0);
}

// �ӵ� ����
void ChangeSpeed(int spd) {
	moveSpeed = spd;
}





// �ڵ� ���� ���
void autoDrive() {
	// ������ �Ÿ� ������ �������� �д´�
	distance = UltrasonicSensor.distanceCm();
	randomSeed (analogRead(A4));
	
	// distance�� 0�� ���� ���� ������� �����Ѵ�
	if (distance > 0) {
		if (distance>10 && distance<40) {
			randnum = random(300);
			if(randnum > 150 && !rightflag) {
				leftflag = true;
				motorLeft(100);
			}
			else {
				rightflag = true;
				motorRight(100);
			}
		}
		else if (distance < 10) {
			randnum = random(300);
			if(randnum > 150)
				motorBackwardLeft(100);
			else
				motorRightBackward(100);
		}
		else {
			leftflag = false;
			rightflag = false;
			motorForward(100);
		}
	}
	
	delay(50);
}
