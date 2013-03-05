package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicPool<T> implements Pool<T> {

	private final PoolConfig config ;
	private final PooledObjectFactory<T> objectFactory;
	
	private final Semaphore borrowingSemaphore;

	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;	
	private final AtomicInteger idleEntriesCount;
	
	protected BasicPool(PoolConfig config, PooledObjectFactory<T> objectFactory) 
			throws CreatePooledObjectException {
		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		for (int i = 0; i < config.getInitialEntries(); i++) {
			idleEntries.add(createIdleEntry());
		}
		idleEntriesCount = new AtomicInteger(idleEntries.size()); 
		
		this.config = config;
		this.objectFactory = objectFactory;
	}

	@Override
	public PoolEntry<T> borrowEntry()
			throws InterruptedException, TimeoutException, CreatePooledObjectException {
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

	@Override
	public PoolEntry<T> tryBorrowEntry() throws CreatePooledObjectException {
		boolean acquireSuccess = borrowingSemaphore.tryAcquire();		
		if (!acquireSuccess) {
			return null;	
		} else {
			return pollOrCreateIdleEntry();
		}		
	}

	@Override
	public void returnEntry(PoolEntry<T> entry) {
		if (entry == null) throw new NullPointerException();
		
		try {
			addOrInvalidateIdleEntry(entry);
		} finally {
			borrowingSemaphore.release();		
		}
	}
	
	private PoolEntry<T> pollOrCreateIdleEntry() throws CreatePooledObjectException {
		PoolEntry<T> entry = idleEntries.poll();
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
			// not be added to the queue. decrement count and invalidate entry. 
			idleEntriesCount.decrementAndGet();
			entry.invalidate();
		} else {
			idleEntries.add(entry);
		}
	}
	
	private PoolEntry<T> createIdleEntry() throws CreatePooledObjectException {
		// TODO refactor BasicPoolEntryに依存しちゃっている
		T object = objectFactory.createInstance();
		return new BasicPoolEntry<T>(object);
	}
}
