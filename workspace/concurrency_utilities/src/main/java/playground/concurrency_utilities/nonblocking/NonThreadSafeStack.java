package playground.concurrency_utilities.nonblocking;

import java.util.NoSuchElementException;

public class NonThreadSafeStack<E> implements Stack<E> {

	private Node<E> top;

	public void push(E item) {
		if (item == null) throw new NullPointerException();
		
		Node<E> newHead = new Node<>(item);
		newHead.next = top;
		top = newHead;
	}

	public E pop() {
		if (top == null) throw new NoSuchElementException();
		
		Node<E> oldHead = top;
		Node<E> newHead = oldHead.next;
		top = newHead;

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
