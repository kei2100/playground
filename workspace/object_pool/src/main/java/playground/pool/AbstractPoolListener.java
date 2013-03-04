package playground.pool;

public abstract class AbstractPoolListener<T> implements PoolListener<T> {

	@Override
	public void afterBorrowSuccess(PoolEntry<T> entry) {
	}

	@Override
	public void beforeReturnEntry(PoolEntry<T> entry) {
	}
}
