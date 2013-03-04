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
	
	protected BasicPool(PoolConfig config, PooledObjectFactory<T> objectFactory) {
		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		for (int i = 0; i < config.getInitialEntries(); i++) {
			addIdleEntry(createIdleEntry());
		}
		idleEntriesCount = new AtomicInteger(idleEntries.size()); 
		
		this.config = config;
		this.objectFactory = objectFactory;
	}

	@Override
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

	@Override
	public PoolEntry<T> tryBorrowEntry() {
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
		
		addOrInvalidateIdleEntry(entry);		
		borrowingSemaphore.release();		
	}
	
	private PoolEntry<T> pollOrCreateIdleEntry() {
		PoolEntry<T> entry = pollIdleEntry();
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
			addIdleEntry(entry);
		}
	}
	
	private PoolEntry<T> createIdleEntry() {
		T object = objectFactory.createInstance();
		return new PoolEntry<T>(this, object);
	}

	private boolean addIdleEntry(PoolEntry<T> entry) {
		// TODO idle begin config
		return idleEntries.add(entry);
	}

	private PoolEntry<T> pollIdleEntry() {
		PoolEntry<T> entry = idleEntries.poll();
		if (entry != null) {
			// TODO idle end config
		}
		return entry;
	}
}
