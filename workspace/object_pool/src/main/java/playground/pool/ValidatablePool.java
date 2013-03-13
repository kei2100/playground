package playground.pool;


public class ValidatablePool<T> extends ObservablePool<T>{
	
	private final Pool<T> pool;
	
	protected ValidatablePool(Pool<T> pool, ValidationConfig config) {
		super(pool);
		this.pool = pool;
		this.addListener(new ValidatablePoolListener<T>(config));
	}
	
}
