package playground.pool.asyncadjust;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.util.NameableDaemonThreadFactory;
import playground.pool.util.PoolLoggerMarkerFactory;

class AsyncEnsureThread<T> {
	private static final Logger logger = LoggerFactory.getLogger(AsyncEnsureThread.class);
	
	private final PoolConfig config;
	private final AsyncAdjustIdleEntriesQueue<T> queue;
	private final PoolEntryFactory<T> entryFactory;
	
	private AtomicBoolean isScheduled = new AtomicBoolean(false); 
	private ScheduledExecutorService taskExecutor;
	
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
		
		taskExecutor = 
				Executors.newScheduledThreadPool(
						config.getEnsureThreads(), 
						new NameableDaemonThreadFactory(AsyncEnsureThread.class.getSimpleName()));
		
		EnsureTaskBootstrap bootstrap = new EnsureTaskBootstrap();
		long initialDelay = 0;
		taskExecutor.scheduleWithFixedDelay(
				bootstrap, initialDelay, config.getEnsureIntervalMillis(), TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				taskExecutor.shutdownNow();
			}
		}));
	}
	
	private class EnsureTaskBootstrap implements Runnable {
		@Override
		public void run() {
			int needForEnsure = queue.countNeedForEnsure();
			
			for (int i = 0; i < needForEnsure; i++) {
				taskExecutor.submit(new EnsureTask());
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
				logger.warn(PoolLoggerMarkerFactory.getMarker(), 
						"Create PoolEntry throws Exception.", e);				
			}
		}
	}
}
