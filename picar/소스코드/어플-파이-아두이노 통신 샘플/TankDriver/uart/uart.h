/*
 * uart.h
 *
 * Created on: 2014. 4. 6.
 * Author: Seokyong Hong
 */

#ifndef UART_H_
#define UART_H_

#include <system/avr.h>

#define UART_BAUD_RATE	19200
#define SIZE_OF_BUFFER	16

static unsigned char *buffer;
static unsigned char length;

int uart_init(void);
int uart_write(unsigned char *data, unsigned char size);
void uart_close(void);

#endif /* UART_H_ */
