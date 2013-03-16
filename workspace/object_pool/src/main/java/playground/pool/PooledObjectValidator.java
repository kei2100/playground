package playground.pool;

public interface PooledObjectValidator<T> {
	
	boolean validate(T pooledObject);
	
	void invalidate(T pooledObject);
}
