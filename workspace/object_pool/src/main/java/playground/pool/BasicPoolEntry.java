package playground.pool;

public class BasicPoolEntry<T> implements PoolEntry<T> {
	
	private final T object;
	private final PoolEntryState state; 
	private final PooledObjectValidator<T> validator;
	
	protected BasicPoolEntry(T object, PooledObjectValidator<T> validator) {
		this.object = object;
		this.validator = validator;
		
		this.state = new PoolEntryState();
	}
		
	@Override
	public T getObject() {
		return object;
	}
	
	@Override
	public PoolEntryState getState() {
		return state;
	}
	
	// このエントリを有効にする
	@Override
	public boolean validate() {
		return validator.validate(object);
	}
	
	@Override
	public void invalidate() {
		validator.invalidate(object);
	}
}
