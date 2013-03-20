package playground.pool;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BasicPool<T> implements Pool<T> {

	private final PoolConfig config ;
	private final PoolEntryFactory<T> entryFactory;
	
	private final Semaphore borrowingSemaphore;
	
	private final IdleEntriesQueue<T> idleEntries;
	
	protected BasicPool(PoolConfig config, PoolEntryFactory<T> entryFactory)
	throws CreatePoolEntryException {
		this.config = config;
		this.entryFactory = entryFactory;

		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		idleEntries = new IdleEntriesQueue<T>(config);
		for (int i = 0; i < config.getInitialEntries(); i++) {
			idleEntries.add(createIdleEntry());
		}
	}

	@Override
	public PoolConfig getPoolConfig() {
		return config;
	}
	
	protected IdleEntriesQueue<T> getIdleEntries() {
		return idleEntries;
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
		PoolEntry<T> idleEntry = idleEntries.poll();
		
		if (idleEntry != null) {
			return idleEntry;
		}
		
		return createIdleEntry();
	}
			
	private void innerReturnEntry(PoolEntry<T> entry) {
		if (isAlreadyInvalid(entry)) {
			// do nothing.
			return;
		}
		
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
