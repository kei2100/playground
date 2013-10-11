package playground.jetty_httpclient;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpFields;

public class AsyncRequest {
	
	public static void main(String[] args) throws Exception {
		System.out.println("==============================");
		System.out.println("checkListenersCallTiming");
		System.out.println("==============================");
		checkListenersCallTiming();
		
		System.out.println("==============================");
		System.out.println("checkConcurrencyOfListenersWhenBlocking");
		System.out.println("==============================");
		checkConcurrencyOfListenersWhenBlocking();
	}

	private static void checkConcurrencyOfListenersWhenBlocking() throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.setMaxConnectionsPerDestination(2);
		httpClient.start();
		
		final CountDownLatch latch = new CountDownLatch(2);
		try {
			Request request1 = httpClient.newRequest("http://www.example.com");
			Request request2 = httpClient.newRequest("http://www.example.com");
			
			request1.onResponseHeaders(new Response.HeadersListener() {
				@Override
				public void onHeaders(Response arg0) {
					System.out.println("request1 onHeaders:" + System.currentTimeMillis());
					sleep(3, TimeUnit.SECONDS);
				}
			}).send(new Response.CompleteListener() {
				@Override
				public void onComplete(Result arg0) {
					System.out.println("request1 onComplete:" + System.currentTimeMillis());
					latch.countDown();
				}
			});
			
			request2.onResponseHeaders(new Response.HeadersListener() {
				@Override
				public void onHeaders(Response arg0) {
					System.out.println("request2 onHeaders:" + System.currentTimeMillis());
					sleep(1, TimeUnit.SECONDS);
				}
			}).send(new Response.CompleteListener() {
				@Override
				public void onComplete(Result arg0) {
					System.out.println("request2 onComplete:" + System.currentTimeMillis());
					latch.countDown();
				}
			});
			
			latch.await(10, TimeUnit.SECONDS);
			
		} finally {
			httpClient.stop();
		}
	}

	private static void sleep(long wait, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(wait));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	private static void checkListenersCallTiming() throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.setResponseBufferSize(1024);
		httpClient.start();
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		try {
			httpClient.newRequest("http://www.example.com")
				.onResponseBegin(new Response.BeginListener() {
					@Override
					public void onBegin(Response response) {
						System.out.println("onBegin ----------");
						printResponse(response);
					}
				}).onResponseHeaders(new Response.HeadersListener() {
					@Override
					public void onHeaders(Response response) {
						System.out.println("onHeaders ----------");
						printResponse(response);
					}
				}).onResponseContent(new Response.ContentListener() {
					@Override
					public void onContent(Response response, ByteBuffer bytebuffer) {
						System.out.println("onContent ----------");
					}
				}).send(new Response.CompleteListener() {
					@Override
					public void onComplete(Result arg0) {
						System.out.println("onContent ----------");
						latch.countDown();
					}
				});
			
			System.out.println("await latch ----------");
			latch.await(3, TimeUnit.SECONDS);
			System.out.println("fin ----------");
			
		} finally {
			httpClient.stop();
		}
	}

	private static  void printResponse(Response response) {
		if (response == null) {
			System.out.println("response is null");
			return;
		}
		
		int status = response.getStatus();
		HttpFields headers = response.getHeaders();
		
		System.out.println("status:" + status);
		System.out.println("headers:" + (headers.size() == 0 ? "empty" : headers));
	}
}
