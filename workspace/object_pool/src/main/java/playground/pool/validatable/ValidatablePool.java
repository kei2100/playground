package playground.pool.validatable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import playground.pool.Pool;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolException;
import playground.pool.ValidationConfig;

public class ValidatablePool<T> implements Pool<T> {

	private final Pool<T> delegate;
	private final List<ValidatablePoolListener<T>> listeners; 
	private ValidatablePoolThread<T> validationThread;
	
	public ValidatablePool(Pool<T> delegate, ValidationConfig config) {
		this.delegate = delegate;
		
		this.listeners = new ArrayList<ValidatablePoolListener<T>>();
		this.listeners.add(new CheckAgeExpiredListener<T>(delegate, config));
		this.listeners.add(new ValidatePoolEntryListener<T>(delegate, config));
		
		if (config.isTestInBackground()) {
			validationThread = new ValidatablePoolThread<T>(delegate, config);
			validationThread.scheduleBackgroundValidate();
		}		
	}
	
	@Override
	public PoolConfig getPoolConfig() {
		return delegate.getPoolConfig();
	}
	
	@Override
	public PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, PoolException {

		return borrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> borrowEntry(boolean createNew) 
			throws InterruptedException, TimeoutException, PoolException {

		long timeout = getPoolConfig().getMaxWaitMillisOnBorrow();
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
		
		long methodStartedAt = System.currentTimeMillis();

		PoolEntry<T> entry = delegate.borrowEntry(createNew, timeout, unit);
		if (entry == null) {
			return null;
		}
		long elapsedMillis = System.currentTimeMillis() - methodStartedAt;
		return afterBorrowEntry(entry, createNew, elapsedMillis);
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry() throws PoolException {
		return tryBorrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry(boolean createNew) throws PoolException {

		PoolEntry<T> entry = delegate.tryBorrowEntry(createNew);
		if (entry == null) {
			return null;
		}
		return afterTryBorrowEntry(entry, createNew);
	}

	private PoolEntry<T> afterBorrowEntry(PoolEntry<T> entry, boolean createNew, long elapsedMillis) 
			throws PoolException, InterruptedException, TimeoutException {
		
		for (ValidatablePoolListener<T> listener : listeners) {
			entry = listener.afterBorrowEntry(entry, createNew, elapsedMillis);
		}
		return entry;
	}

	private PoolEntry<T> afterTryBorrowEntry(PoolEntry<T> entry, boolean createNew) throws PoolException {
		for (ValidatablePoolListener<T> listener : listeners) {
			entry = listener.afterTryBorrowEntry(entry, createNew);
		}
		return entry;
	}
	
	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (entry == null) throw new NullPointerException();

		try {
			entry = beforeReturnEntry(entry);
		} finally {
			delegate.returnEntry(entry);
		}
	}

	private PoolEntry<T> beforeReturnEntry(PoolEntry<T> entry) {
		for (ValidatablePoolListener<T> listener : listeners) {
			entry = listener.beforeReturnEntry(entry);
		}
		return entry;
	}
}
