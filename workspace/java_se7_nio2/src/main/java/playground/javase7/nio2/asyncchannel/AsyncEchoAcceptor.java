package playground.javase7.nio2.asyncchannel;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncEchoAcceptor
implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

	private int bufferSize = 1024;
	
	@Override
	public void completed(AsynchronousSocketChannel socketChannel,
						  AsynchronousServerSocketChannel serverSocketChannel) {
		
		System.out.println(String.format("[%s] Server socket channel accepted. ", Thread.currentThread().getName()));
		
		// 最初に次の接続へのacceptの準備をしておく。
		serverSocketChannel.accept(serverSocketChannel, this);		
		
		System.out.println(String.format("socket channel hashCode:%s", socketChannel.hashCode()));
		
		// socket read
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		socketChannel.read(buffer, socketChannel, new AsyncEchoReader(buffer));
	}

	@Override
	public void failed(Throwable th, AsynchronousServerSocketChannel serverSocketChannel) {
		th.printStackTrace();
	}
}
