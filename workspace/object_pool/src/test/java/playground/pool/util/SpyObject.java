package playground.pool.util;

public class SpyObject {
	
	private boolean valid = true;
	
	private long createdAt = System.currentTimeMillis();
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
}
