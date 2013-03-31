package playground.pool.asyncadjust;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;

public class AsyncAdjustIdleEntriesQueue<T> implements IdleEntriesQueue<T> {

	private final PoolConfig config;
	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntries;
	private final AtomicInteger idleEntriesCount;	
	private final ConcurrentLinkedQueue<PoolEntry<T>> idleEntriesToBeInvalidate;
	
	private AsyncInvalidateThread<T> invalidateThread;
	private AsyncEnsureThread<T> ensureThread;
	private AtomicBoolean isScheduledEnsureThread = new AtomicBoolean(false);
	
	public AsyncAdjustIdleEntriesQueue(PoolConfig config, PoolEntryFactory<T> entryFactory) {
		this.config = config;
		
		idleEntries = new ConcurrentLinkedQueue<PoolEntry<T>>();
		idleEntriesCount = new AtomicInteger(0);
		idleEntriesToBeInvalidate = new ConcurrentLinkedQueue<PoolEntry<T>>();
		
		if (config.isInvalidateInBackground()) {
			invalidateThread = new AsyncInvalidateThread<T>(config, this);
			invalidateThread.startBackgroundInvalidate();
		}
		if (config.isEnsureInBackground()) {
			ensureThread = new AsyncEnsureThread<T>(config, this, entryFactory);
		}
	}
	
	@Override
	public PoolEntry<T> poll() {
		PoolEntry<T> idle = idleEntries.poll();
		if (idle == null) {
			return idleEntriesToBeInvalidate.poll();
		}
		
		// Move toBeInvalidate to idle, if toBeInvalidate is not null.
		PoolEntry<T> toBeInvalidate = idleEntriesToBeInvalidate.poll();
		if (toBeInvalidate != null) {
			idleEntries.add(toBeInvalidate);
		} else {
			idleEntriesCount.decrementAndGet();
			scheduleBackgroundEnsureIfNeed(idle);
		}
		return idle;
	}

	private void scheduleBackgroundEnsureIfNeed(PoolEntry<T> idle) {
		if (!config.isEnsureInBackground()) return;
		if (countNeedForEnsure() < 1) return;
		
		boolean expect = isScheduledEnsureThread.get();
		if (expect) return;
		
		// schedule only once
		boolean setSuccessful = isScheduledEnsureThread.compareAndSet(expect, true);
		if (setSuccessful) {
			ensureThread.scheduluBackgroundEnsure();
		}
	}
	
	@Override
	public void add(PoolEntry<T> entry) {
		int idleCount = idleEntriesCount.incrementAndGet();
		
		if (idleCount > config.getMaxIdleEntries()) {
			idleEntriesCount.decrementAndGet();
			innerInvalidate(entry);
		} else {
			idleEntries.add(entry);
		}
	}	
	
	private void innerInvalidate(PoolEntry<T> entry) {
		if (!config.isInvalidateInBackground()) {
			try {
				entry.invalidate();
			} catch (Exception e) {
				// TODO Logger
				e.printStackTrace();
			}
		} else {
			idleEntriesToBeInvalidate.add(entry);
		}
	}
	
	PoolEntry<T> pollToBeInvalidate() {
		return idleEntriesToBeInvalidate.poll();
	}
	
	int countNeedForEnsure() {
		return config.getMinIdleEntries() - idleEntriesCount.get();
	}
}
