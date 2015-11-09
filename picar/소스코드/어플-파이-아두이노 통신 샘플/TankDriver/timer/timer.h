/*
 * timer.h
 *
 * Created on: 2014. 4. 3.
 * Author: Seokyong Hong
 */

#ifndef TIMER_H_
#define TIMER_H_

#include <system/avr.h>

#define CLOCK_IO_FREQUENCY								16000000UL
#define CLOCK_CYCLES_PER_MICROSECOND					(CLOCK_IO_FREQUENCY / 1000000UL)
#define PRESCALE_VALUE									64
#define MAXIMUM_TCNT2_VALUE								0xFF
#define MICROSECONDS_PER_TIMER2_OVERFLOW				(PRESCALE_VALUE * MAXIMUM_TCNT2_VALUE / CLOCK_CYCLES_PER_MICROSECOND)
#define DELTA_PER_TIMER2_OVERFLOW_IN_MILLISECONDS	(MICROSECONDS_PER_TIMER2_OVERFLOW / 1000)
#define REMAINING_MILLISECONDS_PER_TIMER2_OVERFLOW	((DELTA_PER_TIMER2_OVERFLOW_IN_MILLISECONDS % 1000) >> 3)
#define MAXIMUM_VALUE_FOR_ROUNDING						(1000 >> 3)

static volatile unsigned long timer2_overflow_count;
static volatile unsigned long timer2_milli_seconds;
static volatile unsigned long timer2_remainings;

void timer_init(void);
unsigned long micros(void);
void delay(unsigned long milli_seconds);
void micro_delay(unsigned int micro_seconds);

#endif /* TIMER_H_ */
