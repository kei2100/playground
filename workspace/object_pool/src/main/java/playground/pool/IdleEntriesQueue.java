package playground.pool;

/**
 * Queue for Idle {@link PoolEntry} interface.
 * 
 * @param <T> the type of included object in {@link PoolEntry}  
 * */
public interface IdleEntriesQueue<T> {
	
	/**
	 * Retrieves and removes the head of this queue, 
	 * or returns null if this queue is empty.
	 * 
	 * @return the head of this queue, or null if this queue is empty
	 * */
	PoolEntry<T> poll();
	
	/**
	 * Inserts the specified element at the tail of this queue.
	 * 
	 * @param entry the element to add
	 * @throws NullPointerException if the specified element is null
	 * */
	void add(PoolEntry<T> entry) throws NullPointerException;
}
