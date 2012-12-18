package playground.javase7.coin;

import java.util.Random;

/**
 * switchの比較対象にStringが使用可能に
 * */
public class StringInSwitch {
	public static void main(String[] args) {
		String [] dataStores = new String [] {"mysql", "mongo", "cassandra"}; 
				
		int randomIndex = new Random().nextInt(dataStores.length);
		String dataStore = dataStores[randomIndex];
		
		switch (dataStore) {
		case "mysql":
			System.out.println("mysql is RDBMS");
			break;
		case "mongo":
			System.out.println("mongo is document-oriented datastore");
			break;
		case "cassandra":
			System.out.println("cassandra is column-oriented datastore");
			break;
		default:
			System.out.println("unmatch");
			break;
		}
	}
}
