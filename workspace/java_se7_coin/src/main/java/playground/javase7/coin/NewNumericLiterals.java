package playground.javase7.coin;

/**
 * 新しい数値リテラル表現
 * */
public class NewNumericLiterals {
	public static void main(String[] args) {
		// 0bをプレフィクスにした byteリテラル
		byte b = 0b00000011;
		System.out.println(b);
		
		// underscore区切りの数値表現
		System.out.println(1_000_000);
	}
}
