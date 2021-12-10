package io.github.chungtsai.cmd;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCmdService {

	private List<CmdRunnable> cmdRunnables = new ArrayList<TestCmdService.CmdRunnable>();

	private volatile static int INDEX = 1;

	public static TestCmdService create() {
		return new TestCmdService();
	}

	public TestCmdService addCache(CmdRunnable cmd) {
		cmdRunnables.add(cmd);
		return this;
	}

	public TestCmdService addCacheRepeat(CmdRunnable cmd, int repeatCount) {
		for (int i = 0; i < repeatCount; i++) {
			cmdRunnables.add(cmd);
		}
		return this;
	}

	public void run(CmdRunnable cmd) {
		TestCmdService.runCmd(cmd);
	}

	public void startJoin() {
		TestCmdService.runCmd(cmdRunnables);
	}

	public static void runCmd(CmdRunnable cmd) {
		runCmd(Arrays.asList(cmd));
	}

	public static void runCmd(List<CmdRunnable> cmds) {

		List<Thread> collect = cmds.stream().map(TestCmdService::thread).collect(Collectors.toList());
		collect.forEach(Thread::start);
		collect.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		Optional<CmdRunnable> findFirst = cmds.stream()
				.sorted(Comparator.comparing(new Function<CmdRunnable, Integer>() {
					@Override
					public Integer apply(CmdRunnable t) {
						return t.getWait();
					}
				})).findFirst();
		CmdRunnable cmdRunnable = findFirst.get();
		cmdRunnable.sleep();
	}

	static Thread thread(CmdRunnable cmdRunnable) {
		Runnable warp = new Runnable() {

			@Override
			public void run() {
				log.info("#### 呼叫" + cmdRunnable.cmd + " ####");
				cmdRunnable.runnable.run();
			}
		};
		Thread thread = new Thread(warp, cmdRunnable.cmd + "_" + INDEX++);
		if (cmdRunnable.uncaughtExceptionHandler != null) {
			thread.setUncaughtExceptionHandler(cmdRunnable.uncaughtExceptionHandler);
		}
		return thread;
	}

	public static void runCmd(String cmd, Runnable runnable, int wait) {
		runCmd(Lists.newArrayList(new CmdRunnable(cmd, runnable, wait, 0)));
	}

	public static void runCmd(String cmd, Runnable runnable, int wait, int delay) {
		runCmd(Lists.newArrayList(new CmdRunnable(cmd, runnable, wait, delay)));
	}

	public static void runCmd(String cmd, Runnable runnable) {
		runCmd(Lists.newArrayList(new CmdRunnable(cmd, runnable)));
	}

	static public class CmdRunnable {
		String cmd;
		Runnable runnable;
		UncaughtExceptionHandler uncaughtExceptionHandler;
		int wait = 0;

		public CmdRunnable(String cmd, Runnable runnable, int wait, int delayStart) {
			super();
			this.cmd = cmd;
			this.runnable = new DelayRunnable(runnable, delayStart);
			this.wait = wait;
		}

		class DelayRunnable implements Runnable {

			private Runnable runnable;
			private int delayStart = 0;

			public DelayRunnable(Runnable runnable, int delayStart) {
				super();
				this.runnable = runnable;
				this.delayStart = delayStart;
			}

			@Override
			public void run() {
				if (delayStart > 0) {
					log.info("delay start...{}s", delayStart);
					try {
						TimeUnit.SECONDS.sleep(delayStart);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				runnable.run();
			}

		}

		public CmdRunnable(String cmd, Runnable runnable) {
			this(cmd, runnable, 0, 0);
		}

		public int getWait() {
			return wait;
		}

		void sleep() {
			if (wait > 0) {
				try {
					TimeUnit.SECONDS.sleep(wait);
				} catch (InterruptedException e) {
				}
			}
		}

		public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
			this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		}

	}
}
