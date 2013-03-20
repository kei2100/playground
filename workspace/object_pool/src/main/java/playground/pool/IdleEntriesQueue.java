package playground.pool;

public interface IdleEntriesQueue<T> {
	PoolEntry<T> poll();	
	void add(PoolEntry<T> entry) ;	
}
