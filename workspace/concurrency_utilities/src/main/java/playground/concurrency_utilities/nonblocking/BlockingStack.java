package playground.concurrency_utilities.nonblocking;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingStack<E> implements Stack<E> {

	private Node<E> top;
	private Lock lock = new ReentrantLock();

	public void push(E item) {
		if (item == null) throw new NullPointerException();
		
		Node<E> newHead = new Node<>(item);
		try {
			lock.lock();
			
			newHead.next = top;
			top = newHead;
		} finally {
			lock.unlock();
		}
	}

	public E pop() {
		try {
			lock.lock();
			
			if (top == null) throw new NoSuchElementException();
			
			Node<E> oldHead = top;
			Node<E> newHead = oldHead.next;
			top = newHead;

			return oldHead.item;
		} finally {
			lock.unlock();
		}
	}

	private static class Node<E> {
		private final E item;
		private Node<E> next;

		public Node(E item) {
			this.item = item;
		}
	}
}
