package playground.pool.basic;

import org.junit.Test;
import static org.junit.Assert.*;

import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.util.SpyObject;
import playground.pool.util.SpyObjectValidator;

public class BasicIdleEntriesQueueTest {
	
	private static final SpyObjectValidator validator = new SpyObjectValidator();
	
	private static BasicIdleEntriesQueue<SpyObject> createQueue() {
		return createQueue(new PoolConfig());
	}
	
	private static BasicIdleEntriesQueue<SpyObject> createQueue(PoolConfig config) {
		return new BasicIdleEntriesQueue<SpyObject>(config);
	}
	
	private static BasicPoolEntry<SpyObject> createPoolEntry() {
		return new BasicPoolEntry<SpyObject>(new SpyObject(), validator);
	}
	
	@Test(expected = NullPointerException.class)
	public void add_追加エントリがnullの場合() {
		BasicIdleEntriesQueue<SpyObject> queue = createQueue();
		queue.add(null);
	}
	
	@Test
	public void add_maxIdleEntries数を超えない場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = createQueue(config);
		BasicPoolEntry<SpyObject> entry = createPoolEntry();
		
		queue.add(entry);
		int actualCount = queue.getIdleEntriesCount();
		
		assertEquals(1, actualCount);
		assertTrue(entry.getObject().isValid());
	}

	@Test
	public void add_maxIdleEntries数を超える場合() {
		PoolConfig config = new PoolConfig();
		config.setMaxIdleEntries(1);
		BasicIdleEntriesQueue<SpyObject> queue = createQueue(config);
		BasicPoolEntry<SpyObject> entry1 = createPoolEntry();
		BasicPoolEntry<SpyObject> entry2 = createPoolEntry();
		
		queue.add(entry1);
		queue.add(entry2);
		int actualCount = queue.getIdleEntriesCount();
		
		assertEquals(1, actualCount);
		assertTrue(entry1.getObject().isValid());
		assertFalse(entry2.getObject().isValid());
	}
	
	@Test
	public void poll_アイドルエントリが空の場合() {
		BasicIdleEntriesQueue<SpyObject> queue = createQueue();
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		int actualCount = queue.getIdleEntriesCount();
		
		assertNull(actualObject);
		assertEquals(0, actualCount);
	}
	
	@Test
	public void poll_アイドルエントリが空でない場合() {
		BasicIdleEntriesQueue<SpyObject> queue = createQueue();
		
		queue.add(createPoolEntry());
		queue.add(createPoolEntry());
		
		PoolEntry<SpyObject> actualObject = queue.poll();
		int actualCount = queue.getIdleEntriesCount();
		
		assertNotNull(actualObject);
		assertEquals(1, actualCount);
	}
}
