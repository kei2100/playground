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
			throws InterruptedException, TimeoutException, PoolException;
	
	PoolEntry<T> borrowEntry(boolean createNew) 
			throws InterruptedException, TimeoutException, PoolException;
	
	PoolEntry<T> tryBorrowEntry() throws PoolException;

	PoolEntry<T> tryBorrowEntry(boolean createNew) throws PoolException;
	
	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;
	
}