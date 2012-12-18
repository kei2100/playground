package playground.javase7.coin;

/**
 * 例外のマルチキャッチ
 * */
public class MultipuleCatch {
	public static void main(String[] args) {
		try {
			Object instance = Class.forName("java.lang.String").newInstance();
			System.out.println(instance.getClass());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
