package playground.pool;

import java.util.concurrent.TimeoutException;

/**
 * Object pool 
 * */
public interface Pool<T> {
	
	/**
	 * Returns this configration
	 * @return {@link PoolConfig} 
	 * */
	PoolConfig getPoolConfig();
	
	PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePoolEntryException;
	
	PoolEntry<T> borrowEntry(boolean createNew) 
			throws InterruptedException, TimeoutException, CreatePoolEntryException;
	
	PoolEntry<T> tryBorrowEntry() throws CreatePoolEntryException;

	PoolEntry<T> tryBorrowEntry(boolean createNew) throws CreatePoolEntryException;
	
	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;
	
}