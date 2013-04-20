package playground.pool.basic;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.util.PoolLoggerMarkerFactory;


public class BasicIdleEntriesQueue<T> implements IdleEntriesQueue<T>{
	private static final Logger logger = LoggerFactory.getLogger(BasicIdleEntriesQueue.class);
	
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
	public boolean offer(PoolEntry<T> entry) throws NullPointerException {
		if (entry == null) throw new NullPointerException("entry is null.");
		
		int idleCount = idleEntriesCount.incrementAndGet();

		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			try {
				entry.invalidate();
			} catch (Exception e) {
				logger.warn(PoolLoggerMarkerFactory.getMarker(), 
						"Invalidate PoolEntry throws Exception.", e);
			}
			return false;
		} else {
			idleEntries.add(entry);
			return true;
		}
	}
	
	/*
	 * This method is typically used for debugging and testing purposes.
	 * */	
	int getIdleEntriesCount() {
		return idleEntriesCount.intValue();
	}
}
