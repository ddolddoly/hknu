/*
 * protocol.c
 *
 * Created on: 2014. 4. 6.
 * Author: Seokyong Hong
 */

#include "protocol.h"

void parse_and_add_command(unsigned char *message, unsigned char size) {
	p_node command = (p_node)malloc(sizeof(node));

	command -> type = message[1];
	command -> data = message[2];

	queue_insert(command);
}
