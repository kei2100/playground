package playground.pool;


public class PoolConfig {
	// TODO validate state
	private int maxActiveEntries = 8;
	private int initialEntries = 0;
	private int maxIdleEntries = 8;
	// 0 is wait forever to borrowed.
	private long maxWaitMillisOnBorrow = 1000;
	
	private int invalidateThreads = 0;
	private long invalidateThreadInitialDelayMillis = 1000;
	private long invalidateIntervalMillis = 1000;

	private int minIdleEntries = 0;
	private int ensureThreads = 0;
	private long ensureIntervalMillis = 1000;
	
	public PoolConfig() {
	}
		
	public boolean isInvalidateInBackground() {
		return (invalidateThreads > 0);
	}
	
	public boolean isEnsureInBackground() {
		return (ensureThreads > 0);
	}
	
	public int getMaxActiveEntries() {
		return maxActiveEntries;
	}
	public void setMaxActiveEntries(int maxActiveEntries) {
		this.maxActiveEntries = maxActiveEntries;
	}
	
	public int getInitialEntries() {
		return initialEntries;
	}
	public void setInitialEntries(int initialEntries) {
		this.initialEntries = initialEntries;
	}
	
	public long getMaxWaitMillisOnBorrow() {
		return maxWaitMillisOnBorrow;
	}
	public void setMaxWaitMillisOnBorrow(long maxWaitMillisOnBorrow) {
		this.maxWaitMillisOnBorrow = maxWaitMillisOnBorrow;
	}
	
	public int getMaxIdleEntries() {
		return maxIdleEntries;
	}
	public void setMaxIdleEntries(int maxIdleEntries) {
		this.maxIdleEntries = maxIdleEntries;
	}

	public int getMinIdleEntries() {
		return minIdleEntries;
	}
	public void setMinIdleEntries(int minIdleEntries) {
		this.minIdleEntries = minIdleEntries;
	}

	public int getInvalidateThreads() {
		return invalidateThreads;
	}
	public void setInvalidateThreads(int invalidateThreads) {
		this.invalidateThreads = invalidateThreads;
	}

	public long getInvalidateThreadInitialDelayMillis() {
		return invalidateThreadInitialDelayMillis;
	}
	public void setInvalidateThreadInitialDelayMillis(long invalidateThreadInitialDelayMillis) {
		this.invalidateThreadInitialDelayMillis = invalidateThreadInitialDelayMillis;
	}
	
	public long getInvalidateIntervalMillis() {
		return invalidateIntervalMillis;
	}
	public void setInvalidateIntervalMillis(long invalidateIntervalMillis) {
		this.invalidateIntervalMillis = invalidateIntervalMillis;
	}
		
	public int getEnsureThreads() {
		return ensureThreads;
	}
	public void setEnsureThreads(int ensureThreads) {
		this.ensureThreads = ensureThreads;
	}
	
	public long getEnsureIntervalMillis() {
		return ensureIntervalMillis;
	}
	public void setEnsureIntervalMillis(long ensureIntervalMillis) {
		this.ensureIntervalMillis = ensureIntervalMillis;
	}
}