package playground.pool.util;

import playground.pool.PooledObjectValidator;

public class SpyObjectValidator implements PooledObjectValidator<SpyObject>{

	@Override
	public boolean validate(SpyObject spyObject) throws Exception {
		spyObject.setValid(true);
		return true;
	}

	@Override
	public void invalidate(SpyObject spyObject) throws Exception {
		spyObject.setValid(false);
	}
}
