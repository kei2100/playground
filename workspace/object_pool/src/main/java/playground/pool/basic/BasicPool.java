package playground.pool.basic;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import playground.pool.PoolException;
import playground.pool.IdleEntriesQueue;
import playground.pool.Pool;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;


public class BasicPool<T> implements Pool<T> {
	
	private final PoolConfig config ;
	private final PoolEntryFactory<T> entryFactory;
	private final IdleEntriesQueue<T> idleEntries;
	
	private final Semaphore borrowingSemaphore;
		
	public BasicPool(PoolConfig config, IdleEntriesQueue<T> idleEntries, PoolEntryFactory<T> entryFactory)
	throws PoolException {
		this.config = config;
		this.idleEntries = idleEntries;
		this.entryFactory = entryFactory;
		
		borrowingSemaphore = new Semaphore(config.getMaxActiveEntries());
		
		// initialize idle entries
		for (int i = 0; i < config.getInitialEntries(); i++) {
			try {
				idleEntries.add(createIdleEntry());
			} catch (Exception e) {
				// TODO Logger
				e.printStackTrace();
			}
		}
	}

	@Override
	public PoolConfig getPoolConfig() {
		return config;
	}
	
	@Override
	public PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, PoolException {

		return borrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> borrowEntry(boolean createNew)
			throws InterruptedException, TimeoutException, PoolException {

		long timeout = config.getMaxWaitMillisOnBorrow();
		return borrowEntry(createNew, timeout, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public PoolEntry<T> borrowEntry(long timeout, TimeUnit unit)
			throws InterruptedException, TimeoutException, PoolException {

		return borrowEntry(true, timeout, unit);
	}
	
	@Override
	public PoolEntry<T> borrowEntry(boolean createNew, long timeout, TimeUnit unit) 
			throws InterruptedException, TimeoutException, PoolException {

		try {
			if (config.isWaitUnlimitOnBorrow()) {
				borrowingSemaphore.acquire();
			} else {
				boolean acquireSuccess = borrowingSemaphore.tryAcquire(timeout, unit);
				if (!acquireSuccess) {
					// pool entries all busy
					throw new TimeoutException("borrowEntry timed out.");
				}
			}
		} catch (InterruptedException e) {
			throw e;
		}
		
		try {
			PoolEntry<T> entry = innerBorrowEntry(createNew);
			if (entry == null) {
				borrowingSemaphore.release();
			}
			return entry;
		} catch (Exception e) {
			borrowingSemaphore.release();
			throw new PoolException(e);
		}
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry() throws PoolException {
		return tryBorrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry(boolean createNew) throws PoolException {
		boolean acquireSuccess = borrowingSemaphore.tryAcquire();
		if (!acquireSuccess) {
			return null;
		}
		
		try {
			 PoolEntry<T> entry = innerBorrowEntry(createNew);
			if (entry == null) {
				borrowingSemaphore.release();
			}
			return entry;
		} catch (Exception e) {
			borrowingSemaphore.release();
			throw new PoolException(e);
		}
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
	
	private PoolEntry<T> innerBorrowEntry(boolean createNew) throws Exception {
		PoolEntry<T> idleEntry = idleEntries.poll();
		if (idleEntry == null) {
			if (createNew) return createIdleEntry();
		}
		return idleEntry;
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

	private PoolEntry<T> createIdleEntry() throws Exception {
		return entryFactory.createPoolEntry();
	}
}
