package playground.pool.basic;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntryFactory;
import playground.pool.PooledObjectFactory;
import playground.pool.PooledObjectValidator;
import playground.pool.basic.BasicIdleEntriesQueue;
import playground.pool.basic.BasicPool;
import playground.pool.basic.BasicPoolEntry;
import playground.pool.basic.BasicPoolEntryFactory;
import playground.pool.util.SpyObject;
import playground.pool.util.SpyObjectFactory;
import playground.pool.util.SpyObjectValidator;
import playground.pool.util.ThrowExceptionValidator;

public class PoolTestUtil {
	
	public static <T> BasicPoolEntry<T> createPoolEntry(Class<T> pooledClass) {
		T pooledInstance = createPooledObject(pooledClass);
		PooledObjectValidator<T> validator = createPooledObjectValidator(pooledClass);
		
		return new BasicPoolEntry<T>(pooledInstance, validator);
	}
	
	public static <T> BasicPoolEntry<T> createPoolEntry(Class<T> pooledClass, PooledObjectValidator<T> validator) {
		T pooledInstance = createPooledObject(pooledClass);
		return new BasicPoolEntry<T>(pooledInstance, validator);
	}

	public static <T> ThrowExceptionValidator<T> createThrowExceptionValidator(Class<T> pooledClass) {
		PooledObjectValidator<T> validator = createPooledObjectValidator(pooledClass);
		return new ThrowExceptionValidator<T>(validator);
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
		return createPool(config, queue, createPoolEntryFactory(pooledClass));
	}

	public static <T> BasicPool<T> createPool(PoolConfig config, IdleEntriesQueue<T> queue, PoolEntryFactory<T> factory) {
		return new BasicPool<T>(config, queue, factory);
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
