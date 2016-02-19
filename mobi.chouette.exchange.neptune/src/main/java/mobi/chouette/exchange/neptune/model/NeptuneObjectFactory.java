package mobi.chouette.exchange.neptune.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.Route;

public class NeptuneObjectFactory {

	@Getter
	@Setter
	private Map<String, AreaCentroid> areaCentroid = new HashMap<>();

	@Getter
	@Setter
	private Map<String, PTLink> ptLink = new HashMap<>();

	@Getter
	@Setter
	private Map<Route, List<PTLink>> ptLinksOnRoute = new HashMap<>();

	@Getter
	@Setter
	private Map<String, TimeSlot> timeSlots = new HashMap<>();

	public PTLink getPTLink(String objectId) {
		PTLink result = ptLink.get(objectId);
		if (result == null) {
			result = new PTLink();
			result.setObjectId(objectId);
			ptLink.put(objectId, result);
		}
		return result;
	}

	public AreaCentroid getAreaCentroid(String objectId) {
		AreaCentroid result = areaCentroid.get(objectId);
		if (result == null) {
			result = new AreaCentroid();
			result.setObjectId(objectId);
			areaCentroid.put(objectId, result);
		}
		return result;
	}

	
	public List<PTLink> getPTLinksOnRoute(Route route) {
		List<PTLink> result = ptLinksOnRoute.get(route);
		if(result == null){
			result = new ArrayList<PTLink>();
			ptLinksOnRoute.put(route, result);
		}
		return result;
	}
	
	public TimeSlot getTimeSlot(String objectId) {
		TimeSlot timeSlot = timeSlots.get(objectId);
		if (timeSlot == null) {
			timeSlot = new TimeSlot();
			timeSlot.setObjectId(objectId);
			timeSlots.put(objectId,  timeSlot);
		}
		return timeSlot;
	}
	
	public void clear(){
		ptLink.clear();
		ptLinksOnRoute.clear();
		timeSlots.clear();
	}
	
	public void dispose()
	{
	     clear();
	     areaCentroid.clear();
	}
	
}
