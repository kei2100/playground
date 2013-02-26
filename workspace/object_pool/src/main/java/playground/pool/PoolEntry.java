package playground.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PoolEntry<T> {
	private static final Logger log = LoggerFactory.getLogger(PoolEntry.class);
	
	private T object;
	private final Pool<T> pool;
	
	private long returnedAt;
	
	protected PoolEntry(Pool<T> pool, T object) {
		this.pool = pool;
		this.object = object;
		
		long createdAt = System.currentTimeMillis();
		returnedAt = createdAt;
	}
	
	public void returnObjectToPool(T object) {
		if (object != this.object) {
			log.warn(
				String.format("arg.object and this.object is not same. %d %d"
				, (object != null ? object.hashCode() : null)
				, (this.object != null ? this.object.hashCode() : null))
			);
		}
		
		returnedAt = System.currentTimeMillis();
		this.object = object;
		
		pool.returnEntry(this);
	}
	
	public T getObject() {
		return object;
	}
	
	protected long getReturnedAt() {
		return this.returnedAt;
	}
}
