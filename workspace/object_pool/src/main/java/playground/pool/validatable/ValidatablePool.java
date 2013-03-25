package playground.pool.validatable;

import java.util.concurrent.TimeoutException;

import playground.pool.CreatePoolEntryException;
import playground.pool.Pool;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.basic.BasicPool;


public class ValidatablePool<T> implements Pool<T> {

	private final BasicPool<T> delegate;
	private final ValidationConfig config;
	private ValidatablePoolThread<T> validationThread;
	
	public ValidatablePool(BasicPool<T> pool, ValidationConfig config) {
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
			throws InterruptedException, TimeoutException, CreatePoolEntryException {

		return borrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> borrowEntry(boolean createNew) 
			throws InterruptedException, TimeoutException, CreatePoolEntryException {
		
		PoolEntry<T> entry = delegate.borrowEntry(createNew);

		if (entry == null) {
			return null;
		}
		
		return afterBorrowEntry(entry);
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry() throws CreatePoolEntryException {
		return tryBorrowEntry(true);
	}
	
	@Override
	public PoolEntry<T> tryBorrowEntry(boolean createNew) throws CreatePoolEntryException {
		PoolEntry<T> entry = delegate.tryBorrowEntry(createNew);
		
		if (entry == null) {
			return null;
		}
		
		return afterBorrowEntry(entry);
	}

	protected PoolEntry<T> afterBorrowEntry(PoolEntry<T> entry) throws CreatePoolEntryException {
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
