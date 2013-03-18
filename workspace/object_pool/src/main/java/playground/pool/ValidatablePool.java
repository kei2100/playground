package playground.pool;

import java.util.concurrent.TimeoutException;

public class ValidatablePool<T> implements Pool<T> {

	private final Pool<T> delegate;
	private final ValidationConfig config;
	private ValidatablePoolThread<T> validationThread;
	
	protected ValidatablePool(Pool<T> pool, ValidationConfig config) {
		delegate = pool;
		this.config = config;
				
		if (config.isTestInBackground()) {
			validationThread = new ValidatablePoolThread<T>(delegate, config);
			validationThread.scheduleBackgroundValidation();
		}		
	}
	
	@Override
	public PoolConfig getPoolConfig() {
		return delegate.getPoolConfig();
	}
	
	@Override
	public PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePoolEntryException {
		
		PoolEntry<T> entry = delegate.borrowEntry();
		
		if (!config.isTestOnBorrow()) {
			return entry;
		}
		
		boolean validateSuccessful = ValidationHelper.validate(config, entry);
		if (validateSuccessful) {
			return entry;
		} else {
			delegate.returnEntry(entry);
			throw new CreatePoolEntryException("PoolEntry is invalid.");
		}
	}
	
	@Override
	public PoolEntry<T> tryBorrowIdleEntry() {
		return tryBorrowIdleEntry(0);
	}
	
	protected PoolEntry<T> tryBorrowIdleEntry(int retryCount) {
		PoolEntry<T> entry = delegate.tryBorrowIdleEntry();
		if (entry == null) {
			return null;
		}

		if (!config.isTestOnBorrow()) {
			return entry;
		}
		
		boolean validateSuccessful = ValidationHelper.validate(config, entry);
		if (validateSuccessful) {
			return entry;
		} else {
			delegate.returnEntry(entry);
			
			if (retryCount == 0) {
				return null;
			} else if (retryCount > 0) {
				return this.tryBorrowIdleEntry(retryCount--);				
			} else {
				throw new IllegalArgumentException("retryCount is negative value. retryCount:" + retryCount);
			}
		}
	}
	
	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (config.isTestOnReturn()) {
			ValidationHelper.validate(config, entry);
		}
		delegate.returnEntry(entry);
	}	
}
