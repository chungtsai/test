package io.github.chungtsai.lock;

import java.util.concurrent.locks.StampedLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StampedLockSample implements IntValue {

	private int value = 0;
	StampedLock lock = new StampedLock();

	public void add() {
		long id = lock.writeLock();
		try {
			log.info("add old value:{}:{}", value, ++value);
		} finally {
			lock.unlockWrite(id);
		}
	}

	public int getValue() {
		long readLock = lock.readLock();
		try {
			log.info("get value:{}", value);
			return value;
		} finally {
			lock.unlockRead(readLock);
		}
	}

}
