package playground.concurrency_utilities.nonblocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StackTest {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		final Stack<Integer> stack = new NonThreadSafeStack<>();
		final Stack<Integer> stack = new NonBlockingStack<>();
		
		List<Callable<Void>> pushTasks = createPushTasks(stack);
		List<Callable<Integer>> popTasks = createPopTasks(stack);
		 
//		ExecutorService executor = Executors.newFixedThreadPool(1);
		ExecutorService executor = Executors.newFixedThreadPool(10);
		try {
			executor.invokeAll(pushTasks);
			for (Future<Integer> result : executor.invokeAll(popTasks)) {
				System.out.println(result.get());;
			}
		} finally {
			executor.shutdown();
		}
	}

	private static List<Callable<Void>> createPushTasks(final Stack<Integer> stack) {
		List<Callable<Void>> pushTasks = new ArrayList<>();
		for (int i = 1; i < 11; i++) {
			final int forPush = i;
			pushTasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					stack.push(forPush);
					return null;
				}
			});
		}
		return pushTasks;
	}
	private static List<Callable<Integer>> createPopTasks(final Stack<Integer> stack) {
		List<Callable<Integer>> popTasks = new ArrayList<>();
		for (int i = 1; i < 11; i++) {
			Callable<Integer> popTask = new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return stack.pop();
				}
			};
			popTasks.add(popTask);
		}
		return popTasks;
	}
}
