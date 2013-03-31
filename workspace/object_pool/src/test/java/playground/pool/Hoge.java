package playground.pool;

import java.util.concurrent.TimeUnit;

import playground.pool.asyncadjust.AsyncAdjustIdleEntriesQueue;
import playground.pool.basic.BasicPool;
import playground.pool.basic.BasicPoolEntryFactory;
import playground.pool.validatable.ValidatablePool;
import playground.pool.validatable.ValidationConfig;


public class Hoge {
	public static void main(String[] args) throws Exception {
		PoolConfig poolConfig = new PoolConfig();
		poolConfig.setMaxActiveEntries(5);
		poolConfig.setInitialEntries(5);
		poolConfig.setMaxIdleEntries(5);
		poolConfig.setMinIdleEntries(2);
		poolConfig.setInvalidateThreads(4);
		poolConfig.setInvalidateIntervalMillis(5);
		poolConfig.setEnsureThreads(2);
		poolConfig.setEnsureIntervalMillis(30);
		
		BasicPoolEntryFactory<String> entryFactory = new BasicPoolEntryFactory<String>(
				new TestObjectFactory(), new TestObjectValidator());
		
		BasicPool<String> poolbase = new BasicPool<String>(
				poolConfig,
				entryFactory,
//				new BasicIdleEntriesQueue<String>(poolConfig)
				new AsyncAdjustIdleEntriesQueue<String>(poolConfig, entryFactory)
				);

		ValidationConfig config = new ValidationConfig();
		config.setTestOnBorrow(false);
//		config.setTestIntervalMillis(3000);
		config.setTestInBackgroundIntervalMillis(1000);
		config.setTestInBackgroundThreads(4);
				
		ValidatablePool<String> pool = new ValidatablePool<String>(poolbase, config);		
		TimeUnit.SECONDS.sleep(6);

//		while(true) {
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(30);
		
		pool.borrowEntry();
		pool.borrowEntry();
		pool.borrowEntry();
		pool.borrowEntry();
		pool.borrowEntry();
		TimeUnit.MILLISECONDS.sleep(500);
		
		pool.returnEntry(entryFactory.createPoolEntry());
		pool.returnEntry(entryFactory.createPoolEntry());
		pool.returnEntry(entryFactory.createPoolEntry());
		pool.returnEntry(entryFactory.createPoolEntry());
		pool.returnEntry(entryFactory.createPoolEntry());
		TimeUnit.MILLISECONDS.sleep(500);
//		}
		
		
//		while(true) {
//			PoolEntry<String> entry = pool.borrowEntry();
//			pool.returnEntry(entry);
//		}
	}
	
	public static class TestObjectFactory implements PooledObjectFactory<String> {
		@Override
		public String createInstance() throws Exception {
			return "created";
		}
	}
	
	public static class TestObjectValidator implements PooledObjectValidator<String> {
		@Override
		public boolean validate(String pooledObject) {
			System.out.println("validated");
			return true;
		}

		@Override
		public void invalidate(String pooledObject) {
			System.out.println("invalidate string " + pooledObject);
			pooledObject = null;
		}
	}

}
