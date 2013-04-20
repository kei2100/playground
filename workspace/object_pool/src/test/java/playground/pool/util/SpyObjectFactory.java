package playground.pool.util;

import playground.pool.PooledObjectFactory;

public class SpyObjectFactory implements PooledObjectFactory<SpyObject> {
	@Override
	public SpyObject createInstance() throws Exception {
		return new SpyObject();
	}
}
