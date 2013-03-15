package playground.pool;

public class ValidationHelper {

	static <T> boolean validate(ValidationConfig config, PoolEntry<T> entry) {
		PoolEntryState state = entry.getState();
		long lastValidatedAt = state.getLastValidatedAt();
		
		if (config.isTestWithInterval()) {
			if (!intervalElapses(config, lastValidatedAt)) {
				return true;
			}
		}

		boolean validateSuccessful = entry.validate();
		if (validateSuccessful) {
			long now = System.currentTimeMillis();
			state.compareAndSetLastValidatedAt(lastValidatedAt, now);
			return true;
		} else {
			entry.invalidate();
			return false;
		}
	}

	
	private static boolean intervalElapses(ValidationConfig config, long lastValidatedAt) {
		int testIntervalSecond = config.getTestIntervalSecond();
		long now = System.currentTimeMillis();
		
		return testIntervalSecond < ((now - lastValidatedAt) / 1000);
	}

}
