package playground.pool;

public class BasicPoolEntry<T> implements PoolEntry<T> {
	
	private final T object;
	private final PoolEntryState state; 
	
	protected BasicPoolEntry(T object) {
		this.object = object;
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
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	
	@Override
	public void invalidate() {
		// TODO 自動生成されたメソッド・スタブ
	}
}
