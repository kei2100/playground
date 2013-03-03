package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Pool<T> {
	private final PoolConfig config ;
	private final PooledObjectFactory<T> objectFactory;
	
	private final Semaphore borrowingSemaphore;

	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;	
	private final AtomicInteger idleEntriesCount;
	
	public Pool(PoolConfig config, PooledObjectFactory<T> objectFactory) {
		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		for (int i = 0; i < config.getInitialEntries(); i++) {
			idleEntries.add(createPoolEntry());
		}
		idleEntriesCount = new AtomicInteger(idleEntries.size()); 
		
		this.config = config;
		this.objectFactory = objectFactory;
	}

	public PoolEntry<T> borrowEntry() throws InterruptedException, TimeoutException {
		try {
			boolean acquireSuccess = 
					borrowingSemaphore.tryAcquire(config.getMaxWaitMillis(), TimeUnit.MILLISECONDS);
			
			if (!acquireSuccess) {
				// pool entries all busy
				throw new TimeoutException(/* TODO */);
			}
		} catch (InterruptedException e) {
			throw e;
		}
		
		PoolEntry<T> entry = idleEntries.poll();
		if (entry == null) {
			entry = createPoolEntry();
		} else {
			idleEntriesCount.decrementAndGet();
		}
		
		return entry;
	}
	
	public void returnEntry(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();
		if (idleCount > config.getMaxIdleEntries()) {
			// not be added to the queue. and decrement count. 
			idleEntriesCount.decrementAndGet();
		} else {
			idleEntries.add(entry);
		}
		
		borrowingSemaphore.release();		
	}

	private PoolEntry<T> createPoolEntry() {
		T object = objectFactory.createInstance();
		return new PoolEntry<T>(this, object);
	}
}
