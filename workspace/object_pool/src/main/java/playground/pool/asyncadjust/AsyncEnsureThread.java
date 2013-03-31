package playground.pool.asyncadjust;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.util.NameableDaemonThreadFactory;

class AsyncEnsureThread<T> {
	private final PoolConfig config;
	private final AsyncAdjustIdleEntriesQueue<T> queue;
	private final PoolEntryFactory<T> entryFactory;
	
	private AtomicBoolean isScheduled = new AtomicBoolean(false); 
	private ScheduledExecutorService ensureTaskBootstrapExecutor;
	private ExecutorService ensureTaskExecutor;
	
	AsyncEnsureThread(
			PoolConfig config, AsyncAdjustIdleEntriesQueue<T> queue, PoolEntryFactory<T> entryFactory) {
		
		this.config = config;
		this.queue = queue;
		this.entryFactory = entryFactory;
	}
	
	void scheduluBackgroundEnsure() {
		if (isScheduled.get()) {
			throw new IllegalStateException("already scheduled");
		}
		if (!isScheduled.compareAndSet(false, true)) {
			throw new IllegalStateException("already scheduled");
		}
		
		ensureTaskBootstrapExecutor = 
				Executors.newScheduledThreadPool(
						1, 
						new NameableDaemonThreadFactory(EnsureTaskBootstrap.class.getSimpleName()));

		ensureTaskExecutor = 
				Executors.newFixedThreadPool(
						config.getEnsureThreads(), 
						new NameableDaemonThreadFactory(EnsureTask.class.getSimpleName()));
		
		EnsureTaskBootstrap bootstrap = new EnsureTaskBootstrap();
		long initialDelay = 0;
		ensureTaskBootstrapExecutor.scheduleWithFixedDelay(
				bootstrap, initialDelay, config.getEnsureIntervalMillis(), TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				ensureTaskExecutor.shutdownNow();
				ensureTaskBootstrapExecutor.shutdownNow();
			}
		}));
	}
	
	private class EnsureTaskBootstrap implements Runnable {
		@Override
		public void run() {
			int needForEnsure = queue.countNeedForEnsure();
			
			for (int i = 0; i < needForEnsure; i++) {
				ensureTaskExecutor.submit(new EnsureTask());
				if (notNeedForEnsure()) {
					break;
				}
			}
		}

		private boolean notNeedForEnsure() {
			int needForEnsure = queue.countNeedForEnsure();
			return needForEnsure < 1;
		}
	}
	
	private class EnsureTask implements Runnable {
		@Override
		public void run() {
			try {
				PoolEntry<T> entry = entryFactory.createPoolEntry();
				queue.add(entry);
			} catch (Exception e) {
				// TODO Logger
				e.printStackTrace();
			}
		}
	}
}
