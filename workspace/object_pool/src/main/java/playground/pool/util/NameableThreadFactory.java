package playground.pool.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameableThreadFactory implements ThreadFactory {
	
	private final String threadName;
	private final AtomicInteger threadNum; 
	
	public NameableThreadFactory(String threadName) {
		if (threadName == null) throw new NullPointerException();
		
		this.threadName = threadName;
		this.threadNum = new AtomicInteger(1);
	}
	
	@Override
	public Thread newThread(Runnable runnable) {
		Thread th = new Thread(runnable);
		
		th.setName(
				new StringBuilder(threadName)
				.append("-")
				.append(threadNum.getAndIncrement())
				.toString()
				);

		if (th.isDaemon()) th.setDaemon(false);
		if (th.getPriority() != Thread.NORM_PRIORITY) th.setPriority(Thread.NORM_PRIORITY);
		
		return th;
	}

}
