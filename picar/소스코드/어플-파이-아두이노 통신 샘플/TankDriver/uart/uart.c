/*
 * uart.c
 *
 * Created on: 2014. 4. 6.
 * Author: Seokyong Hong
 */

#include "uart.h"

int uart_init(void) {
	int ubrr;

	if(buffer)
		return 0;

	buffer = (unsigned char *)malloc(sizeof(unsigned char *) * SIZE_OF_BUFFER);
	memset((void *)buffer, 0, sizeof(unsigned char *) * SIZE_OF_BUFFER);
	length = 0;
	ubrr = CLOCK_IO_FREQUENCY / 16 / UART_BAUD_RATE - 1;

	UBRR0H = ubrr >> 8;
	UBRR0L = ubrr & 0xFF;
	UCSR0B = _BV(RXCIE0) | _BV(RXEN0) | _BV(TXEN0);
	UCSR0C = _BV(UCSZ01) | _BV(UCSZ00);

	return 1;
}

int uart_write(unsigned char *data, unsigned char size) {
	int index;

	if(!buffer || !data)
		return 0;

	for(index = 0;index < size;index++) {
		while(!(UCSR0A & (1 << UDRE0)));
		UDR0 = *(data + index);
	}

	return size;
}

void uart_close(void) {
	free((void *)buffer);
	buffer = (unsigned char *)NULL;
}

ISR(USART_RX_vect) {
	unsigned char RxD = UDR0;

	buffer[length++] = RxD;
	if(RxD == PROTOCOL_CLOSE_CHARACTER) {
		parse_and_add_command(buffer, length);
		length = 0;
	}
}
