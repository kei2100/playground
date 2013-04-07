package playground.pool.util;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PoolLoggerMarkerFactory {
	private static final Marker marker =
			MarkerFactory.getMarker(PoolLoggerMarkerFactory.class.getName());
	
	public static Marker getMarker() {
		return marker;
	}
}
