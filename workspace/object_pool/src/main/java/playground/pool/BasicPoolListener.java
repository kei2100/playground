package playground.pool;

public class BasicPoolListener<T> extends AbstractPoolListener<T> {
	
	private final ValidationConfig config;
	
	protected BasicPoolListener(ValidationConfig config) {
		this.config = config;
	}
	
	@Override
	public void afterBorrowSuccess(PoolEntry<T> entry)  {
		if (config.isTestOnBorrow()) {
			boolean validateSuccess = entry.validate();
			if (validateSuccess) {
				entry.invalidate();
			}
		}
	}
	
	@Override
	public void beforeReturnEntry(PoolEntry<T> entry) {
		if (config.isTestOnReturn()) {
			boolean validateSuccess = entry.validate();
			if (validateSuccess) {
				entry.invalidate();
			}
		}
	}
}
