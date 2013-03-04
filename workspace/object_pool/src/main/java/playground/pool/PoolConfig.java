package playground.pool;

public class PoolConfig {
	private int initialEntries;
	private int maxActiveEntries;
	private int maxIdleEntries;
	private int maxWaitMillis;
	
	public PoolConfig() {
	}

	public int getInitialEntries() {
		return initialEntries;
	}

	public int getMaxActiveEntries() {
		return maxActiveEntries;
	}

	public int getMaxIdleEntries() {
		return maxIdleEntries;
	}

	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}
}