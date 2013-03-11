package playground.pool;

public class ValidationConfig {
	
	private boolean testOnBorrow = false;
	
	private boolean testOnReturn = false;
	
	private int testIntervalSecond = 0;
	
	public ValidationConfig() {
	}
	
	public boolean isTestWithInterval() {
		return testIntervalSecond > 0;
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
	
	public int getTestIntervalSecond() {
		return testIntervalSecond;
	}
	public void setTestIntervalSecond(int testIntervalSecond) {
		this.testIntervalSecond = testIntervalSecond;
	}
}
