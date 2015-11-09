


#define MOTOR_L_POWER 12
#define MOTOR_R_POWER 13
#define MOTOR_L_BREAK 9
#define MOTOR_R_BREAK 8
#define MOTOR_L 3
#define MOTOR_R 11

int moveSpeed = 255;
int minSpeed = 45;
int factor = 23;

int distance = 0;
int randnum = 0;
boolean leftflag, rightflag;

boolean isUltra = false;


void setup() {
	
	// 채널 A - Left Motor
	pinMode (MOTOR_L_POWER, OUTPUT);
	pinMode (MOTOR_L_BREAK, OUTPUT);

	// 채널 B - Right Motor
	pinMode (MOTOR_R_POWER, OUTPUT);
	pinMode (MOTOR_R_BREAK, OUTPUT);
	
	Serial.begin(9600);
}


void loop() {
	if (Serial.available()) {
		String readString = "";
		while (Serial.available()) {
			delay(3);
			if (Serial.available() > 0) {
				char c = Serial.read();
				readString += c;
			}
		}
		
		if (readString.charAt(0) == 'M') {
			switch (readString.charAt(1)) {
			case 'f' : case 'F':
				motor_forward();
				break;
			case 'b': case 'B':
				motor_backward();
				break;
			case 'l': case 'L':
				motor_turnLeft();
				break;
			case 'r': case 'R':
				motor_turnRight();
				break;
			case 's': case 'S':
				motor_stop();
				break;
			case 'm': case 'A':
				isUltra = !isUltra;
				break;
				
			case 'p': case 'P':
				char c = readString.charAt(2);
				ChangeSpeed((c - '0') * factor + minSpeed);
				break;
			}
		}
	} else {
		if (isUltra) {
			//ultraCarProcess();
		}
		else {
			//motor_stop();
		}
	}
}


// 모터 제어
void motor_forward() {
	digitalWrite(MOTOR_L_POWER, HIGH);
	digitalWrite(MOTOR_R_POWER, HIGH);
	analogWrite(MOTOR_L, moveSpeed);
	analogWrite(MOTOR_R, moveSpeed);
}

void motor_backward() {
	digitalWrite(MOTOR_L_POWER, LOW);
	digitalWrite(MOTOR_R_POWER, LOW);
	analogWrite(MOTOR_L, moveSpeed);
	analogWrite(MOTOR_R, moveSpeed);
}

void BackwardAndTurnLeft() {
	digitalWrite(MOTOR_L_POWER, LOW);
	digitalWrite(MOTOR_R_POWER, LOW);
	analogWrite(MOTOR_L, moveSpeed / 2);
	analogWrite(MOTOR_R, moveSpeed);
}

void BackwardAndTurnRight() {
	digitalWrite(MOTOR_L_POWER, LOW);
	digitalWrite(MOTOR_R_POWER, LOW);
	analogWrite(MOTOR_L, moveSpeed);
	analogWrite(MOTOR_R, moveSpeed / 2);
}

void motor_turnLeft() {
	digitalWrite(MOTOR_L_POWER, LOW);
	digitalWrite(MOTOR_R_POWER, HIGH);
	analogWrite(MOTOR_L, moveSpeed / 2);
	analogWrite(MOTOR_R, moveSpeed);
}

void motor_turnRight() {
	digitalWrite(MOTOR_L_POWER, HIGH);
	digitalWrite(MOTOR_R_POWER, LOW);
	analogWrite(MOTOR_L, moveSpeed);
	analogWrite(MOTOR_R, moveSpeed / 2);
}

void motor_stop() {
	analogWrite(MOTOR_L, 0);
	analogWrite(MOTOR_R, 0);
}

void ChangeSpeed(int spd) {
	moveSpeed = spd;
}


// 센서


