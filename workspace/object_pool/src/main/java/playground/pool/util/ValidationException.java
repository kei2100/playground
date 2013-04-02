package playground.pool.util;

public class ValidationException extends RuntimeException{
	
	private static final long serialVersionUID = 6467024091536458019L;

	public ValidationException(String message) {
		super(message);
	}
	
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
