package playground.pool;

import java.util.concurrent.TimeoutException;

public interface Pool<T> {

	PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePooledObjectException;

	PoolEntry<T> tryBorrowEntry() 
			throws CreatePooledObjectException;

	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;

}