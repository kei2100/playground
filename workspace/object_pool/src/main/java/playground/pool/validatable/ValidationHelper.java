package playground.pool.validatable;

import playground.pool.PoolEntry;
import playground.pool.PoolEntryState;
import playground.pool.ValidationConfig;

public class ValidationHelper {

	static <T> boolean validate(ValidationConfig config, PoolEntry<T> entry) {
		PoolEntryState state = entry.getState();
		long lastValidatedAt = state.getLastValidatedAt();
		
		if (config.isTestWithInterval()) {
			if (!intervalElapses(config, lastValidatedAt)) {
				return true;
			}
		}
		
		boolean validateSuccessful = innerValidate(entry);
		if (validateSuccessful) {
			long now = System.currentTimeMillis();
			state.compareAndSetLastValidatedAt(lastValidatedAt, now);
			return true;
		} else {
			innerInvalidate(entry);
			return false;
		}
	}

	private static <T> boolean innerValidate(PoolEntry<T> entry) {
		try {
			boolean validateSuccessful = entry.validate();
			return validateSuccessful;
		} catch (Exception e) {
			// TODO Logger
			e.printStackTrace();
			innerInvalidate(entry);
			return false;
		}
	}
	
	private static <T> void innerInvalidate(PoolEntry<T> entry) {
		try {
			entry.invalidate();
		} catch (Exception e) {
			// TODO Logger
			e.printStackTrace();
		}
	}
	
	static boolean intervalElapses(ValidationConfig config, long lastValidatedAt) {				
		long testIntervalMillis = config.getTestIntervalMillis();
		long now = System.currentTimeMillis();
		
		return testIntervalMillis < (now - lastValidatedAt);
	}
}
