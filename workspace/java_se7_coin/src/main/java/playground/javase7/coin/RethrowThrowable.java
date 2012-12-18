package playground.javase7.coin;

/**
 * 例外の再スロー
 * */
public class RethrowThrowable {
	
	public static void main(String[] args) {
		String [] arr = new String [] {"one"};
		
		try {
			System.out.println(arr[1]);
		} catch (final Throwable e) {
			throw e;
			// JDK 6では、メソッドにthrows Throwable節が必要だが、
			// JDK 7では、ランタイム例外に限り(たぶん)、Throwableでキャッチして、
			// それを再代入などしていなければ、throws節無しでコンパイル通る。
			// 
			// ) だったらRuntimeExceptionでキャッチすればいいような気もするし、
			// ) Exceptionで全部キャッチしちゃうデザインってどうなのって感じだが・・
		}
	}	
}
