package io.github.chungtsai.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadWriteSample implements IntValue {
	private int value = 0;
	ReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public void add() {
		final Lock writeLock = this.lock.writeLock();
		try {
			writeLock.lock();
			log.info("add old value:{}:{}", this.value, ++this.value);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int getValue() {
		final Lock readLock = this.lock.readLock();
		try {
			readLock.lock();
			log.info("get value:{}", this.value);
			return this.value;
		} finally {
			readLock.unlock();
		}
	}
}