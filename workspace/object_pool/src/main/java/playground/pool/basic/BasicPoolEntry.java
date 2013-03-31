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
	
	// このエントリの有効性を確認する
	@Override
	public boolean validate() throws Exception {
		boolean expectValid = state.isValid();
		boolean updateValid = validator.validate(object);
		boolean updateSuccessful = state.compareAndSetValid(expectValid, updateValid);
		
		// return true, if entry is not invalidated while setting the state
		return (updateValid && updateSuccessful);
	}
	
	@Override
	public void invalidate() throws Exception {
		boolean expectValid = state.isValid();
		boolean updateSuccessful = state.compareAndSetValid(expectValid, false);
		
		// invalidate only once
		if (expectValid && updateSuccessful) {
			validator.invalidate(object);
		}
	}
}
