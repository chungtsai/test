package io.github.chungtsai.lock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LockTest {

	@ParameterizedTest(name = "{index} ==> {0}")
	@MethodSource("getCounts")
	void testReadWriteSampleAll(Count count) {

		testSize(new ReadWriteSample(), count.readCount, count.writeCount);
	}

	@ParameterizedTest(name = "{index} ==> {0}")
	@MethodSource("getCounts")
	void testStampedLockSampleAll(Count count) {

		testSize(new StampedLockSample(), count.readCount, count.writeCount);
	}

	@ParameterizedTest(name = "{index} ==> {0}")
	@MethodSource("getCounts")
	void testSynchronizedSampleAll(Count count) {

		testSize(new SynchronizedSample(), count.readCount, count.writeCount);
	}

	static List<IntValue> getValue() {

		List<IntValue> intValues = new ArrayList<>();
		intValues.add(new ReadWriteSample());
		intValues.add(new StampedLockSample());
		intValues.add(new SynchronizedSample());
		return intValues;
	}

	static List<Count> getCounts() {
		return Stream.of(new Count(1000, 1000), //
				new Count(10000, 100), //
				new Count(100, 1000))//
				.collect(Collectors.toList());//
	}

	static class Count {
		int readCount = 0;
		int writeCount = 0;

		public Count(int readCount, int writeCount) {
			super();
			this.readCount = readCount;
			this.writeCount = writeCount;
		}

		@Override
		public String toString() {
			return "Count [readCount=" + readCount + ", writeCount=" + writeCount + "]";
		}

	}

	public void testSize(IntValue common, int readcount, int writeCount) {
		TestCmdService.create()//
				.addCacheRepeat(new CmdRunnable("add", () -> common.add(), 0, 0), writeCount)//
				.addCacheRepeat(new CmdRunnable("get", () -> common.getValue(), 0, 0), readcount)//
				.startJoin();//
		log.info("value:{}", common.getValue());

		assertThat(common.getValue()).isEqualTo(writeCount);

	}
}
