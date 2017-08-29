package mobi.chouette.exchange.stopplace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import mobi.chouette.model.StopArea;

public class StopAreaUpdateContext {

	@Getter
	private Set<String> inactiveStopAreaIds = new HashSet<>();
	@Getter
	private Set<StopArea> activeStopAreas = new HashSet<>();
	@Getter
	private Map<String, String> mergedQuays = new HashMap<>();


	public int getChangedStopCount() {
		return getActiveStopAreas().size() + getInactiveStopAreaIds().size();
	}

}
