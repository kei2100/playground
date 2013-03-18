package playground.pool;

import java.util.concurrent.TimeoutException;

public interface Pool<T> {

	PoolConfig getPoolConfig();

	PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePoolEntryException;
	
	// TODO asyncBorrow
	
	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;
	
}