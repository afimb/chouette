package fr.certu.chouette.exchange.xml.neptune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Route;

public class ModelAssembler {
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private List<Route> routes;
	
	private Map<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>> populatedDictionaries = new HashMap<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>>();
	
	private Map<String, Line> linesDictionary = new HashMap<String, Line>();
	private Map<String, Route> routesDictionary = new HashMap<String, Route>();
	
	
	public void connect(){
		populateDictionaries();
		connectLines();
		connectRoutes();
	}
	
	private void populateDictionaries(){
		populateDictionnary(lines, linesDictionary);
		populateDictionnary(routes, routesDictionary);
	}
	
	private <T extends NeptuneIdentifiedObject> void populateDictionnary(List<T> list, Map<String,T> dictionnary){
		for(T item : list){
			if(item != null && item.getObjectId() != null){
				dictionnary.put(item.getObjectId(), item);
			}
		}
		populatedDictionaries.put(list.get(0).getClass(), dictionnary);
	}
	
	private void connectLines(){
		for(Line line : lines){
			//line.setCompany(company);
			//line.setPtNetwork(ptNetwork);
			
			line.setRoutes(getObjectsFromIds(line.getRouteIds(), Route.class));
		}
	}
	
	private void connectRoutes(){
		for(Route route : routes){
			//route.setJourneyPatterns(journeyPatterns);
			//route.setPtLinks(ptLinks);
		}
	}
	
	private <T extends NeptuneIdentifiedObject> List<T> getObjectsFromIds(List<String> ids, Class<T> dictionaryClass){
		Map<String, ? extends NeptuneIdentifiedObject> dic =  populatedDictionaries.get(dictionaryClass);
		List<T> objects = new ArrayList<T>();
		
		for(String id : ids){
			objects.add((T)dic.get(id));
		}
		
		return objects;
	}
}
