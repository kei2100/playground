package playground.pool.validatable;

import playground.pool.PoolEntry;
import playground.pool.ValidationConfig;

class ValidationHelper {

	static <T> boolean validate(ValidationConfig config, PoolEntry<T> entry) {
		long lastValidatedAt = entry.getState().getLastValidatedAt();
		
		if (config.isTestWithInterval()) {
			if (!intervalElapses(config, lastValidatedAt)) {
				return true;
			}
		}
		
		boolean validateSuccessful = innerValidate(entry);
		if (validateSuccessful) {
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
