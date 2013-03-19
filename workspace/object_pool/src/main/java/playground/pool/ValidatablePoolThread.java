package playground.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// TODO Executorの生成を抑える
public class ValidatablePoolThread<T> {

	private final BasicPool<T> pool;
	private final ValidationConfig config;
	
	protected ValidatablePoolThread(BasicPool<T> pool, ValidationConfig config) {
		this.pool = pool;
		this.config = config;
	}
	
	protected void scheduleBackgroundValidation() {
		final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ValidateTaskBootstrap bootstrap = new ValidateTaskBootstrap();
		
		ses.scheduleWithFixedDelay(
				bootstrap, 
				config.getTestInBackgroundInitialDelayMillis(),
				config.getTestInBackgroundIntervalMillis(), 
				TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				ses.shutdownNow();
			}
		}));
	}	
	
	private class ValidateTaskBootstrap implements Runnable {
		@Override
		public void run() {			
			int threadPoolSize = config.getTestInBackgroundThreads();			
			int maxIdleEntries = pool.getPoolConfig().getMaxIdleEntries();

			List<ValidateTask> tasks = new ArrayList<ValidateTask>(threadPoolSize);
			ConcurrentMap<Integer, Object> alreadyValidatedCheckMap = 
					new ConcurrentHashMap<Integer, Object>(maxIdleEntries);
			
			for (int i = 0; i < threadPoolSize; i++) {
				tasks.add(new ValidateTask(alreadyValidatedCheckMap));
			}
			
			ExecutorService es = Executors.newFixedThreadPool(tasks.size());
			try {
				es.invokeAll(tasks);
			} catch (InterruptedException e) {
				es.shutdownNow();
			}
			es.shutdown();
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
			while ((idleEntry = pool.pollIdleEntry()) != null) {				
				if (isAlreadyValidated(idleEntry)) {
					pool.returnEntry(idleEntry);
					break;
				}
				validateAndReturn(idleEntry);
			}
			
			// validate idleEntriesToBeInvalidate.
			PoolEntry<T> idleEntryToBeInvalidate = null;
			while ((idleEntryToBeInvalidate = pool.pollIdleEntryToBeInvalidate()) != null) {				
				if (isAlreadyValidated(idleEntryToBeInvalidate)) {
					pool.returnEntry(idleEntryToBeInvalidate);
					break;
				}
				validateAndReturn(idleEntryToBeInvalidate);
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
