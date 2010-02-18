package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class SubLine extends TridentObject {
	
	private String			subLineName;								// 1
	private String			lineName;									// 1
	// TODO : XXX Vérfier lineName par rapport à line
	private Registration	registration;								// 0..1
	private List<String>	routeIds		= new ArrayList<String>();	// 1..w
	private List<Route>		routes			= new ArrayList<Route>();	// 1..w
	private String			lineId;										// 1
	private Line			line;										// 1
	private String			comment;									// 0..1
	
	public void setSubLineName(String subLineName) {
		this.subLineName = subLineName;
	}
	
	public String getSubLineName() {
		return subLineName;
	}
	
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	
	public String getLineName() {
		return lineName;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setRouteIds(List<String> routeIds) {
		this.routeIds = routeIds;
	}
	
	public List<String> getRouteIds() {
		return routeIds;
	}
	
	public void addRouteId(String routeId) {
		routeIds.add(routeId);
	}
	
	public void removeRouteId(String routeId) {
		routeIds.remove(routeId);
	}
	
	public void removeRouteId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRouteIdsCount()))
			throw new IndexOutOfBoundsException();
		routeIds.remove(i);
	}
	
	public String getRouteId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRouteIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)routeIds.get(i);
	}
	
	public int getRouteIdsCount() {
		if (routeIds == null)
			return 0;
		return routeIds.size();
	}
	
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
	public List<Route> getRoutes() {
		return routes;
	}
	
	public void addRoute(Route route) {
		routes.add(route);
	}
	
	public void removeRoute(Route route) {
		routes.remove(route);
	}
	
	public void removeRoute(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRoutesCount()))
			throw new IndexOutOfBoundsException();
		routes.remove(i);
	}
	
	public Route getRoute(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRoutesCount()))
			throw new IndexOutOfBoundsException();
		return (Route)routes.get(i);
	}
	
	public int getRoutesCount() {
		if (routes == null)
			return 0;
		return routes.size();
	}
	
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	
	public String getLineId() {
		return lineId;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
}
