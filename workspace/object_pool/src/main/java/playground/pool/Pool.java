package playground.pool;

import java.util.concurrent.TimeoutException;

public interface Pool<T> {

	PoolConfig getPoolConfig();

	PoolEntry<T> borrowEntry() 
			throws InterruptedException, TimeoutException, CreatePoolEntryException;
	
	// TODO interfaceとして必要か？
	// pollするという表現のほうがわかりやすいかも。↓のため
	// 一番長くアイドルしているエントリを最初に返す、インターフェース契約
	PoolEntry<T> tryBorrowIdleEntry();

	void returnEntry(PoolEntry<T> entry) 
			throws NullPointerException;
	
}