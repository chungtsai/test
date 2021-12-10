package io.github.chungtsai.lock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.chungtsai.cmd.TestCmdService;
import io.github.chungtsai.cmd.TestCmdService.CmdRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StampedLockSampleTest {

	@Test
	void testReadEqWriteLock() {
		IntValue common = new StampedLockSample();
		TestCmdService cmdService = TestCmdService.create();

		for (int i = 0; i < 1000; i++) {
			cmdService.addCache(new CmdRunnable("add_" + i, () -> common.add(), 0, 0));
			cmdService.addCache(new CmdRunnable("get_" + i, () -> common.getValue(), 0, 0));

		}
		cmdService.startJoin();
		log.info("value:{}", common.getValue());

		assertThat(common.getValue()).isEqualTo(1000);

	}

}
