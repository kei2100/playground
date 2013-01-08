package playground.javase7.nio2.asyncchannel;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncEchoWriter implements 
		CompletionHandler<Integer, AsynchronousSocketChannel> {

	private ByteBuffer buffer;

	public AsyncEchoWriter(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void completed(Integer result, AsynchronousSocketChannel socketChannel) {
		System.out.println(String.format("[%s] socket channel write completed. ", Thread.currentThread().getName()));
		buffer.clear();
		socketChannel.read(buffer, socketChannel, new AsyncEchoReader(buffer));
	}

	@Override
	public void failed(Throwable th, AsynchronousSocketChannel attachment) {
		th.printStackTrace();
	}
}
