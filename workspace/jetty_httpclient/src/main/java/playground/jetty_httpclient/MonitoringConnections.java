package playground.jetty_httpclient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Destination;
import org.eclipse.jetty.client.http.HttpDestinationOverHTTP;

public class MonitoringConnections {
	
	private static volatile HttpClient httpClient;
	
	public static void main(String[] args) throws Exception {

		// Create Executor for sending http request.
		int httpRequestsConcurrency = 5;
		ExecutorService httpRequestTaskExecutor = Executors.newFixedThreadPool(httpRequestsConcurrency);
	
		// Create Executor for monitoring connection pool state.
		ScheduledExecutorService monitorTaskExecutor = Executors.newScheduledThreadPool(1);
		monitorTaskExecutor.scheduleWithFixedDelay(new MonitorTask(), 1, 1, TimeUnit.SECONDS);

		try {
			httpClient = new HttpClient();
			httpClient.start();

			// Sending request and monitoring connection pool state while 10 seconds. 
			long after10sec = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);		
			while (after10sec > System.currentTimeMillis()) {
				httpRequestTaskExecutor.submit(new HttpRequestTask());
				Thread.sleep(1000 / httpRequestsConcurrency);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpRequestTaskExecutor.shutdownNow();
			monitorTaskExecutor.shutdown();
			httpClient.stop();
		}
	}
	
	private static class HttpRequestTask implements Runnable {
		@Override
		public void run() {
			if (httpClient == null) return;
			if (!httpClient.isStarted()) return;

			try {
				System.out.println("Sending.");
				httpClient.GET("http://www.google.com");
				httpClient.GET("http://www.example.com");
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class MonitorTask implements Runnable {
		@Override
		public void run() {
			if (httpClient == null) return;
			if (!httpClient.isStarted()) return;
			
			// Print active and idle connections per destination
			for (Destination destination : httpClient.getDestinations()) {
				if (!(destination instanceof HttpDestinationOverHTTP)) continue;
				
				HttpDestinationOverHTTP httpDestination = (HttpDestinationOverHTTP) destination;
				
				// Get destination info.
				String scheme = httpDestination.getScheme();
				String host = httpDestination.getHost();
				int port = httpDestination.getPort();
				
				// Get connection pool info.
				int activeConnections = httpDestination.getHttpConnectionPool().getActiveConnections().size();
				int idleConnections = httpDestination.getHttpConnectionPool().getIdleConnections().size();
				
				StringBuilder sb = new StringBuilder()
					.append("destination:").append(scheme).append("://").append(host).append(":").append(port).append("\t")
					.append("activeConnections:").append(activeConnections).append("\t")
					.append("idleConnections:").append(idleConnections);
				
				System.out.println(sb.toString());
			}
		}
	}
}
