package playground.pool;

import java.util.concurrent.atomic.AtomicLong;

public class PoolEntryState {
	
	private AtomicLong lastValidatedAt = new AtomicLong(0);
	
	public long getLastValidatedAt() {
		return lastValidatedAt.longValue();
	}
	
	public void compareAndSetLastValidatedAt(long expect, long update) {
		lastValidatedAt.compareAndSet(expect, update);
	}
}