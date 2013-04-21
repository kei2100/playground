package playground.pool.util;

import playground.pool.PooledObjectValidator;

public class ThrowExceptionValidator<T> implements PooledObjectValidator<T> {
	
	public ThrowExceptionValidator(PooledObjectValidator<T> validator) {
	}
	
	@Override
	public boolean validate(T pooledObject) throws Exception {
		throw new Exception();
	}

	@Override
	public void invalidate(T pooledObject) throws Exception {
		throw new Exception();
	}

}
