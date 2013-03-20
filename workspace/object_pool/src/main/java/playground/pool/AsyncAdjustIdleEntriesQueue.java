package playground.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.util.NameableThreadFactory;

public class AsyncAdjustIdleEntriesQueue<T> implements IdleEntriesQueue<T> {

	private final PoolConfig config;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;
	
	private final AsyncAdjustIdleEntriesTask asyncAdjustIdleEntriesTask;
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntriesToBeInvalidate;
	private final ConcurrentLinkedQueue<RefillIdleEntryRequest> refillIdleEntryRequests;  
	
	protected AsyncAdjustIdleEntriesQueue(PoolConfig config, PoolEntryFactory<T> entryFactory) {
		this.config = config;
		
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		idleEntriesCount = new AtomicInteger(0);
		
		idleEntriesToBeInvalidate = new ConcurrentLinkedQueue<PoolEntry<T>>();
		refillIdleEntryRequests = new ConcurrentLinkedQueue<RefillIdleEntryRequest>();
		
		asyncAdjustIdleEntriesTask = new AsyncAdjustIdleEntriesTask();
		// TODO not background
		asyncAdjustIdleEntriesTask.startBackgroundInvalidate();
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
			idleEntriesCount.decrementAndGet();
			
			// TODO refill request impl
		}
		
		return idle;
	}
	
	
	// TODO refill impl addEntryAndGetIdleCount
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
	
	
	private class AsyncAdjustIdleEntriesTask {

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
}
