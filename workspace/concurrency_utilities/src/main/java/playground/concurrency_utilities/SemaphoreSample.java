package playground.concurrency_utilities;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import playground.concurrency_utilities.SemaphoreSample.Pool.PoolEntry;

public class SemaphoreSample {

	public static void main(String[] args) {
		Pool pool = new Pool(5, 10);

		for (int i = 0; i < 100; i++) {
			new Thread(new Worker(pool)).start();
		}
	}
	
	public static class Worker implements Runnable {
		private Pool pool;
		
		public Worker(Pool pool) {
			this.pool = pool;
		}

		public void run() {
			try {
				PoolEntry entry = pool.getEntry(1000, TimeUnit.MILLISECONDS);
				TimeUnit.MILLISECONDS.sleep(150);
				entry.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *  セマフォを使ってオブジェクトプールのようなものを実装してみる。
	 */
	public static class Pool {
		private final Semaphore semaphore;
		
		private ConcurrentLinkedQueue<PoolEntry> idleEntrys = new ConcurrentLinkedQueue<PoolEntry>();
		private ConcurrentLinkedQueue<PoolEntry> busyEntrys = new ConcurrentLinkedQueue<PoolEntry>();
				
		public Pool(int initialCapacity, int maxCapacity) {
			// プールエントリの最大数を、セマフォのパーミット数で表現。
			this.semaphore = new Semaphore(maxCapacity);
			for (int i = 0; i < initialCapacity; i++) {
				idleEntrys.add(new PoolEntry(this));
			}
		}
		
		/*
		 * プールからエントリを取得
		 */
		public PoolEntry getEntry(long timeout, TimeUnit unit)
		throws InterruptedException, TimeoutException {
			// セマフォに利用可能パーミットがなければ、すべてのプールエントリがbusyと判断できる。
			boolean acquireSuccess = semaphore.tryAcquire(timeout, unit);
			if (!acquireSuccess) {
				throw new TimeoutException(
						"PoolEntry is all busy. " + 
						"idleEntrys : " + idleEntrys.size() + 
						", busyEntrys : " + busyEntrys.size());
			}
			
			PoolEntry entry = idleEntrys.poll();
			if (entry == null) {
				// idleなエントリがプールに存在しなくても、
				// セマフォに利用可能パーミットがある = プールのキャパシティはあるはずなので
				// 新しくエントリを生成する。
				entry = new PoolEntry(this);
				System.out.println("new PoolEntry created.");
			}
			
			busyEntrys.add(entry);
			return entry;
		}
		
		/*
		 * プールにエントリを返却
		 */
		private void returnEntry(PoolEntry entry) {
			boolean wasExsisted = busyEntrys.remove(entry);
			if (!wasExsisted) {
				throw new IllegalArgumentException();
			}
			
			idleEntrys.add(entry);
			// セマフォにパーミットを一つ戻す。
			semaphore.release();
		}
		
		/*
		 * プールのエントリ
		 */
		public static class PoolEntry {
			private final Pool pool;
			
			public PoolEntry(Pool pool) {
				this.pool = pool;
			}
			
			/*
			 * エントリをプールに返却
			 */
			public void release() {
				pool.returnEntry(this);
			}
		}
	}	
}


