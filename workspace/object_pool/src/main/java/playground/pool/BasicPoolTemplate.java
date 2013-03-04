package playground.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BasicPoolTemplate<T> implements Pool<T>{
	private Pool<T> delegate;
	private List<PoolListener<T>> listeners = new ArrayList<PoolListener<T>>();
	
	protected BasicPoolTemplate(Pool<T> pool) {
		this.delegate = pool;
	}
	
	public void setListeners(List<PoolListener<T>> listeners) {
		this.listeners = listeners;
	}
	
	public void addListener(PoolListener<T> listener) {
		listeners.add(listener);
	}
	
	@Override
	public PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePooledObjectException {
		
		PoolEntry<T> entry = delegate.borrowEntry();

		for (PoolListener<T> listener : listeners) {
			listener.afterBorrowSuccess(entry);
		}
		return entry;
	}

	@Override
	public PoolEntry<T> tryBorrowEntry() throws CreatePooledObjectException {
		
		PoolEntry<T> entry = delegate.tryBorrowEntry();
		
		if (entry != null) {
			for (PoolListener<T> listener : listeners) {
				listener.afterBorrowSuccess(entry);
			}
		}
		return entry;
	}

	@Override
	public void returnEntry(PoolEntry<T> entry) throws NullPointerException {
		
		if (entry == null) throw new NullPointerException();

		for (PoolListener<T> listener : listeners) {
			listener.beforeReturnEntry(entry);
		}

		delegate.returnEntry(entry);
	}
}
