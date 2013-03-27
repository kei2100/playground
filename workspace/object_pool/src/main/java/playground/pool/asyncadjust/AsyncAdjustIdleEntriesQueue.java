package playground.pool.asyncadjust;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.util.NameableThreadFactory;

public class AsyncAdjustIdleEntriesQueue<T> implements IdleEntriesQueue<T> {

	private final PoolConfig config;
	private final PoolEntryFactory<T> entryFactory;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;
	
	private final InvalidateIdleEntryThread invalidateIdleEntryThread;
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntriesToBeInvalidate;
	
	private final EnsureIdleEntryThread ensureIdleEntryThread;
	
	public AsyncAdjustIdleEntriesQueue(PoolConfig config, PoolEntryFactory<T> entryFactory) {
		this.config = config;
		this.entryFactory = entryFactory;
		
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		idleEntriesCount = new AtomicInteger(0);
		
		idleEntriesToBeInvalidate = new ConcurrentLinkedQueue<PoolEntry<T>>();
		
		invalidateIdleEntryThread = new InvalidateIdleEntryThread();
		// TODO not background
		invalidateIdleEntryThread.startBackgroundInvalidate();
		ensureIdleEntryThread = new EnsureIdleEntryThread();
		// TODO not background
	}
	
	@Override
	public PoolEntry<T> poll() {
		PoolEntry<T> idle = idleEntries.poll();

		if (idle == null) {
			return idleEntriesToBeInvalidate.poll();
		}
		
		PoolEntry<T> toBeInvalidate = idleEntriesToBeInvalidate.poll();

		// Move toBeInvalidate to idle, if toBeInvalidate is not null.
		if (toBeInvalidate != null) {
			idleEntries.add(toBeInvalidate);
		} else {
			int decremented = idleEntriesCount.decrementAndGet();
			int minIdleEntriesCount = config.getMinIdleEntries();
			
			if (decremented < minIdleEntriesCount) {
				// TODO refactor
				boolean isScheduled = ensureIdleEntryThread.getAndSetScheduled(true);
				if (!isScheduled) {
					ensureIdleEntryThread.scheduluBackgroundEnsure();
				}
			}
		}
		
		return idle;
	}
	
	
	@Override
	// TODO not background
	public void add(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();

		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			idleEntriesToBeInvalidate.add(entry);
		} else {
			idleEntries.add(entry);
		}
	}	
	
	
	private class InvalidateIdleEntryThread {

		private ExecutorService invalidateTaskExecutor;
				
		protected void startBackgroundInvalidate() {
			int threadPoolSize = config.getInvalidateThreads();
			
			invalidateTaskExecutor = 
					Executors.newFixedThreadPool(
							threadPoolSize, new NameableThreadFactory(InvalidateTask.class.getSimpleName()));
			
			for (int i = 0; i < threadPoolSize; i++) {
				invalidateTaskExecutor.submit(new InvalidateTask());
			}
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					invalidateTaskExecutor.shutdownNow();
				}
			}));
		}
		
		private class InvalidateTask implements Callable<Void> {
			@Override
			public Void call() throws Exception {
				PoolEntry<T> idleEntry = null;
				
				while (true) {
					long invalidateStartedAt = System.currentTimeMillis();
				
					while ((idleEntry = idleEntriesToBeInvalidate.poll()) != null) {
						idleEntry.invalidate();
					}
					
					long elapsed = System.currentTimeMillis() - invalidateStartedAt;
					long sleepTime = config.getInvalidateIntervalMillis() - elapsed;

					if (sleepTime > 0) {
						TimeUnit.MILLISECONDS.sleep(sleepTime);
					}
				}
			}
		}
	}
	
	private class EnsureIdleEntryThread {
		
		private ScheduledExecutorService ensureTaskBootstrapExecutor;
		private ExecutorService ensureTaskExecutor;
		private AtomicBoolean scheduled = new AtomicBoolean(false);
		
		public boolean getAndSetScheduled(boolean scheduled) {
			return this.scheduled.getAndSet(scheduled);
		}
		
		protected void scheduluBackgroundEnsure() {
			ensureTaskBootstrapExecutor = 
					Executors.newScheduledThreadPool(
							1, new NameableThreadFactory(EnsureTaskBootstrap.class.getSimpleName()));

			ensureTaskExecutor = 
					Executors.newFixedThreadPool(
							config.getEnsureThreads(), new NameableThreadFactory(EnsureTask.class.getSimpleName()));
			
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
				int ensureCount = config.getMinIdleEntries() - idleEntriesCount.get();
				List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
				
				for (int i = 0; i < ensureCount; i++) {
					Future<Integer> future = ensureTaskExecutor.submit(new EnsureTask());
					futures.add(future);
				}
				
				boolean isAllDone = false;
				while (!isAllDone) {
					isAllDone = true;

					for (Future<Integer> future : futures) {
						if (future.isDone()) {
							try {
								Integer idleEntriesCountAfterAdding = future.get();
								if (idleEntriesCountAfterAdding >= config.getMinIdleEntries()) {
									isAllDone = true;
									break;
								}
							} catch (InterruptedException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						} else {
							isAllDone = false;
						}
					}
				}
				
				for (Future<Integer> future : futures) {
					boolean notInterrupt = false;
					future.cancel(notInterrupt);
				}
			}
		}
		
		private class EnsureTask implements Callable<Integer> {
			@Override
			public Integer call() throws Exception {
				PoolEntry<T> entry = entryFactory.createPoolEntry();
				add(entry);
				return idleEntriesCount.get();
			}
		}
	}
}
