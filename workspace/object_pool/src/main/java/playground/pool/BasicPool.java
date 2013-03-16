package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicPool<T> implements Pool<T> {

	private final PoolConfig config ;
	private final PoolEntryFactory<T> entryFactory;
	
	private final Semaphore borrowingSemaphore;

	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;	
	private final AtomicInteger idleEntriesCount;
	
	protected BasicPool(PoolConfig config, PoolEntryFactory<T> entryFactory)
	throws CreatePoolEntryException {
		this.config = config;
		this.entryFactory = entryFactory;

		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		for (int i = 0; i < config.getInitialEntries(); i++) {
			idleEntries.add(createIdleEntry());
		}
		idleEntriesCount = new AtomicInteger(idleEntries.size()); 
	}

	@Override
	public PoolConfig getPoolConfig() {
		return config;
	}
	
	@Override
	public PoolEntry<T> borrowEntry()
			throws InterruptedException, TimeoutException, CreatePoolEntryException {
		try {
			boolean acquireSuccess = 
					borrowingSemaphore.tryAcquire(
							config.getMaxWaitMillisOnBorrow(), TimeUnit.MILLISECONDS);
			
			if (!acquireSuccess) {
				// pool entries all busy
				throw new TimeoutException(/* TODO */);
			}
		} catch (InterruptedException e) {
			throw e;
		}
		
		return pollIdleOrCreateEntry();
	}

	@Override
	public PoolEntry<T> tryBorrowIdleEntry() {
		boolean acquireSuccess = borrowingSemaphore.tryAcquire();		
		if (!acquireSuccess) {
			return null;	
		} else {
			return pollIdleEntry();
		}		
	}

	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (entry == null) throw new NullPointerException();
		
		try {
			addOrInvalidateIdleEntry(entry);
		} finally {
			borrowingSemaphore.release();		
		}
	}
	
	private PoolEntry<T> pollIdleOrCreateEntry() throws CreatePoolEntryException {
		PoolEntry<T> entry = pollIdleEntry();
		if (entry == null) {
			entry = createIdleEntry();
		}
		return entry;
	}
	
	private PoolEntry<T> pollIdleEntry() {
		PoolEntry<T> entry = idleEntries.poll();
		if (entry != null) {
			idleEntriesCount.decrementAndGet();			
		}
		return entry;
	}

	private void addOrInvalidateIdleEntry(PoolEntry<T> entry) {
		if (isAlreadyInvalid(entry)) {
			// do nothing.
			return;
		}
		
		int idleCount = idleEntriesCount.incrementAndGet();
		if (idleCount > config.getMaxIdleEntries()) {
			// not be added to the queue. decrement count and invalidate entry. 
			idleEntriesCount.decrementAndGet();
			entry.invalidate();
		} else {
			idleEntries.add(entry);
		}
	}
	
	private boolean isAlreadyInvalid(PoolEntry<T> entry) {
		if (entry.getState().isValid()) {
			return false;
		} else {
			return true;
		}
	}

	private PoolEntry<T> createIdleEntry() throws CreatePoolEntryException {
		return entryFactory.createPoolEntry();
	}
}
