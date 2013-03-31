package playground.pool.validatable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import playground.pool.Pool;
import playground.pool.PoolEntry;
import playground.pool.PoolException;
import playground.pool.ValidationConfig;
import playground.pool.util.NameableDaemonThreadFactory;

class ValidatablePoolThread<T> {

	private final Pool<T> pool;
	private final ValidationConfig config;
	
	private AtomicBoolean isScheduled = new AtomicBoolean(false); 
	private ScheduledExecutorService taskBootstrapExecutor;	
	private ExecutorService taskExecutor;
	
	ValidatablePoolThread(Pool<T> pool, ValidationConfig config) {
		this.pool = pool;
		this.config = config;
	}
	
	void scheduleBackgroundValidate() {		
		if (isScheduled.get()) {
			throw new IllegalStateException("already scheduled");
		}
		if (!isScheduled.compareAndSet(false, true)) {
			throw new IllegalStateException("already scheduled");
		}
		
		taskBootstrapExecutor = 
				Executors.newScheduledThreadPool(
						1, 
						new NameableDaemonThreadFactory(ValidateTaskBootstrap.class.getSimpleName()));
		taskExecutor = 
				Executors.newFixedThreadPool(
						config.getTestThreads(), 
						new NameableDaemonThreadFactory(ValidateTask.class.getSimpleName()));
		
		ValidateTaskBootstrap bootstrap = new ValidateTaskBootstrap();
		taskBootstrapExecutor.scheduleWithFixedDelay(
				bootstrap, 
				config.getTestThreadInitialDelayMillis(),
				config.getTestThreadIntervalMillis(), 
				TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				taskExecutor.shutdownNow();
				taskBootstrapExecutor.shutdownNow();
			}
		}));
	}	
	
	// TODO threading design
	private class ValidateTaskBootstrap implements Runnable {
		private static final boolean DO_NOT_CREATE_NEW = false;
		
		private final Object dummy = new Object();		
		private ConcurrentMap<Integer, Object> alreadyValidatedCheckMap;
		
		@Override
		public void run() {
			int maxIdleEntries = pool.getPoolConfig().getMaxIdleEntries();
			alreadyValidatedCheckMap = new ConcurrentHashMap<Integer, Object>(maxIdleEntries);
			
			// TODO delete sysout
			System.out.println("----------");
			try {
				while (true) {
					PoolEntry<T> idleEntry = pool.tryBorrowEntry(DO_NOT_CREATE_NEW);

					if (idleEntry == null) {
						break;
					}
					if (isAlreadyValidated(idleEntry)) {
						pool.returnEntry(idleEntry);
						break;
					}
					
					taskExecutor.submit(new ValidateTask(idleEntry));
				}
			} catch (PoolException e) {
				// TODO Logger
				e.printStackTrace();
			}			
		}

		private boolean isAlreadyValidated(PoolEntry<T> idleEntry) {
			int hashCode = idleEntry.hashCode();
			Object result = alreadyValidatedCheckMap.putIfAbsent(hashCode, dummy);
			return (result != null); 
		}
	}
	
	private class ValidateTask implements Runnable {
		private final PoolEntry<T> idleEntry;
		
		private ValidateTask(PoolEntry<T> idleEntry) {
			this.idleEntry = idleEntry;
		}
		
		@Override
		public void run() {
			ValidationHelper.validate(config, idleEntry);
			pool.returnEntry(idleEntry);
		}
	}
}
