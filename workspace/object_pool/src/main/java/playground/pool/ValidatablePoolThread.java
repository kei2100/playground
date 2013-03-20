package playground.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import playground.pool.util.NameableThreadFactory;

public class ValidatablePoolThread<T> {

	private final BasicPool<T> pool;
	private final ValidationConfig config;
	
	private ScheduledExecutorService bootstrapTaskExecutor;	
	private ExecutorService taskExecutor;
	
	protected ValidatablePoolThread(BasicPool<T> pool, ValidationConfig config) {
		this.pool = pool;
		this.config = config;
	}
	
	protected void scheduleBackgroundValidate() {
		bootstrapTaskExecutor = 
				Executors.newScheduledThreadPool(
						1, new NameableThreadFactory(ValidateTaskBootstrap.class.getSimpleName()));
		taskExecutor = 
				Executors.newFixedThreadPool(
						config.getTestInBackgroundThreads(), new NameableThreadFactory(ValidateTask.class.getSimpleName()));
		
		ValidateTaskBootstrap bootstrap = new ValidateTaskBootstrap();
		bootstrapTaskExecutor.scheduleWithFixedDelay(
				bootstrap, 
				config.getTestInBackgroundInitialDelayMillis(),
				config.getTestInBackgroundIntervalMillis(), 
				TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				taskExecutor.shutdownNow();
				bootstrapTaskExecutor.shutdownNow();
			}
		}));
	}	
	
	private class ValidateTaskBootstrap implements Runnable {
		@Override
		public void run() {
			int maxIdleEntries = pool.getPoolConfig().getMaxIdleEntries();
			ConcurrentMap<Integer, Object> alreadyValidatedCheckMap = 
					new ConcurrentHashMap<Integer, Object>(maxIdleEntries);
			
			// TODO delete sysout
			System.out.println("----------");
			int threadPoolSize = config.getTestInBackgroundThreads();
			for (int i = 0; i < threadPoolSize; i++) {
				ValidateTask task = new ValidateTask(alreadyValidatedCheckMap);
				taskExecutor.submit(task);
			}			
		}
	}
	
	private class ValidateTask implements Callable<Void> {
		private final ConcurrentMap<Integer, Object> alreadyValidatedCheckMap;

		public ValidateTask(ConcurrentMap<Integer, Object> alreadyValidatedCheckMap) {
			this.alreadyValidatedCheckMap = alreadyValidatedCheckMap;
		}

		@Override
		public Void call() {
			// validate idleEntries.
			PoolEntry<T> idleEntry = null;
			IdleEntriesQueue<T> idleEntries = pool.getIdleEntries();
			
			while ((idleEntry = idleEntries.poll()) != null) {				
				if (isAlreadyValidated(idleEntry)) {
					pool.returnEntry(idleEntry);
					break;
				}
				validateAndReturn(idleEntry);
			}
			return null;
		}


		private boolean isAlreadyValidated(PoolEntry<T> idleEntry) {
			int hashCode = idleEntry.hashCode();
			Object dummy = new Object();
			
			Object result = alreadyValidatedCheckMap.putIfAbsent(hashCode, dummy);
			return (result != null); 
		}

		private void validateAndReturn(PoolEntry<T> idleEntry) {
			ValidationHelper.validate(config, idleEntry);
			pool.returnEntry(idleEntry);
		}
	}

}
