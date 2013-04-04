package playground.pool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * threadsafe
 * */
public class PoolEntryState {
	private final long createdAt = System.currentTimeMillis();
	
	private AtomicLong lastValidatedAt = new AtomicLong(System.currentTimeMillis());
	
	private AtomicBoolean valid = new AtomicBoolean(true);
	
	public long getCreatedAt() {
		return createdAt;
	}
	
	public long getLastValidatedAt() {
		return lastValidatedAt.longValue();
	}
	public void setLastValidatedAt(long lastValidatedAt) {
		this.lastValidatedAt.set(lastValidatedAt);
	}
	
	public boolean isValid() {
		return valid.get();
	}
	public boolean compareAndSetValid(boolean expect, boolean update) {
		return valid.compareAndSet(expect, update);
	}
}
