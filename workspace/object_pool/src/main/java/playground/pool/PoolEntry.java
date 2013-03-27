package playground.pool;

public interface PoolEntry<T> {

	public abstract T getObject();

	public abstract PoolEntryState getState();

	// このエントリを有効にする
	public abstract boolean validate() throws Exception;

	public abstract void invalidate() throws Exception;

}