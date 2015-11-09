/*
 * main.c
 *
 * Created on: 2014. 4. 2.
 * Author: Seokyong Hong
 */

#include "system/avr.h"

static void process_command(void);

int main(void) {
	init();

	while(1) {
		process_command();
		delay(10);
	}

	return 0;
}

void process_command(void) {
	long distance;
	unsigned char message[4];
	p_node command = queue_remove();

	if(command != NULL) {
		switch(command -> type) {
		case PROTOCOL_TYPE_MOTOR:
			switch(command -> data) {
			case PROTOCOL_MOTOR_FORWARD:
				motor_forward();
				break;
			case PROTOCOL_MOTOR_BACKWARD:
				motor_backward();
				break;
			case PROTOCOL_MOTOR_TURN_LEFT:
				motor_turn_left();
				break;
			case PROTOCOL_MOTOR_TURN_RIGHT:
				motor_turn_right();
				break;
			case PROTOCOL_MOTOR_STOP:
				motor_stop();
				break;
			}
			break;
		case PROTOCOL_TYPE_SENSOR:
			switch(command -> data) {
			case PROTOCOL_SENSOR_ULTRASONIC:
				distance = ultrasonic_distance();
				message[0] = PROTOCOL_OPEN_CHARACTER;
				message[1] = PROTOCOL_TYPE_SENSOR;
				message[2] = (distance >= 255) ? 255 : distance;
				message[3] = PROTOCOL_CLOSE_CHARACTER;
				uart_write(message, 4);
				break;
			}
			break;
		}

		free(command);
	}
}

