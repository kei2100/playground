package playground.concurrency_utilities;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockSample {

	public static void main(String[] args) {
		State state = new State();
		WorkersLock workersLock = new WorkersLock();
		
		Thread [] threads = new Thread[] {
			new Thread(new Worker(state, workersLock)),	
			new Thread(new Worker(state, workersLock)),	
			new Thread(new Worker(state, workersLock)),	
			new Thread(new Worker(state, workersLock)),	
			new Thread(new Worker(state, workersLock))	
		};
		
		for (Thread thread : threads) {
			thread.start();
		}
	}
	
	private static class State {
		public Integer instanceField = 0;
		public static Integer staticField = 0;
	}
	
	private static class WorkersLock {
		public final Lock lock = new ReentrantLock();
	}
	
	private static class Worker implements Runnable {
		private State state;
		private WorkersLock workersLock;
		
		public Worker(State state, WorkersLock workersLock) {
			this.state = state;
			this.workersLock = workersLock;
		}

		public void run() {
			workersLock.lock.lock();
			try {
				state.instanceField ++;
				State.staticField ++;
				
				System.out.println("instanceField:" + state.instanceField);
				System.out.println("staticField  :" + State.staticField);
			} finally {
				workersLock.lock.unlock();
			}
		}		
	}
}
