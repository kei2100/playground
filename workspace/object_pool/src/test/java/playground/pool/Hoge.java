package playground.pool;

import java.util.concurrent.TimeUnit;

public class Hoge {
	public static void main(String[] args) throws Exception {
		
		BasicPool<String> poolbase = new BasicPool<String>(new PoolConfig(),
				new BasicPoolEntryFactory<String>(
						new TestObjectFactory(), new TestObjectValidator()));

		ValidationConfig config = new ValidationConfig();
		config.setTestOnBorrow(false);
//		config.setTestIntervalMillis(3000);
		config.setTestInBackgroundIntervalMillis(1000);
		config.setTestInBackgroundThreads(5);
		
		ValidatablePool<String> pool = new ValidatablePool<String>(poolbase, config);		
		
		pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		pool.returnEntry(new BasicPoolEntry<String>("created", new TestObjectValidator()));
		
			
		
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
