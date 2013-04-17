package playground.pool.basic;

import static org.junit.Assert.*;

import org.junit.Test;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.util.SpyObject;

public class BasicIdleEntriesQueueTest {
	
	@Test(expected = NullPointerException.class)
	public void add_追加エントリがnullの場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		queue.add(null);
	}
	
	@Test
	public void add_maxIdleEntries数を超えない場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPoolEntry<SpyObject> entry = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		
		queue.add(entry);
		int actualCount = queue.getIdleEntriesCount();
		
		assertEquals(1, actualCount);
		assertTrue(entry.getObject().isValid());
	}

	@Test
	public void add_maxIdleEntries数を超える場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPoolEntry<SpyObject> entry1 = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		BasicPoolEntry<SpyObject> entry2 = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		
		queue.add(entry1);
		queue.add(entry2);
		int actualCount = queue.getIdleEntriesCount();
		
		assertEquals(1, actualCount);
		assertTrue(entry1.getObject().isValid());
		assertFalse(entry2.getObject().isValid());
	}
	
	@Test
	public void poll_アイドルエントリが空の場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		int actualCount = queue.getIdleEntriesCount();
		
		assertNull(actualObject);
		assertEquals(0, actualCount);
	}
	
	@Test
	public void poll_アイドルエントリが空でない場合() {
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class);
		
		queue.add(BasicPackageTestUtil.createPoolEntry(SpyObject.class));
		queue.add(BasicPackageTestUtil.createPoolEntry(SpyObject.class));
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		int actualCount = queue.getIdleEntriesCount();
		
		assertNotNull(actualObject);
		assertEquals(1, actualCount);
	}
}
