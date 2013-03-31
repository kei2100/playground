package playground.pool.basic;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;


public class BasicIdleEntriesQueue<T> implements IdleEntriesQueue<T>{
	
	private final PoolConfig config;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;

	public BasicIdleEntriesQueue(PoolConfig config) {
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
			try {
				entry.invalidate();
			} catch (Exception e) {
				// TODO Logger
				e.printStackTrace();
			}
		} else {
			idleEntries.add(entry);
		}
	}	
}
