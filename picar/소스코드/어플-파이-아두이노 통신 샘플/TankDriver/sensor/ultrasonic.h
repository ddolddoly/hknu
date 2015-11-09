/*
 * untrasonic.h
 *
 * Created on: 2014. 4. 27.
 * Author: Seokyong Hong
 */

#ifndef UNTRASONIC_H_
#define UNTRASONIC_H_

#include <system/avr.h>

#define ULTRASONIC_READ_TIMEOUT	1000000UL

void ultrasonic_init(void);
long ultrasonic_distance(void);

#endif /* UNTRASONIC_H_ */
