package playground.javase7.coin;

public class ResourceAutoClose {
	
	public static void main(String[] args) throws Exception {
		
		try (
			SomeResource resource1 = new SomeResource("SomeResource1");
			SomeResource resource2 = new SomeResource("SomeResource2");
		) {
			resource1.printname();
			resource2.printname();
			
		} finally {
			System.out.println("done");
		}
	}	
	
	// AutoClosableをimplementsしているクラスは、
	// リソースを含むtry構文で、自動クローズの対象となる。
	private static class SomeResource implements AutoCloseable {
		
		private String name = "unknown";

		public SomeResource(String name) {
			this.name = name;
		}
		
		@Override
		public void close() throws Exception {
			System.out.println(name + " Closed!!");
			throw new Exception(name + " throws Exception!!");
			// 複数リソースが存在していて、途中でクローズ処理で例外が発生しても、
			// 最後のリソースまで、クローズは呼び切るようである。
		}
		
		public void printname() {
			System.out.println("Resource name is " + name);
		}
	}
}


