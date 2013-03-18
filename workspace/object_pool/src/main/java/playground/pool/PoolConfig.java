package playground.pool;

public class PoolConfig {
	private int initialEntries = 5;
	private int maxActiveEntries = 10;
	private long maxWaitMillisOnBorrow = 3000;

	private int maxIdleEntries = 5;
	private int minIdleEntries = 2;
	private int invalidateThreads = 1;
	private long invalidateInitialDelayMillis = 1000;
	private int ensureThreads = 1;
	private long ensureInitialDelayMillis = 1000;
	
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

	public long getInvalidateInitialDelayMillis() {
		return invalidateInitialDelayMillis;
	}
	public void setInvalidateInitialDelayMillis(long invalidateInitialDelayMillis) {
		this.invalidateInitialDelayMillis = invalidateInitialDelayMillis;
	}

	public int getEnsureThreads() {
		return ensureThreads;
	}
	public void setEnsureThreads(int ensureThreads) {
		this.ensureThreads = ensureThreads;
	}

	public long getEnsureInitialDelayMillis() {
		return ensureInitialDelayMillis;
	}
	public void setEnsureInitialDelayMillis(long ensureInitialDelayMillis) {
		this.ensureInitialDelayMillis = ensureInitialDelayMillis;
	}
}