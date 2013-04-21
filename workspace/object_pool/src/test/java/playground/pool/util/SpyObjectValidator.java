package playground.pool.util;

import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.PooledObjectValidator;

public class SpyObjectValidator implements PooledObjectValidator<SpyObject>{
	
	private boolean forceInvalid = false;
	private AtomicInteger validateCallCount = new AtomicInteger(0);
	private AtomicInteger invalidateCallCount = new AtomicInteger(0);
	
	public SpyObjectValidator() {}
	
	public SpyObjectValidator(boolean forceInvalid) {
		this.forceInvalid  = forceInvalid;
	}
	
	@Override
	public boolean validate(SpyObject spyObject) throws Exception {
		validateCallCount.incrementAndGet();
		
		if (forceInvalid) {
			spyObject.invalidate();
			return false;
		}
		
		spyObject.validate();
		return true;
	}

	@Override
	public void invalidate(SpyObject spyObject) throws Exception {
		invalidateCallCount.incrementAndGet();
		spyObject.invalidate();
	}
	
	public int getValidateCallCount() {
		return validateCallCount.intValue();
	}

	public int getInvalidateCallCount() {
		return invalidateCallCount.intValue();
	}
}
