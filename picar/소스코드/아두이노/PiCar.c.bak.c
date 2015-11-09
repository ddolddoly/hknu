#include <Makeblock.h>
#include <SoftwareSerial.h>
#include <Wire.h>

// 하드웨어 추상화
MeDCMotor MotorL(M1);
MeDCMotor MotorR(M2);
MeUltrasonicSensor UltrasonicSensor(PORT_3);

// 모터 제어 변수
int moveSpeed = 160;
int minSpeed = 45;
int factor = 23;

boolean isAuto = false;
boolean leftflag, rightflag;
int distance = 0;
int randnum = 0;

// 아두이노 초기화 - 최초 1회 실행
void setup() {
	leftflag = false;
	rightflag = false;
	randomSeed(analogRead(0));
	Serial.begin(9600);
}

// 아두이노 동작 중 반복 실행
void loop() {
	// 입력이 있는 경우
	if (Serial.available()) {
		// 입력 문자를 문자열로 합친다
		String readString = "";
		char[] ca = new char[10];
		while (Serial.available()) {
			delay(3);
			if (Serial.available() > 0) {
				char c = Serial.read();
				readString += c;
			}
		}

		// 모터 제어 프로토콜은 MXXXX ==> ex) M8100 : motorForward = 전진 100% 파워
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

// 파워 퍼센티지 구하기
int getPower(String s) {
	String s2 = "";
	for (int i=2; i<s.length(); i++)
		s2 += s.charAt(i);
	return s2.toInt();
}

// 전진
void motorForward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(speed);
}

// 전진 + 우회전
void motorForwardRight(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(speed / 2);
}

// 우회전
void motorRight(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed);
	MotorR.run(-speed);
}

// 우회전 + 후진
void motorRightBackward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(-speed / 2);
}

// 후진
void motorBackward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(-speed);
}

// 후진 + 좌회전
void motorBackwardLeft(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed / 2);
	MotorR.run(-speed);
}

// 좌회전
void motorLeft(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(-speed);
	MotorR.run(speed);
}

// 좌회전 + 전진
void motorLeftForward(int power) {
	int speed = (int)(moveSpeed * (power/100.0));
	MotorL.run(speed / 2);
	MotorR.run(speed);
}


// 정지
void motorStop() {
	MotorL.run(0);
	MotorR.run(0);
}

// 속도 조절
void ChangeSpeed(int spd) {
	moveSpeed = spd;
}





// 자동 운전 모드
void autoDrive() {
	// 초음파 거리 센서의 센서값을 읽는다
	distance = UltrasonicSensor.distanceCm();
	randomSeed (analogRead(A4));
	
	// distance가 0인 경우는 센서 오류라고 가정한다
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
