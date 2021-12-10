package io.github.chungtsai.lock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedSample implements IntValue {

	private int value = 0;

	public synchronized void add() {
		log.info("add old value:{}:{}", value, ++value);

	}

	public synchronized int getValue() {
		log.info("get value:{}", value);
		return value;
	}

}
