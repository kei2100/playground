package playground.pool;

import java.util.concurrent.TimeoutException;

public interface Pool<T> {

	PoolEntry<T> borrowEntry() throws InterruptedException, TimeoutException;

	PoolEntry<T> tryBorrowEntry();

	void returnEntry(PoolEntry<T> entry) throws NullPointerException;

}