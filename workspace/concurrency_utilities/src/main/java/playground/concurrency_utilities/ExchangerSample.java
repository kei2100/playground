package playground.concurrency_utilities;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ExchangerSample {
	
	/*
	 * バッファ、的な。
	 */
	public static class Buffer {
		private final byte[] bytes;
		private int position;
		private int capacity;
		
		public Buffer(int capacity) {
			this.capacity = capacity;
			bytes = new byte[this.capacity];
			position = 0;
		}
		
		public void put(byte b) {
			bytes[position] = b;
			position++;
		}
		
		public byte get() {
			byte b = bytes[position];
			position++;
			return b;
		}
		
		public int remaining() {
			return capacity - position;
		}
		
		public void setPosition(int position) {
			this.position = position;
		}
	}
	
	/*
	 * Bufferを1詰めするWorker
	 */
	public static class FillingWorker implements Runnable{
		private Buffer buffer;
		private Exchanger<Buffer> exchanger;
		
		public FillingWorker(Buffer buffer, Exchanger<Buffer> exchanger) {
			this.buffer = buffer;
			this.exchanger = exchanger;
		}
		
		@Override
		public void run() {
			while (true) {
				// バッファ状態を出力
				buffer.setPosition(0);
				StringBuilder sb = new StringBuilder();
				while (buffer.remaining() != 0) {
					sb.append("[" + buffer.get() + "]");
				}
				System.out.println("FillingWorker before filling.	" + sb.toString());
				
				// バッファを1詰めする
				buffer.setPosition(0);
				while (buffer.remaining() != 0) {
					buffer.put((byte) 1);
				}

				try {
					// 1詰めされたBufferをexchangerに渡す。
					// 0詰めされたBufferをexchangerから取得できるまで待つ。
					buffer = exchanger.exchange(buffer);
				} catch (InterruptedException e) {
					break;  // インタラプトされたら終了
				}
			}
		}
	}
	
	/*
	 * Bufferを0詰めするWorker
	 */
	public static class EmptyingWorker implements Runnable{
		private Buffer buffer;
		private Exchanger<Buffer> exchanger;
		
		public EmptyingWorker(Buffer buffer, Exchanger<Buffer> exchanger) {
			this.buffer = buffer;
			this.exchanger = exchanger;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					// 0詰めされたBufferをexchangerに渡す。
					// 1詰めされたBufferをexchangerから取得できるまで待つ。
					buffer = exchanger.exchange(buffer);
				} catch (InterruptedException e) {
					break;  // インタラプトされたら終了
				}
				// バッファ状態を出力
				buffer.setPosition(0);
				StringBuilder sb = new StringBuilder();
				while (buffer.remaining() != 0) {
					sb.append("[" + buffer.get() + "]");
				}
				System.out.println("EmptyingWorker before emptying.	" + sb.toString());

				// バッファを0詰めする
				buffer.setPosition(0);
				while (buffer.remaining() != 0) {
					buffer.put((byte) 0);
				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Buffer buffer1 = new Buffer(10);
		Buffer buffer2 = new Buffer(10);
		Exchanger<Buffer> exchanger = new Exchanger<Buffer>();
		
		Thread fillingThread = 
				new Thread(new FillingWorker(buffer1, exchanger));
		
		Thread emptyingThread = 
				new Thread(new EmptyingWorker(buffer2, exchanger));
		
		fillingThread.start();
		emptyingThread.start();
		
		TimeUnit.MILLISECONDS.sleep(200);
		
		fillingThread.interrupt();
		emptyingThread.interrupt();
	}
}
