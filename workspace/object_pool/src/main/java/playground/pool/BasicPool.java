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
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntriesToBeInvalidate;
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
		
		idleEntriesToBeInvalidate = new ConcurrentLinkedQueue<PoolEntry<T>>();
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
		
		return innerBorrowEntry();
	}

	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (entry == null) throw new NullPointerException();
		
		try {
			innerReturnEntry(entry);
		} finally {
			borrowingSemaphore.release();		
		}
	}
	
	private PoolEntry<T> innerBorrowEntry() throws CreatePoolEntryException {
		PoolEntry<T> idleEntry = pollIdleEntry();
		if (idleEntry != null) return idleEntry;
		
		PoolEntry<T> idleEntryToBeInvalidate = pollIdleEntryToBeInvalidate();
		if (idleEntryToBeInvalidate != null) return idleEntryToBeInvalidate;
		
		return createIdleEntry();
	}
	
	protected PoolEntry<T> pollIdleEntry() {
		PoolEntry<T> entry = idleEntries.poll();
		
		if (entry != null) {
			idleEntriesCount.decrementAndGet();
		}
		return entry;
	}
	
	protected PoolEntry<T> pollIdleEntryToBeInvalidate() {
		return idleEntriesToBeInvalidate.poll();
	}
	
	private void innerReturnEntry(PoolEntry<T> entry) {
		if (isAlreadyInvalid(entry)) {
			// do nothing.
			return;
		}
		
		int idleCount = idleEntriesCount.incrementAndGet();
		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			idleEntriesToBeInvalidate.add(entry);
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
