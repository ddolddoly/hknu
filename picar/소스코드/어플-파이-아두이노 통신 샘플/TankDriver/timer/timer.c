/*
 * timer.c
 *
 * Created on: 2014. 4. 3.
 * Author: Seokyong Hong
 */

#include "timer.h"

/*
 * In this project, an 8-bit timer, Timer/Counter0, is used for generating
 * Phase Correct PWM (mode 1) output. The other 8-bit timer, Timer/Counter2,
 * is used to implement a set of important timing functions (e.g., delay).
 */
void timer_init(void) {
	// Set Timer/Counter0 prescaler to 64
	TCCR0B |= _BV(CS01) | _BV(CS00);

	// Set PWM mode to Phase Correct PWM (mode 1)
	TCCR0A |= _BV(WGM00);

	// Set Timer/Counter0 couting mode to non-inverting
	TCCR0A |= _BV(COM0A1) | _BV(COM0B1);

	// Set Timer/Counter2 prescaler to 64
	TCCR2B |=  _BV(CS22);

	// Set Timer/Counter2 mode to normal operation (mode 0)
	// Disconnect OC2x
	// Hence, do nothing.

	// Enable Timer/Counter2 overflow interrupt
	TIMSK2 |= _BV(TOIE2);
}

unsigned long micros(void) {
	unsigned long overflows;
	uint8_t sreg = SREG, tick_count;

	cli();
	overflows = timer2_overflow_count;
	tick_count = TCNT2;

	if((TIFR2 & _BV(TOV2)) && (tick_count < 255))
		overflows++;

	SREG = sreg;

	return ((overflows << 8) + tick_count) * (PRESCALE_VALUE / CLOCK_CYCLES_PER_MICROSECOND);
}

void delay(unsigned long milli_seconds) {
	uint16_t start = (uint16_t)micros();

	while(milli_seconds > 0) {
		if(((uint16_t)micros() - start) >= 1000) {
			milli_seconds--;
			start += 1000;
		}
	}
}

void micro_delay(unsigned int micro_seconds) {
	if(--micro_seconds == 0)
		return;

	micro_seconds <<= 2;
	micro_seconds -= 2;

	__asm__ __volatile__ (
		"1: sbiw %0, 1" "\n\t"
		"brne 1b" : "=w" (micro_seconds) : "0" (micro_seconds)
	);
}

ISR(TIMER2_OVF_vect) {
	unsigned long millis = timer2_milli_seconds;
	unsigned long elapsed;
	unsigned char remainings = timer2_remainings;

	elapsed = DELTA_PER_TIMER2_OVERFLOW_IN_MILLISECONDS;
	remainings += REMAINING_MILLISECONDS_PER_TIMER2_OVERFLOW;
	if(remainings >= MAXIMUM_VALUE_FOR_ROUNDING) {
		remainings -= MAXIMUM_VALUE_FOR_ROUNDING;
		elapsed++;
	}

	timer2_milli_seconds = millis + elapsed;
	timer2_remainings = remainings;
	timer2_overflow_count++;
}
