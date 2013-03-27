package playground.pool;

public interface PooledObjectFactory<T> {

	/**
	 * 
	 * @return 生成したオブジェクト。何らかの理由によりオブジェクトが生成できなかった場合は例外
	 * */
	T createInstance() throws Exception;
}
