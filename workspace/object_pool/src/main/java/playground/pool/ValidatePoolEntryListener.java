package playground.pool;


public class ValidatePoolEntryListener<T> extends AbstractPoolListener<T> {

	private final ValidationConfig config;

	protected ValidatePoolEntryListener(ValidationConfig config) {
		this.config = config;
	}

	@Override
	public void afterBorrowSuccess(PoolEntry<T> entry) {
		if (!config.isTestOnBorrow()) {
			return;
		}
		validate(entry);
	}

	@Override
	public void beforeReturnEntry(PoolEntry<T> entry) {
		if (!config.isTestOnReturn()) {
			return;
		}
		validate(entry);
	}

	private void validate(PoolEntry<T> entry) {
		PoolEntryState state = entry.getState();
		long lastValidatedAt = state.getLastValidatedAt();
		
		// TODO refacor -> config.isValidateWithInterval
		if (validateWithInterval()) {
			if (!intervalElapses(lastValidatedAt)) {
				return;
			}
		}

		boolean validateSuccess = entry.validate();
		if (validateSuccess) {
			long now = System.currentTimeMillis();
			state.compareAndSetLastValidatedAt(lastValidatedAt, now);
		} else {
			entry.invalidate();
		}
	}

	private boolean validateWithInterval() {
		return config.getTestIntervalSecond() > 0;
	}

	private boolean intervalElapses(long lastValidatedAt) {
		int testIntervalSecond = config.getTestIntervalSecond();
		long now = System.currentTimeMillis();
		
		return testIntervalSecond < (now - lastValidatedAt);
	}
}
