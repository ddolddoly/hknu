/*
 * queue.h
 *
 * Created on: 2014. 4. 6.
 * Author: Seokyong Hong
 */

#ifndef QUEUE_H_
#define QUEUE_H_

#include <system/avr.h>

typedef struct _node {
	unsigned char type;
	unsigned char data;
	struct _node *prev;
	struct _node *next;
} node;
typedef node *p_node;

static p_node queue;

void queue_init(void);
void queue_insert(p_node element);
p_node queue_remove(void);

#endif /* QUEUE_H_ */
