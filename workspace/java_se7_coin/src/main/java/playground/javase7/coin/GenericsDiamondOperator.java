package playground.javase7.coin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ジェネリクスのダイヤモンド記法
 * */
public class GenericsDiamondOperator {
	public static void main(String[] args) {
		Map<String, List<String>> map = new HashMap<>();
		System.out.println(map.isEmpty());
	}
}
