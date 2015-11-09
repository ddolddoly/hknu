/*
 * avr.c
 *
 * Created on: 2014. 4. 3.
 * Author: Seokyong Hong
 */

#include "avr.h"

void init() {
	timer_init();
	motor_init();
	uart_init();
	queue_init();
	ultrasonic_init();
	sei();
}

