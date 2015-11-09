/*
 * ultrasonic.c
 *
 * Created on: 2014. 4. 27.
 * Author: Seokyong Hong
 */

#include "ultrasonic.h"

void ultrasonic_init(void) {
	// Nothing to be done
}

long ultrasonic_distance(void) {
	unsigned long loops = 0;
	unsigned long width = 0;
	unsigned long max_loops = CLOCK_CYCLES_PER_MICROSECOND * ULTRASONIC_READ_TIMEOUT / 16;

	DDRC |= _BV(DDC1);
	PORTC &= ~_BV(PC1);
	micro_delay(2);
	PORTC |= _BV(PC1);
	micro_delay(10);
	PORTC &= ~_BV(PC1);

	DDRC &= ~_BV(DDC1);

	while(PINC & _BV(PC1))
		if(loops++ == max_loops)
			return 0;

	while(!(PINC & _BV(PC1)))
		if(loops++ == max_loops)
			return 0;

	while(PINC & _BV(PC1)) {
		if(loops++ == max_loops)
			return 0;
		width++;
	}

	return ((((width * 19 + 16) / CLOCK_CYCLES_PER_MICROSECOND) / 29) >> 1);
}

