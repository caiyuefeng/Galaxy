package com.galaxy.sirius;

import java.util.concurrent.*;

/**
 * 同步线程池。
 *
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 21:48 2020/1/21
 */
public class SyncThreadPool {

	/**
	 * 最大同时运行的线程数。
	 */
	private static final int THREAD_NUM = 10;

	/**
	 * 单例模式
	 */
	private static volatile ExecutorService threadPool = null;

	public static ExecutorService getInstance() {
		if (threadPool == null) {
			threadPool = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM,
					0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new InnerThreadFactory());
		}
		return threadPool;
	}

	/**
	 * 内部同步线程工厂构造器。
	 */
	private static class InnerThreadFactory implements ThreadFactory {
		/**
		 * 线程编号
		 */
		private int threadNo = 0;

		@Override
		public Thread newThread(Runnable r) {
			assert r != null;
			Thread thread = new Thread(r);
			thread.setName("Galaxy_Sync_Thread_" + threadNo);
			threadNo++;
			return thread;
		}
	}
}
