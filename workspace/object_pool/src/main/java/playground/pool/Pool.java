package playground.pool;

public class Pool<T> {
	private int initEntries;
	private int maxActiveEntries;
	private int minIdleEntries;
	
	private int maxWaitMillis; 
	
	
	public PoolEntry<T> borrowEntry() {
		// TODO
		return null;
	}
	
	public void returnEntry(PoolEntry<T> entry) {
		
	}
}
