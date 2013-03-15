package playground.pool;

import java.util.concurrent.TimeoutException;

public class ValidatablePool<T> implements Pool<T> {

	private final Pool<T> delegate;
	private final ValidationConfig config;
	
	protected ValidatablePool(Pool<T> pool, ValidationConfig config) {
		this.delegate = pool;
		this.config = config;
				
		// TODO true, config
//		if (true) {
//			ScheduledExecutorService ses = 
//					Executors.newScheduledThreadPool(2);
//			ses.
//			
//		}
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
	
	
	private class ValidateTask implements Runnable {
		@Override
		public void run() {
			PoolEntry<T> idleEntry = null;
			while ((idleEntry = delegate.tryBorrowIdleEntry()) != null) {
//				boolean validateSuccessful = ValidationHelper.validate(config, idleEntry);
//				
//				if (!validateSuccessful) {
//					// TODO invalidateしてpoolに返す。
//				}
			}
		}
	}

}
