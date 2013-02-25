package playground.concurrency_utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierSample {
	
	public static void main(String[] args) throws Exception {		
		int workerSize = 10;
		CyclicBarrier barrier = new CyclicBarrier(workerSize);
		
		ExecutorService es = Executors.newFixedThreadPool(workerSize);
		List<Future<String>> futures = es.invokeAll(createWorkers(workerSize, barrier));
		
		System.out.println("----- スレッド終了順を出力:1回目 -----");
		for (Future<String> future : futures) {
			System.out.println(future.get());
		}
		
		barrier.reset();
		futures = es.invokeAll(createWorkers(workerSize, barrier));

		System.out.println("----- スレッド終了順を出力:2回目 -----");
		for (Future<String> future : futures) {
			System.out.println(future.get());
		}
	}
	
	/*
	 * workerを作成
	 */
	private static List<Callable<String>> createWorkers(int workerSize, CyclicBarrier barrier) {
		List<Callable<String>> workers = new ArrayList<Callable<String>>(workerSize);
		for (int i = 0; i < workerSize; i++) {
			workers.add(new Worker(barrier));
		}
		return workers;
	}

	
	public static class Worker implements Callable<String> {
		private static final int FIBONACCI_COUNT = 50;
		
		private CyclicBarrier barrier;
		public Worker(CyclicBarrier barrier) {
			this.barrier = barrier;
		}
		@Override
		public String call() throws Exception {
			
			// フィボナッチ数列を計算する
			getFibonacci(FIBONACCI_COUNT);
			
			try {
				// すべてのスレッドがバリアに到着後、到着順を取得。
				int arrival = barrier.getParties() - barrier.await();
				String workerName = Thread.currentThread().getName();
				
				return "workerName:" + workerName + ", arrival:" + arrival;
			} catch (InterruptedException e) {
				throw e;
			} catch (BrokenBarrierException e) {
				throw e;
			}
		}
		
		/*
		 * フィボナッチ数列を返却
		 */
		private static String getFibonacci(int fibonacciCount) throws InterruptedException {
			StringBuilder result = new StringBuilder();
			long operand_1 = 0;
			long operand_2 = 1;
			
			result.append(operand_1);
			result.append(",").append(operand_2);
			
			for (int i = 0; i < fibonacciCount; i++) {
				long sum = operand_1 + operand_2;
				result.append(",").append(sum);
				
				operand_1 = operand_2;
				operand_2 = sum;
				
				TimeUnit.MILLISECONDS.sleep((int) (Math.random() * 10 + 1));
			}
			return result.toString();
		}
	}
	
}
