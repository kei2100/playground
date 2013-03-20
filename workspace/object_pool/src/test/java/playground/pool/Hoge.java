package playground.pool;

import java.util.concurrent.TimeUnit;

public class Hoge {
	public static void main(String[] args) throws Exception {
		PoolConfig poolConfig = new PoolConfig();
		poolConfig.setInvalidateThreads(4);
		poolConfig.setInvalidateIntervalMillis(3000);
		
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
		
		while(true) {
			TimeUnit.SECONDS.sleep(2);
			
			pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
			pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
			pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		}
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
