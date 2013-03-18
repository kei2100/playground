package playground.pool;

import java.util.concurrent.TimeoutException;

public class ValidatablePool<T> implements Pool<T> {

	private final BasicPool<T> delegate;
	private final ValidationConfig config;
	private ValidatablePoolThread<T> validationThread;
	
	protected ValidatablePool(BasicPool<T> pool, ValidationConfig config) {
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
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		if (config.isTestOnReturn()) {
			ValidationHelper.validate(config, entry);
		}
		delegate.returnEntry(entry);
	}	
}
