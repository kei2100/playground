package playground.pool;

public class ValidationConfig {
	// TODO validateState
	private boolean testOnBorrow = true;
	private boolean testOnReturn = false;
	private long testIntervalMillis = 0;

	// TODO maxage
//	private long maxAgeMillis = 0;
	
	private int testThreads = 0;
	// default 10min
	private long testThreadInitialDelayMillis = 1000 * 60 * 10; 
	// default 10min
	private long testThreadIntervalMillis = 1000 * 60 * 10;

	
	public ValidationConfig() {
	}
	
	public boolean isTestWithInterval() {
		return testIntervalMillis > 0;
	}
	
	public boolean isTestInBackground() {
		return testThreads > 0;
	}
	
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}
	
	public long getTestIntervalMillis() {
		return testIntervalMillis;
	}
	public void setTestIntervalMillis(long testIntervalMillis) {
		this.testIntervalMillis = testIntervalMillis;
	}

	public int getTestThreads() {
		return testThreads;
	}
	public void setTestThreads(int testThreads) {
		this.testThreads = testThreads;
	}
	
	public long getTestThreadInitialDelayMillis() {
		return testThreadInitialDelayMillis;
	}
	public void setTestThreadInitialDelayMillis(long testThreadInitialDelayMillis) {
		this.testThreadInitialDelayMillis = testThreadInitialDelayMillis;
	}

	public long getTestThreadIntervalMillis() {
		return testThreadIntervalMillis;
	}
	public void setTestThreadIntervalMillis(long testThreadIntervalMillis) {
		this.testThreadIntervalMillis = testThreadIntervalMillis;
	}
}
