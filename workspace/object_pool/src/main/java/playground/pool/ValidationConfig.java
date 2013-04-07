package playground.pool;

import javax.validation.constraints.Min;

import playground.pool.util.PropertyValidator;
import playground.pool.util.PropertyValidationException;

public class ValidationConfig {
	private static byte MAX_AGE_UNLIMIT = 0;
	
	private boolean testOnBorrow = true;
	private boolean testOnReturn = false;
	@Min(0)
	private long testIntervalMillis = 0;
	@Min(0)
	private long maxAgeMillis = 0;	// 0 is no limit.
	
	@Min(0)
	private int testThreads = 0;
	@Min(0)
	private long testThreadInitialDelayMillis = 1000 * 60 * 10;	// default 10min 
	@Min(1)
	private long testThreadIntervalMillis = 1000 * 60 * 10;	// default 10min
	
	public void validateState() throws PropertyValidationException {
		validatePropValues();
	}
	
	public void validatePropValues() throws PropertyValidationException {
		PropertyValidator.INSTANCE.validate(this);
	}
	
	public boolean isMaxAgeUnlimit() {
		return maxAgeMillis == MAX_AGE_UNLIMIT;
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

	public long getMaxAgeMillis() {
		return maxAgeMillis;
	}
	public void setMaxAgeMillis(long maxAgeMillis) {
		this.maxAgeMillis = maxAgeMillis;
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
