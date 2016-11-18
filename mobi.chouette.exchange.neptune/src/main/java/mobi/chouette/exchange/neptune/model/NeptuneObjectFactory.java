package mobi.chouette.exchange.neptune.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.Route;

public class NeptuneObjectFactory {

	@Getter
	@Setter
	private Map<ChouetteId, AreaCentroid> areaCentroid = new HashMap<>();

	@Getter
	@Setter
	private Map<ChouetteId, PTLink> ptLink = new HashMap<>();

	@Getter
	@Setter
	private Map<Route, List<PTLink>> ptLinksOnRoute = new HashMap<>();

	@Getter
	@Setter
	private Map<ChouetteId, TimeSlot> timeSlots = new HashMap<>();

	public PTLink getPTLink(ChouetteId chouetteId) {
		PTLink result = ptLink.get(chouetteId);
		if (result == null) {
			result = new PTLink();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			ptLink.put(chouetteId, result);
		}
		return result;
	}

	public AreaCentroid getAreaCentroid(ChouetteId chouetteId) {
		AreaCentroid result = areaCentroid.get(chouetteId);
		if (result == null) {
			result = new AreaCentroid();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			areaCentroid.put(chouetteId, result);
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
	
	public TimeSlot getTimeSlot(ChouetteId chouetteId) {
		TimeSlot timeSlot = timeSlots.get(chouetteId);
		if (timeSlot == null) {
			timeSlot = new TimeSlot();
			timeSlot.setChouetteId(new ChouetteId());
			timeSlot.getChouetteId().setObjectId(chouetteId.getObjectId());
			timeSlots.put(chouetteId,  timeSlot);
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
