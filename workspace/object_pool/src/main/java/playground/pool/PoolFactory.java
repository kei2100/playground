package playground.pool;

/**
 * A factory class for {@link Pool}
 * 
 * @param <T> the type of included object in {@link PoolEntry}
 * */
public class PoolFactory<T> {
	
	private final PoolConfig poolConfig;
	private final ValidationConfig validationConfig;
	
	public PoolFactory(PoolConfig poolConfig, ValidationConfig validationConfig) {
		this.poolConfig = poolConfig;
		this.validationConfig = validationConfig;
	}
	
	public Pool<T> createInstance() {
		// TODO impl
		return null;
	}
}
