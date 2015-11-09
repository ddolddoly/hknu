/*
 * motor.c
 *
 * Created on: 2014. 4. 3.
 * Author: Seokyong Hong
 */

#include "motor.h"

void motor_init(void) {
	/* Set the directions of OC0A and OC1B pins and those pins
	 * which set the directions of current to motors:
	 * <<Pin Description>
	 * 	- PortD PD6: PWM input to left motor
	 * 	- PortD PD7: current's direction for left motor input
	 * 	- PortD PD5: PWM input to right motor
	 * 	- PortD PD4: current's direction for right motor input
	 */
	DDRD |= _BV(DDD4) | _BV(DDD5) | _BV(DDD6) | _BV(DDD7);
}

void motor_forward(void) {
	PORTD |= _BV(PD4) | _BV(PD7);
	OCR0A = OCR0B = DEFAULT_MOTOR_SPEED;
}

void motor_backward(void) {
	PORTD &= ~(_BV(PD4) | _BV(PD7));
	OCR0A = OCR0B = DEFAULT_MOTOR_SPEED;
}

void motor_turn_left(void) {
	PORTD |= _BV(PD4);
	PORTD &= ~_BV(PD7);
	OCR0A = OCR0B = DEFAULT_MOTOR_SPEED;
}

void motor_turn_right(void) {
	PORTD |= _BV(PD7);
	PORTD &= ~_BV(PD4);
	OCR0A = OCR0B = DEFAULT_MOTOR_SPEED;
}

void motor_stop(void) {
	OCR0A = OCR0B = 0x00;
}

