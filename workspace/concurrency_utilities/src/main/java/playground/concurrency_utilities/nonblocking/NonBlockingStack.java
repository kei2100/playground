package playground.concurrency_utilities.nonblocking;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingStack<E> implements Stack<E> {
	private AtomicReference<Node<E>> top = new AtomicReference<>();
	
	@Override
	public void push(E item) {
		if (item == null) throw new NullPointerException();
		
		Node<E> newHead = new Node<>(item);
		Node<E> oldHead = null;
		do {
			oldHead = top.get();
			newHead.next = oldHead;
		} while (!top.compareAndSet(oldHead, newHead)); 
	}

	@Override
	public E pop() {
		Node<E> oldHead = null;
		Node<E> newHead = null;
		do {
			oldHead = top.get();
			
			if (oldHead == null) throw new NoSuchElementException();
			
			newHead = oldHead.next;
		} while (!top.compareAndSet(oldHead, newHead));
		
		return oldHead.item;
	}

	private static class Node<E> {
		private final E item;
		private Node<E> next;

		public Node(E item) {
			this.item = item;
		}
	}
}
