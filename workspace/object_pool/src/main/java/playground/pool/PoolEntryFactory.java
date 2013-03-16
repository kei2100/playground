package playground.pool;

public interface PoolEntryFactory<T> {
	
	PoolEntry<T> createPoolEntry() throws CreatePoolEntryException;
}
