package playground.concurrency_utilities.nonblocking;

public interface Stack<E> {

	void push(E item);
	
	E pop();
}