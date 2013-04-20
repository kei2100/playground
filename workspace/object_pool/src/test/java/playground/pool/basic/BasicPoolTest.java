package playground.pool.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.PoolException;
import playground.pool.util.SpyObject;
import playground.pool.util.ThrowExceptionObjectFactory;

public class BasicPoolTest {
	
	@Test
	public void borrowEntry_maxActiveEntriesを超えない_initialEntriesを超えない場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		TimeUnit.MILLISECONDS.sleep(1);
		long beforeExec = System.currentTimeMillis();
		TimeUnit.MILLISECONDS.sleep(1);
		
		PoolEntry<SpyObject> entry1 = pool.borrowEntry(1, TimeUnit.MILLISECONDS);
		PoolEntry<SpyObject> entry2 = pool.borrowEntry(1, TimeUnit.MILLISECONDS);
		
		assertTrue(entry1.getObject().getCreatedAt() < beforeExec);
		assertTrue(entry2.getObject().getCreatedAt() < beforeExec);
		assertEquals(0, pool.availablePermits());
	}
	
	@Test
	public void borrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewする場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		TimeUnit.MILLISECONDS.sleep(1);
		long beforeExec = System.currentTimeMillis();
		TimeUnit.MILLISECONDS.sleep(1);
		
		boolean createNew = true;
		PoolEntry<SpyObject> entry1 = pool.borrowEntry(createNew, 1, TimeUnit.MILLISECONDS);
		PoolEntry<SpyObject> entry2 = pool.borrowEntry(createNew, 1, TimeUnit.MILLISECONDS);
		
		assertTrue(entry1.getObject().getCreatedAt() < beforeExec);
		assertTrue(entry2.getObject().getCreatedAt() > beforeExec);
		assertEquals(0, pool.availablePermits());
	}

	@Test
	public void borrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewしない場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		boolean notCreateNew = false;
		PoolEntry<SpyObject> entry1 = pool.borrowEntry(notCreateNew, 1, TimeUnit.MILLISECONDS);
		PoolEntry<SpyObject> entry2 = pool.borrowEntry(notCreateNew, 1, TimeUnit.MILLISECONDS);
		
		assertNotNull(entry1);
		assertNull(entry2);
		assertEquals(1, pool.availablePermits());
	}

	@Test
	public void borrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewで例外の場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);

		PoolEntryFactory<SpyObject> entryFactory = 
				BasicPackageTestUtil.createPoolEntryFactory(
						SpyObject.class, new ThrowExceptionObjectFactory<SpyObject>(SpyObject.class, 2));
		IdleEntriesQueue<SpyObject> queue = 
				BasicPackageTestUtil.createQueue(SpyObject.class, config);
		
		BasicPool<SpyObject> pool = new BasicPool<SpyObject>(config, queue, entryFactory);
		assertNotNull(pool.borrowEntry(1, TimeUnit.MILLISECONDS));
		try {
			pool.borrowEntry(1, TimeUnit.MILLISECONDS);
		} catch (PoolException e) {
			assertEquals(1, pool.availablePermits());
			return;
		}
		fail();
	}
	
	@Test(expected = TimeoutException.class)
	public void borrowEntry_maxActiveEntriesを超える場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		PoolEntry<SpyObject> entry1 = pool.borrowEntry(1, TimeUnit.MILLISECONDS);
		PoolEntry<SpyObject> entry2 = pool.borrowEntry(1, TimeUnit.MILLISECONDS);
		
		assertNotNull(entry1);
		assertNotNull(entry2);
		
		pool.borrowEntry(1, TimeUnit.MILLISECONDS);
	}

	@Test
	public void tryBorrowEntry_maxActiveEntriesを超えない_initialEntriesを超えない場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		TimeUnit.MILLISECONDS.sleep(1);
		long beforeExec = System.currentTimeMillis();
		TimeUnit.MILLISECONDS.sleep(1);
		
		PoolEntry<SpyObject> entry1 = pool.tryBorrowEntry();
		PoolEntry<SpyObject> entry2 = pool.tryBorrowEntry();
		
		assertTrue(entry1.getObject().getCreatedAt() < beforeExec);
		assertTrue(entry2.getObject().getCreatedAt() < beforeExec);
		assertEquals(0, pool.availablePermits());
	}
	
	@Test
	public void tryBorrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewする場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		TimeUnit.MILLISECONDS.sleep(1);
		long beforeExec = System.currentTimeMillis();
		TimeUnit.MILLISECONDS.sleep(1);
		
		boolean createNew = true;
		PoolEntry<SpyObject> entry1 = pool.tryBorrowEntry(createNew);
		PoolEntry<SpyObject> entry2 = pool.tryBorrowEntry(createNew);
		
		assertTrue(entry1.getObject().getCreatedAt() < beforeExec);
		assertTrue(entry2.getObject().getCreatedAt() > beforeExec);
		assertEquals(0, pool.availablePermits());
	}

	@Test
	public void tryBorrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewしない場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		boolean notCreateNew = false;
		PoolEntry<SpyObject> entry1 = pool.tryBorrowEntry(notCreateNew);
		PoolEntry<SpyObject> entry2 = pool.tryBorrowEntry(notCreateNew);
		
		assertNotNull(entry1);
		assertNull(entry2);
		assertEquals(1, pool.availablePermits());
	}

	@Test
	public void tryBorrowEntry_maxActiveEntriesを超えない_initialEntriesを超える_createNewで例外の場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(1);

		PoolEntryFactory<SpyObject> entryFactory = 
				BasicPackageTestUtil.createPoolEntryFactory(
						SpyObject.class, new ThrowExceptionObjectFactory<SpyObject>(SpyObject.class, 2));
		IdleEntriesQueue<SpyObject> queue = 
				BasicPackageTestUtil.createQueue(SpyObject.class, config);
		
		BasicPool<SpyObject> pool = new BasicPool<SpyObject>(config, queue, entryFactory);
		assertNotNull(pool.tryBorrowEntry());
		try {
			pool.tryBorrowEntry();
		} catch (PoolException e) {
			assertEquals(1, pool.availablePermits());
			return;
		}
		fail();
	}
	
	@Test
	public void tryBorrowEntry_maxActiveEntriesを超える場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config);
		
		PoolEntry<SpyObject> entry1 = pool.tryBorrowEntry();
		PoolEntry<SpyObject> entry2 = pool.tryBorrowEntry();
		PoolEntry<SpyObject> entry3 = pool.tryBorrowEntry();
		
		assertNotNull(entry1);
		assertNotNull(entry2);
		assertNull(entry3);
	}
	
	@Test
	public void returnEntry_maxActiveEntriesを超えない_returnするentryがvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		
		PoolEntry<SpyObject> entry = pool.borrowEntry();
		assertEquals(1, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
		
		pool.returnEntry(entry);
		assertEquals(2, pool.availablePermits());
		assertEquals(2, queue.getIdleEntriesCount());
	}

	@Test
	public void returnEntry_maxActiveEntriesを超えない_returnするentryがinvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		
		PoolEntry<SpyObject> entry = pool.borrowEntry();
		assertEquals(1, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
		
		entry.invalidate();
		pool.returnEntry(entry);
		assertEquals(2, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
	}

	@Test
	public void returnEntry_maxActiveEntriesを超える_returnするentryがvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		
		BasicPoolEntry<SpyObject> exceededEntry = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		pool.returnEntry(exceededEntry);
		
		assertEquals(2, pool.availablePermits());
		assertEquals(2, queue.getIdleEntriesCount());
	}

	@Test
	public void returnEntry_maxActiveEntriesを超える_returnするentryがinvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		
		BasicPoolEntry<SpyObject> exceededEntry = BasicPackageTestUtil.createPoolEntry(SpyObject.class);
		exceededEntry.invalidate();
		pool.returnEntry(exceededEntry);
		
		assertEquals(2, pool.availablePermits());
		assertEquals(2, queue.getIdleEntriesCount());
	}
	
	@Test
	public void returnEntry_nullをreturnした場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> pool = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		
		pool.borrowEntry();
		try {
			pool.returnEntry(null);
		} catch (NullPointerException e) {
			assertEquals(1, pool.availablePermits());
			assertEquals(1, queue.getIdleEntriesCount());
			return;
		}
		fail();
	}
}
