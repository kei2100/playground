package playground.concurrency_utilities.nonblocking;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class NonBlockingStackWithFieldUpdater<E> implements Stack<E> {
	// 原始的な更新をしたい値の型はAtomicXxxxでなくて良い。volatileは必須
	private volatile Node<E> top;

	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<NonBlockingStackWithFieldUpdater, Node> updater = 
			AtomicReferenceFieldUpdater.newUpdater(NonBlockingStackWithFieldUpdater.class, Node.class, "top");
	
	@Override
	public void push(E item) {
		if (item == null) throw new NullPointerException();
		
		Node<E> newHead = new Node<>(item);
		Node<E> oldHead = null;
		do {
			oldHead = top;
			newHead.next = oldHead;
		} while (!updater.compareAndSet(this, oldHead, newHead)); 
	}

	@Override
	public E pop() {
		Node<E> oldHead = null;
		Node<E> newHead = null;
		do {
			oldHead = top;
			
			if (oldHead == null) throw new NoSuchElementException();
			
			newHead = oldHead.next;
		} while (!updater.compareAndSet(this, oldHead, newHead));
		
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
