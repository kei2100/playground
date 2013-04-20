package playground.pool.basic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.util.SpyObject;

public class BasicIdleEntriesQueueTest {
	
	@Test(expected = NullPointerException.class)
	public void add_追加エントリがnullの場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		queue.offer(null);
	}
	
	@Test
	public void offer_maxIdleEntries数を超えない場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPoolEntry<SpyObject> entry = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		
		boolean offerSuccess = queue.offer(entry);
		
		assertTrue(offerSuccess);
		assertTrue(entry.getObject().isValid());
	}

	@Test
	public void offer_maxIdleEntries数を超える場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPoolEntry<SpyObject> entry1 = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		BasicPoolEntry<SpyObject> entry2 = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		
		boolean offerSuccess1 = queue.offer(entry1);
		boolean offerSuccess2 = queue.offer(entry2);
		
		assertTrue(offerSuccess1);
		assertFalse(offerSuccess2);
		assertTrue(entry1.getObject().isValid());
		assertFalse(entry2.getObject().isValid());
	}
	
	@Test
	public void poll_アイドルエントリが空の場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		assertNull(actualObject);
	}
	
	@Test
	public void poll_アイドルエントリが空でない場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		
		queue.offer(BasicPackageTestUtil.createPoolEntry(SpyObject.class));
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		assertNotNull(actualObject);
	}
	
	@Test
	public void offer_pool_マルチスレッドで繰り返す() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(5);	// 5 is common num with pool size.
		
		final BasicIdleEntriesQueue<SpyObject> queue =
				BasicPackageTestUtil.createQueue(SpyObject.class, config);
		
		ExecutorService es = Executors.newFixedThreadPool(5);	// 5 is common num with maxIdleEntries.
		List<Future<PoolEntry<SpyObject>>> futures = new ArrayList<Future<PoolEntry<SpyObject>>>();
		
		for (int i = 0; i < 50; i++) {
			Future<PoolEntry<SpyObject>> future = es.submit(
				new Callable<PoolEntry<SpyObject>>() {
					@Override
					public PoolEntry<SpyObject> call() throws Exception {
						queue.offer(BasicPackageTestUtil.createPoolEntry(SpyObject.class));
						TimeUnit.MILLISECONDS.sleep(1);
						return queue.poll();
					}
				}
			);
			futures.add(future);
		}
		
		try {
			for (Future<PoolEntry<SpyObject>> future : futures) {
				PoolEntry<SpyObject> entry = future.get();
				
				boolean actualValid = entry.getObject().isValid();
				assertTrue(actualValid);
			}
		} finally {
			es.shutdown();
		}
	}
}
