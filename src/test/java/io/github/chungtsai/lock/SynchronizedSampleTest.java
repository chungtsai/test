package io.github.chungtsai.lock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedSampleTest {

	@Test
	void testReadEqWriteLock() {

		IntValue common = new SynchronizedSample();
		TestCmdService cmdService = TestCmdService.create();
		//
		cmdService.addCacheRepeat(new CmdRunnable("add", () -> common.add(), 0, 0), 1000)
				.addCacheRepeat(new CmdRunnable("get", () -> common.getValue(), 0, 0), 1000).startJoin();
		log.info("value:{}", common.getValue());

		assertThat(common.getValue()).isEqualTo(1000);

	}

}
