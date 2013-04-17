package playground.pool.basic;

import playground.pool.PoolConfig;
import playground.pool.PooledObjectValidator;
import playground.pool.util.SpyObject;
import playground.pool.util.SpyObjectValidator;

class BasicPackageTestUtil {
	
	static <T> BasicPoolEntry<T> createPoolEntry(Class<T> pooledClass) {
		T pooledInstance = createPooledObject(pooledClass);
		PooledObjectValidator<T> validator = createPooledObjectValidator(pooledClass);
		
		return new BasicPoolEntry<T>(pooledInstance, validator);
	}

	private static <T> T createPooledObject(Class<T> pooledClass) {
		try {
			return pooledClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> PooledObjectValidator<T> createPooledObjectValidator(Class<T> pooledClass) {
		if (pooledClass.equals(SpyObject.class)) {
			return (PooledObjectValidator<T>) new SpyObjectValidator();
		}
		
		throw new IllegalArgumentException();
	}
	
	static <T> BasicIdleEntriesQueue<T> createQueue(Class<T> pooledClass) {
		return createQueue(pooledClass, new PoolConfig());
	}
	
	static <T> BasicIdleEntriesQueue<T> createQueue(Class<T> pooledClass, PoolConfig config) {
		return new BasicIdleEntriesQueue<T>(config);
	}
}
