package io.github.chungtsai.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadWriteSample {
	private int value = 0;
	ReadWriteLock lock = new ReentrantReadWriteLock();

	public void add() {
		Lock writeLock = lock.writeLock();
		try {
			writeLock.lock();
			log.info("add old value:{}:{}", value, ++value);
		} finally {
			writeLock.unlock();
		}
	}

	public int getValue() {
		Lock readLock = lock.readLock();
		try {
			readLock.lock();
			log.info("get value:{}", value);
			return value;
		} finally {
			readLock.unlock();
		}
	}
}