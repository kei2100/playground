package playground.javase7.nio2.asyncchannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;

public class AsyncEchoReader implements
		CompletionHandler<Integer, AsynchronousSocketChannel> {
	
	private ByteBuffer buffer;
	
	private static final byte[] QUIT = "quit".getBytes();
	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;	
	
	public AsyncEchoReader(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void completed(Integer result, AsynchronousSocketChannel socketChannel) {
		if (result == null || result < 0) {
			try {
				socketChannel.close();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
		try {
			if (isRecieveQuitSingnal(buffer)) {
				System.out.println("socket channel catch quit signal.");
				socketChannel.close();
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(String.format("[%s] socket channel read completed. ", Thread.currentThread().getName()));
		buffer.flip();
		socketChannel.write(buffer, socketChannel, new AsyncEchoWriter(buffer));
	}

	@Override
	public void failed(Throwable th, AsynchronousSocketChannel attachment) {
		th.printStackTrace();
	}
	
	// quit¥n , quit¥r , quit¥r¥n を受信した場合にtrueを返す
	private boolean isRecieveQuitSingnal(ByteBuffer buffer) throws IOException {
		int crntPosition = buffer.position();
		if (crntPosition != 5 && crntPosition != 6) {
			return false;
		}
		
		// 受信バッファを頭から読み込む
		buffer.position(0);
		
		byte[] first4Bytes = new byte[4];
		buffer.get(first4Bytes);

		byte[] remainingBytes = new byte[crntPosition - 4];
		buffer.get(remainingBytes);
		
		// バッファのポジションを元に戻しておく
		buffer.position(crntPosition);
		
		if (!Arrays.equals(first4Bytes, QUIT)) {
			return false;
		}
		
		if (remainingBytes.length == 1) {
			if (remainingBytes[0] == LF || remainingBytes[0] == CR) {
				return true;
			} else {
				return false;
			}
		} else {
			if (Arrays.equals(remainingBytes, new byte[] {CR, LF})) {
				return true;
			} else {
				return false;
			}
		}
	}	
}
