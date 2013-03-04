package playground.pool;

public class ValidationConfig {
	
	private boolean testOnBorrow;
	
	private boolean testOnReturn;
	
	private int testIntervalSecond;

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}
	
	public int getTestIntervalSecond() {
		return testIntervalSecond;
	}
}
