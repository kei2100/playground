package playground.pool.asyncadjust;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.util.NameableDaemonThreadFactory;

class AsyncInvalidateThread<T> {
	
	private final PoolConfig config;
	private final AsyncAdjustIdleEntriesQueue<T> queue; 
	
	private AtomicBoolean isScheduled = new AtomicBoolean(false);
	private ScheduledExecutorService invalidateTaskBootstrapExecutor;
	private ExecutorService invalidateTaskExecutor;
	
	AsyncInvalidateThread(PoolConfig config, AsyncAdjustIdleEntriesQueue<T> queue) {
		this.config = config;
		this.queue = queue;
	}
	
	void startBackgroundInvalidate() {
		if (isScheduled.get()) {
			throw new IllegalStateException("already scheduled");
		}
		if (!isScheduled.compareAndSet(false, true)) {
			throw new IllegalStateException("already scheduled");
		}
		
		invalidateTaskBootstrapExecutor = 
				Executors.newScheduledThreadPool(
						1, 
						new NameableDaemonThreadFactory(InvalidateTaskBootstrap.class.getSimpleName()));
		
		invalidateTaskExecutor = 
				Executors.newFixedThreadPool(
						config.getInvalidateThreads(), 
						new NameableDaemonThreadFactory(InvalidateTask.class.getSimpleName()));
		
		InvalidateTaskBootstrap bootstrap = new InvalidateTaskBootstrap();
		invalidateTaskBootstrapExecutor.scheduleWithFixedDelay(
				bootstrap, 
				config.getInvalidateThreadInitialDelayMillis(),
				config.getInvalidateIntervalMillis(), 
				TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				invalidateTaskExecutor.shutdownNow();
			}
		}));
	}
	
	private class InvalidateTaskBootstrap implements Runnable {
		@Override
		public void run() {
			while (true) {
				PoolEntry<T> entry = queue.pollToBeInvalidate();
				if (entry == null) break;
				
				invalidateTaskExecutor.submit(new InvalidateTask(entry));
			}
		}
	}
	
	private class InvalidateTask implements Runnable {
		private final PoolEntry<T> entry;
		
		private InvalidateTask(PoolEntry<T> entry) {
			this.entry = entry;
		}
		
		@Override
		public void run() {
			try {
				entry.invalidate();
			} catch (Exception e) {
				// Logger
				e.printStackTrace();
			}
		}
	}
}

