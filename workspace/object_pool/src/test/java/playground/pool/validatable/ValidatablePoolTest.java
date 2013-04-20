package playground.pool.validatable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import playground.pool.IdleEntriesQueue;
import playground.pool.PoolConfig;
import playground.pool.PoolEntry;
import playground.pool.PoolEntryFactory;
import playground.pool.PoolException;
import playground.pool.ValidationConfig;
import playground.pool.basic.BasicIdleEntriesQueue;
import playground.pool.basic.BasicPackageTestUtil;
import playground.pool.basic.BasicPool;
import playground.pool.util.SpyObject;
import playground.pool.util.ThrowExceptionObjectFactory;

public class ValidatablePoolTest {

	@Test
	public void borrowEntry_maxActiveEntriesを超えない_initialEntriesを超えない場合()
	throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = new BasicPool<SpyObject>(config, queue, entryFactory);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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

		BasicPool<SpyObject> delegate = new BasicPool<SpyObject>(config, queue, entryFactory);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
		
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
		PoolEntry<SpyObject> entry1 = pool.tryBorrowEntry();
		PoolEntry<SpyObject> entry2 = pool.tryBorrowEntry();
		PoolEntry<SpyObject> entry3 = pool.tryBorrowEntry();
		
		assertNotNull(entry1);
		assertNotNull(entry2);
		assertNull(entry3);
	}
	
	@Test
	public void returnEntry_returnするentryがvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
		PoolEntry<SpyObject> entry = pool.borrowEntry();
		assertEquals(1, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
		
		pool.returnEntry(entry);
		assertEquals(2, pool.availablePermits());
		assertEquals(2, queue.getIdleEntriesCount());
	}

	@Test
	public void returnEntry_returnするentryがinvalidの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
		PoolEntry<SpyObject> entry = pool.borrowEntry();
		assertEquals(1, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
		
		entry.invalidate();
		pool.returnEntry(entry);
		assertEquals(2, pool.availablePermits());
		assertEquals(1, queue.getIdleEntriesCount());
	}

	@Test
	public void returnEntry_returnするentryがnullの場合() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(2);
		config.setMaxIdleEntries(2);
		config.setInitialEntries(2);
		
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
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
	
	@Test
	public void borrow_return_マルチスレッドで繰り返す() throws Exception {
		PoolConfig config = new PoolConfig();
		config.setMaxActiveEntries(5);
		config.setMaxIdleEntries(5);
		config.setInitialEntries(5);
		
		BasicIdleEntriesQueue<SpyObject> queue = BasicPackageTestUtil.createQueue(SpyObject.class, config);
		BasicPool<SpyObject> delegate = BasicPackageTestUtil.createPool(SpyObject.class, config, queue);
		final ValidatablePool<SpyObject> pool = new ValidatablePool<SpyObject>(delegate, new ValidationConfig(), null);
		
		ExecutorService es = Executors.newFixedThreadPool(5);	// 5 is common num with maxIdleEntries.
		List<Future<Void>> futures = new ArrayList<Future<Void>>();
		
		for (int i = 0; i < 50; i++) {
			Future<Void> future = es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					PoolEntry<SpyObject> entry = pool.tryBorrowEntry();
					TimeUnit.MILLISECONDS.sleep(1);
					pool.returnEntry(entry);
					return null;
				}
			});
			futures.add(future);
		}
		
		try {
			for (Future<Void> future : futures) {
				// exceptionが発生していないこと
				future.get();
			}
		} finally {
			es.shutdown();
		}
		
		assertEquals(5, pool.availablePermits());
		assertEquals(5, queue.getIdleEntriesCount());
	}
}
