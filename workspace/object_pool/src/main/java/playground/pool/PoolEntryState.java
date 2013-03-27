package playground.pool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * threadsafe
 * */
public class PoolEntryState {
	
	private AtomicLong lastValidatedAt = new AtomicLong(0);
	
	private AtomicBoolean valid = new AtomicBoolean(true);
	
	public long getLastValidatedAt() {
		return lastValidatedAt.longValue();
	}
	
	public boolean compareAndSetLastValidatedAt(long expect, long update) {
		return lastValidatedAt.compareAndSet(expect, update);
	}
	
	public boolean isValid() {
		return valid.get();
	}
	public boolean compareAndSetValid(boolean expect, boolean update) {
		return valid.compareAndSet(expect, update);
	}
}
