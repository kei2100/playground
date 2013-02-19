package playground.concurrency_utilities;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchSample {

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		int thredsCount = 100;

		CountDownLatch startGate = new CountDownLatch(1);
		CountDownLatch endGate = new CountDownLatch(thredsCount );
		
		for (int i = 0; i < thredsCount; i++) {
			Thread thread = new Thread(new Worker(startGate, endGate));
			thread.start();
		}
		
		// すべてのWorkerスレッドの開始〜終了までの時間をCountdownLatchで同期する。
		long startedAt = System.currentTimeMillis();
		
		System.out.println("Start threads.");
		startGate.countDown();
		
		endGate.await();
		System.out.println("All threds finish");
		
		long elapsed = System.currentTimeMillis() - startedAt;
		System.out.println("elapsed : " + elapsed);
	}
	
	private static class Worker implements Runnable {
		final private CountDownLatch startGate;
		final private CountDownLatch endGate;

		public Worker(CountDownLatch startGate, CountDownLatch endGate) {
			this.startGate = startGate;
			this.endGate = endGate;
		}

		public void run() {
			try {
				// startGateが開くまで待つ。
				startGate.await();
				
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException ignored) {
			}
			
			System.out.println("finish : " + Thread.currentThread().getName());
			
			// endGateに終了を告げる。
			endGate.countDown();
		}
	}
}
