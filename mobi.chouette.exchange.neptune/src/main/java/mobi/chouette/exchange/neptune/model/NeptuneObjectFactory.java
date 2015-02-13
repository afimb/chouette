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
	private Map<String, PTLink> ptLink = new HashMap<String, PTLink>();

	@Getter
	@Setter
	private Map<Route, List<PTLink>> ptLinksOnRoute = new HashMap<Route, List<PTLink>>();

	public PTLink getPTLink(String objectId) {
		PTLink result = ptLink.get(objectId);
		if (result == null) {
			result = new PTLink();
			result.setObjectId(objectId);
			ptLink.put(objectId, result);
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
	
	public void clear(){
		ptLink.clear();
		ptLinksOnRoute.clear();
	}
}
