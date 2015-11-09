/*
 * queue.c
 *
 * Created on: 2014. 4. 6.
 * Author: Seokyong Hong
 */

#include "queue.h"

void queue_init(void) {
	queue = NULL;
}

void queue_insert(p_node element) {
	if(!queue) {
		queue = element;
		element -> prev = element;
		element -> next = element;
	}
	else {
		queue -> prev -> next = element;
		element -> prev = queue -> prev;
		queue -> prev = element;
		element -> next = queue;
	}
}

p_node queue_remove(void) {
	uint8_t sreg;
	p_node target;

	sreg = SREG;
	cli();

	if(!queue) {
		SREG = sreg;
		return queue;
	}

	target = queue;
	if(queue -> next == queue) {
		queue = NULL;
	}
	else {
		queue -> prev -> next = queue -> next;
		queue -> next -> prev = queue -> prev;
		queue = queue -> next;
	}
	target -> next = NULL;
	target -> prev = NULL;
	SREG = sreg;

	return target;
}
