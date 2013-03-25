package playground.pool.basic;

import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.PooledObjectFactory;
import playground.pool.PooledObjectValidator;


public class BasicPoolEntryFactory<T> implements PoolEntryFactory<T> {

	private final PooledObjectFactory<T> objectFactory;
	private final PooledObjectValidator<T> validator;
	
	public BasicPoolEntryFactory(PooledObjectFactory<T> objectFactory, PooledObjectValidator<T> validator) {
		this.objectFactory = objectFactory;
		this.validator = validator;
	}
	
	@Override
	public PoolEntry<T> createPoolEntry() throws Exception {
		T object = null;
		try {
			object = objectFactory.createInstance();
			return new BasicPoolEntry<T>(object, validator);
		} catch (Exception e) {
			validator.invalidate(object);
			throw e;
		}
	}
}
