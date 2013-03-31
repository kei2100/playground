package playground.pool;

public class PoolConfig {
	// TODO default value
	// TODO validate state
	private int initialEntries = 5;
	private int maxActiveEntries = 10;
	private long maxWaitMillisOnBorrow = 3000;
	
	private int maxIdleEntries = 5;
	private int invalidateThreads = 1;
	private long invalidateIntervalMillis = 1000;

	private int minIdleEntries = 2;
	private int ensureThreads = 1;
	private long ensureIntervalMillis = 1000;
	
	public PoolConfig() {
	}

	public int getInitialEntries() {
		return initialEntries;
	}
	public void setInitialEntries(int initialEntries) {
		this.initialEntries = initialEntries;
	}
	
	public int getMaxActiveEntries() {
		return maxActiveEntries;
	}
	public void setMaxActiveEntries(int maxActiveEntries) {
		this.maxActiveEntries = maxActiveEntries;
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