package playground.misc;

import java.util.Arrays;

public class LineSeparatorCheck {
	
	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;	
	
	public static void main(String[] args) {		
		byte[] bytes = System.lineSeparator().getBytes();
		
		if (bytes.length == 1) {
			switch (bytes[0]) {
			case CR:
				System.out.println("line separator is CR");
				break;
			case LF:
				System.out.println("line separator is LF");
				break;
			default:
				throw new IllegalArgumentException(new String(bytes));
			}
		} else if (bytes.length == 2) {
			byte[] CRLF = new byte[] {CR, LF};
			
			if (Arrays.equals(bytes, CRLF)) {
				System.out.println("line separator is CRLF");
			} else {
				throw new IllegalArgumentException(new String(bytes));				
			}
		} else {
			throw new IllegalArgumentException("bytes length is " + bytes.length);
		}
	}
}
