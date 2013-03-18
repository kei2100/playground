package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.util.Pair;

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
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (entry == null) throw new NullPointerException();
		
		try {
			addIdleEntry(entry);
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
	
	protected PoolEntry<T> pollIdleEntry() {
		return pollIdleEntryWithCount().getRight();
	}
	
	protected Pair<Integer, PoolEntry<T>> pollIdleEntryWithCount() {
		PoolEntry<T> entry = idleEntries.poll();

		if (entry != null) {
			// first poll entries, and then decrement a counter. 
			// this is fixed processing order
			int decremented = idleEntriesCount.decrementAndGet();
			return new Pair<Integer, PoolEntry<T>>(decremented, entry);
			
		} else {
			return new Pair<Integer, PoolEntry<T>>(0, null);
		}
	}
	
	private void addIdleEntry(PoolEntry<T> entry) {
		if (isAlreadyInvalid(entry)) {
			// do nothing.
			return;
		}
		
		// first increment  a counter, and then add entries. 
		// this is fixed processing order
		idleEntriesCount.incrementAndGet();
		idleEntries.add(entry);
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
