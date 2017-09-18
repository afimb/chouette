package mobi.chouette.exchange.gtfs.exporter;

import mobi.chouette.model.StopArea;

public class StopAreaUtil {

	public static StopArea getTopLevelStopArea(StopArea sa) {
		StopArea nextParent = sa;
		while(nextParent.getParent() != null) {
			nextParent = nextParent.getParent();
		}
	
		return nextParent;
	}

}
