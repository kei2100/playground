package playground.javase7.nio2.asyncchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AsyncEchoServer {
	
	private int port = 18080;
	
	private int backlogSize;

	private AsynchronousServerSocketChannel serverSocketChannel;
	
	public AsyncEchoServer(AsynchronousServerSocketChannel serverSocketChannel) {
		this.serverSocketChannel = serverSocketChannel;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
			
			new AsyncEchoServer(serverSocketChannel).start();
			
			while(true) {
				Thread.sleep(1000L);
			}
		}
	}
	
	private void start() throws IOException {
		System.out.println(String.format("[%s] Server started. ", Thread.currentThread().getName()));
		
		serverSocketChannel.bind(new InetSocketAddress(port), backlogSize);
		serverSocketChannel.accept(serverSocketChannel, new AsyncEchoAcceptor());
	}
}
