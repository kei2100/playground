package playground.pool;

public class ConfigException extends RuntimeException{
	
	private static final long serialVersionUID = 6467024091536458019L;

	public ConfigException(String message) {
		super(message);
	}
	
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
