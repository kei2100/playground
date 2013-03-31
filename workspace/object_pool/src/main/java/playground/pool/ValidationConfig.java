package playground.pool;

public class ValidationConfig {
	
	// TODO default value
	private boolean testOnBorrow = true;
	private boolean testOnReturn = false;
	private long testIntervalMillis = 0;
	
	private int testInBackgroundThreads = 1;
	private long testInBackgroundInitialDelayMillis = 3000; 
	private long testInBackgroundIntervalMillis = 1000;

	// TODO maxage
	// TODO validateState
	
	public ValidationConfig() {
	}
	
	public boolean isTestWithInterval() {
		return testIntervalMillis > 0;
	}
	
	public boolean isTestInBackground() {
		return testInBackgroundIntervalMillis > 0;
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

	public int getTestInBackgroundThreads() {
		return testInBackgroundThreads;
	}
	public void setTestInBackgroundThreads(int testInBackgroundThreads) {
		this.testInBackgroundThreads = testInBackgroundThreads;
	}
	
	public long getTestInBackgroundInitialDelayMillis() {
		return testInBackgroundInitialDelayMillis;
	}
	public void setTestInBackgroundInitialDelayMillis(long testInBackgroundInitialDelayMillis) {
		this.testInBackgroundInitialDelayMillis = testInBackgroundInitialDelayMillis;
	}

	public long getTestInBackgroundIntervalMillis() {
		return testInBackgroundIntervalMillis;
	}
	public void setTestInBackgroundIntervalMillis(long testInBackgroundIntervalMillis) {
		this.testInBackgroundIntervalMillis = testInBackgroundIntervalMillis;
	}
}
