package playground.pool;

// TODO
public interface PooledObjectValidator<T> {
	
	boolean validate(T pooledObject) throws Exception;
	
	void invalidate(T pooledObject) throws Exception;
}
