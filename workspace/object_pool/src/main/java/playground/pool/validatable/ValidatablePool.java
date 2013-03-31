package playground.pool.validatable;

import java.util.concurrent.TimeoutException;

import playground.pool.Pool;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolException;
import playground.pool.ValidationConfig;


public class ValidatablePool<T> implements Pool<T> {

	private final Pool<T> delegate;
	private final ValidationConfig config;
	private ValidatablePoolThread<T> validationThread;
	
	public ValidatablePool(Pool<T> pool, ValidationConfig config) {
		delegate = pool;
		this.config = config;
				
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

		PoolEntry<T> entry = delegate.borrowEntry(createNew);
		if (entry == null) {
			return null;
		}
		return afterBorrowEntry(entry);
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
		return afterBorrowEntry(entry);
	}

	protected PoolEntry<T> afterBorrowEntry(PoolEntry<T> entry) throws PoolException {
		if (!config.isTestOnBorrow()) {
			return entry;
		}
		
		boolean validateSuccessful = ValidationHelper.validate(config, entry);
		if (validateSuccessful) {
			return entry;
		} else {
			delegate.returnEntry(entry);
			throw new PoolException("PoolEntry is invalid.");
		}
	}
	
	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		entry = beforeReturnEntry(entry);
		delegate.returnEntry(entry);
	}

	protected PoolEntry<T> beforeReturnEntry(PoolEntry<T> entry) {
		if (config.isTestOnReturn()) {
			ValidationHelper.validate(config, entry);
		}
		return entry;
	}
}
