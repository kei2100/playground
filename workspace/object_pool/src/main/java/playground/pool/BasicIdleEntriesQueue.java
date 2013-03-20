package playground.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicIdleEntriesQueue<T> implements IdleEntriesQueue<T>{
	
	private final PoolConfig config;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;

	protected BasicIdleEntriesQueue(PoolConfig config) {
		this.config = config;
		
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		idleEntriesCount = new AtomicInteger(0);
	}
	
	@Override
	public PoolEntry<T> poll() {
		PoolEntry<T> idle = idleEntries.poll();

		if (idle != null) {
			idleEntriesCount.decrementAndGet();
		}
		
		return idle;
	}
	
	@Override
	public void add(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();

		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			entry.invalidate();
		} else {
			idleEntries.add(entry);
		}
	}	
}
