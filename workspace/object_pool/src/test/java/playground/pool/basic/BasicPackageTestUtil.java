package playground.pool.basic;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntryFactory;
import playground.pool.PooledObjectFactory;
import playground.pool.PooledObjectValidator;
import playground.pool.util.SpyObject;
import playground.pool.util.SpyObjectFactory;
import playground.pool.util.SpyObjectValidator;

public class BasicPackageTestUtil {
	
	public static <T> BasicPoolEntry<T> createPoolEntry(Class<T> pooledClass) {
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
	private static <T> PooledObjectFactory<T> createPooledObjectFactory(Class<T> pooledClass) {
		if (pooledClass.equals(SpyObject.class)) {
			return (PooledObjectFactory<T>) new SpyObjectFactory();
		}
		throw new IllegalArgumentException();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> PooledObjectValidator<T> createPooledObjectValidator(Class<T> pooledClass) {
		if (pooledClass.equals(SpyObject.class)) {
			return (PooledObjectValidator<T>) new SpyObjectValidator();
		}
		throw new IllegalArgumentException();
	}
	
	public static <T> BasicIdleEntriesQueue<T> createQueue(Class<T> pooledClass) {
		return createQueue(pooledClass, new PoolConfig());
	}
	
	public static <T> BasicIdleEntriesQueue<T> createQueue(Class<T> pooledClass, PoolConfig config) {
		return new BasicIdleEntriesQueue<T>(config);
	}
	
	public static <T> BasicPool<T> createPool(Class<T> pooledClass, PoolConfig config) {
		return createPool(pooledClass, config, createQueue(pooledClass, config));
	}
	
	public static <T> BasicPool<T> createPool(Class<T> pooledClass, PoolConfig config, IdleEntriesQueue<T> queue) {
		return 
			new BasicPool<T>(config, queue, createPoolEntryFactory(pooledClass));
	}
	
	public static <T> PoolEntryFactory<T> createPoolEntryFactory(Class<T> pooledClass) {
		PooledObjectFactory<T> objectFactory = createPooledObjectFactory(pooledClass);
		PooledObjectValidator<T> objectValidator = createPooledObjectValidator(pooledClass);
		
		return createPoolEntryFactory(objectFactory, objectValidator);
	}
	
	public static <T> PoolEntryFactory<T> createPoolEntryFactory(Class<T> pooledClass, PooledObjectFactory<T> objectFactory) {
		PooledObjectValidator<T> objectValidator = createPooledObjectValidator(pooledClass);

		return createPoolEntryFactory(objectFactory, objectValidator);
	}
	
	public static <T> PoolEntryFactory<T> createPoolEntryFactory(
			PooledObjectFactory<T> objectFactory, PooledObjectValidator<T> objectValidator) {
		return new BasicPoolEntryFactory<T>(objectFactory, objectValidator);
	}
	
}
