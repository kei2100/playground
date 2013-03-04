package playground.pool;

public class CreatePooledObjectException extends Exception{

	private static final long serialVersionUID = 5419175944474079329L;

	public CreatePooledObjectException() {
		super();
	}

	public CreatePooledObjectException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public CreatePooledObjectException(String s) {
		super(s);
	}

	public CreatePooledObjectException(Throwable throwable) {
		super(throwable);
	}
}
