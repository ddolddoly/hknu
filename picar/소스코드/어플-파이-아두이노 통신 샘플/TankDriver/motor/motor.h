/*
 * motor.h
 *
 * Created on: 2014. 4. 3.
 * Author: Seokyong Hong
 */

#ifndef MOTOR_H_
#define MOTOR_H_

#include <system/avr.h>

#define DEFAULT_MOTOR_SPEED		200

void motor_init(void);
void motor_forward(void);
void motor_backward(void);
void motor_turn_left(void);
void motor_turn_right(void);
void motor_stop(void);

#endif /* MOTOR_H_ */
