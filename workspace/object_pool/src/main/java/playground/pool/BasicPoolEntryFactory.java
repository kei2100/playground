package playground.pool;

public class BasicPoolEntryFactory<T> implements PoolEntryFactory<T> {

	private final PooledObjectFactory<T> objectFactory;
	private final PooledObjectValidator<T> validator;
	
	public BasicPoolEntryFactory(PooledObjectFactory<T> objectFactory, PooledObjectValidator<T> validator) {
		this.objectFactory = objectFactory;
		this.validator = validator;
	}
	
	@Override
	public PoolEntry<T> createPoolEntry() throws CreatePoolEntryException {
		try {
			T object = objectFactory.createInstance();
			return new BasicPoolEntry<T>(object, validator);
		} catch (Exception e) {
			throw new CreatePoolEntryException(e);
		}
	}
}
