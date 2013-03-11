package playground.pool;

import java.util.concurrent.TimeoutException;

public interface Pool<T> {

	PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePoolEntryException;
	
	// TODO interfaceとして必要か？
	PoolEntry<T> tryBorrowIdleEntry() 
			throws CreatePoolEntryException;

	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;

}