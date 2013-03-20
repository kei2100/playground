package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class IdleEntriesQueue<T> {
	
	private final PoolConfig config;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;

	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntriesToBeInvalidate;

	protected IdleEntriesQueue(PoolConfig config) {
		this.config = config;
		
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		idleEntriesCount = new AtomicInteger(0);
		
		idleEntriesToBeInvalidate = new ConcurrentLinkedQueue<PoolEntry<T>>();
	}
	
	protected PoolEntry<T> poll() {
		PoolEntry<T> idle = idleEntries.poll();

		if (idle == null) {
			return idleEntriesToBeInvalidate.poll();
		}
		
		PoolEntry<T> toBeInvalidate = idleEntriesToBeInvalidate.poll();

		// Move toBeInvalidate to idle, if toBeInvalidate is not null.
		if (toBeInvalidate != null) {
			idleEntries.add(toBeInvalidate);
		} else {
			idleEntriesCount.decrementAndGet();
		}
		
		return idle;
	}
	
	protected void add(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();

		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			idleEntriesToBeInvalidate.add(entry);
		} else {
			idleEntries.add(entry);
		}
	}
	
}
