package playground.pool;

public interface PoolListener<T> {
	
	void afterBorrowSuccess(PoolEntry<T> entry); 
	
	void beforeReturnEntry(PoolEntry<T> entry);
}
