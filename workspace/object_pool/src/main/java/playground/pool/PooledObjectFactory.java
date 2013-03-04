package playground.pool;

public interface PooledObjectFactory<T> {

	T createInstance() throws CreatePooledObjectException;
}
