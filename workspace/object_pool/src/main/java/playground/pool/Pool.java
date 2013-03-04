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
			addIdleEntries(createIdleEntry());
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
		return pollOrCreateIdleEntry();
	}

	public PoolEntry<T> tryBorrowEntry() {
		boolean acquireSuccess = borrowingSemaphore.tryAcquire();		
		if (!acquireSuccess) {
			return null;	
		} else {
			return pollOrCreateIdleEntry();
		}		
	}

	public void returnEntry(PoolEntry<T> entry) {
		addOrInvalidateIdleEntry(entry);		
		borrowingSemaphore.release();		
	}
	
	private PoolEntry<T> pollOrCreateIdleEntry() {
		PoolEntry<T> entry = pollIdleEntries();
		if (entry == null) {
			entry = createIdleEntry();
		} else {
			idleEntriesCount.decrementAndGet();
		}
		return entry;
	}

	private void addOrInvalidateIdleEntry(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();
		if (idleCount > config.getMaxIdleEntries()) {
			// not be added to the queue. and decrement count. 
			idleEntriesCount.decrementAndGet();
			// TODO invalidate pool entry
		} else {
			addIdleEntries(entry);
		}
	}
	
	private PoolEntry<T> createIdleEntry() {
		T object = objectFactory.createInstance();
		return new PoolEntry<T>(this, object);
	}

	private boolean addIdleEntries(PoolEntry<T> entry) {
		// TODO idle begin config
		return idleEntries.add(entry);
	}

	private PoolEntry<T> pollIdleEntries() {
		PoolEntry<T> entry = idleEntries.poll();
		if (entry != null) {
			// TODO idle end config
		}
		return entry;
	}
}
