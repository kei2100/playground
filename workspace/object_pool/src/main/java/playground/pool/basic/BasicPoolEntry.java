package playground.pool.basic;

import playground.pool.PoolEntry;
import playground.pool.PoolEntryState;
import playground.pool.PooledObjectValidator;


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
		 boolean validateSuccessful = validator.validate(object);
		 state.setValid(validateSuccessful);
		 return validateSuccessful;
	}
	
	@Override
	public void invalidate() {
		state.setValid(false);
		validator.invalidate(object);
	}
}
