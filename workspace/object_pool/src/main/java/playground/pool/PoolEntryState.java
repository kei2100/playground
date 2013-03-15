package playground.pool;

import java.util.concurrent.atomic.AtomicLong;

/**
 * threadsafe
 * */
public class PoolEntryState {
	
	private AtomicLong lastValidatedAt = new AtomicLong(0);
	
	private volatile boolean valid = true;
	
	public long getLastValidatedAt() {
		return lastValidatedAt.longValue();
	}
	
	public void compareAndSetLastValidatedAt(long expect, long update) {
		lastValidatedAt.compareAndSet(expect, update);
	}
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
