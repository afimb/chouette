package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class Line extends TridentObject {
	
	private String						name;												// 0..1
	private String						number;												// 0..1
	private String						publishedName;										// 0..1
	private TransportMode					transportMode;										// 0..1
	private List<String>					lineEndIds			= new ArrayList<String>();		// 0..w
	private List<StopPoint> 				lineEnds			= new ArrayList<StopPoint>();	// 0..w
	private List<String>					routeIds			= new ArrayList<String>();		// 0..w
	private List<Route>					routes				= new ArrayList<Route>();		// 0..w
	private Registration					registration;										// 0..1
	private String						ptNetworkIdShortcut;								// 0..1
	private TransportNetwork				transportNetwork;									// 0..1
	private String						comment;											// 0..1
	
	private RespPTLineStructTimetableType	respPTLineStructTimetableType;						// 1..w
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}
	
	public String getPublishedName() {
		return publishedName;
	}
	
	public void setTransportMode(TransportMode transportMode) {
		this.transportMode = transportMode;
	}
	
	public TransportMode getTransportMode() {
		return transportMode;
	}
	
	public void setLineEndIds(List<String> lineEndIds) {
		this.lineEndIds = lineEndIds;
	}
	
	public List<String> getLineEndIds() {
		return lineEndIds;
	}
	
	public void addLineEndId(String lineEndId) {
		lineEndIds.add(lineEndId);
	}
	
	public void removeLineEndId(String lineEndId) {
		lineEndIds.remove(lineEndId);
	}
	
	public void removeLineEndId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineEndIdsCount()))
			throw new IndexOutOfBoundsException();
		lineEndIds.remove(i);
	}
	
	public String getLineEndId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineEndIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)lineEndIds.get(i);
	}
	
	public int getLineEndIdsCount() {
		if (lineEndIds == null)
			return 0;
		return lineEndIds.size();
	}
	
	public void setLineEnds(List<StopPoint> lineEnds) {
		this.lineEnds = lineEnds;
	}
	
	public List<StopPoint> getLineEnds() {
		return lineEnds;
	}
	
	public void addLineEnd(StopPoint lineEnd) {
		lineEnds.add(lineEnd);
	}
	
	public void removeLineEnd(StopPoint lineEnd) {
		lineEnds.remove(lineEnd);
	}
	
	public void removeLineEnd(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineEndsCount()))
			throw new IndexOutOfBoundsException();
		lineEnds.remove(i);
	}
	
	public StopPoint getLineEnd(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineEndsCount()))
			throw new IndexOutOfBoundsException();
		return (StopPoint)lineEnds.get(i);
	}
	
	public int getLineEndsCount() {
		if (lineEnds == null)
			return 0;
		return lineEnds.size();
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
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setPTNetworkIdShortcut(String ptNetworkIdShortcut) {
		this.ptNetworkIdShortcut = ptNetworkIdShortcut;
	}
	
	public String getPTNetworkIdShortcut() {
		return ptNetworkIdShortcut;
	}
	
	public void setTransportNetwork(TransportNetwork transportNetwork) {
		this.transportNetwork = transportNetwork;
	}
	
	public TransportNetwork getTransportNetwork() {
		return transportNetwork;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setRespPTLineStructTimetableType(RespPTLineStructTimetableType respPTLineStructTimetableType) {
		this.respPTLineStructTimetableType = respPTLineStructTimetableType;
	}
	
	public RespPTLineStructTimetableType getRespPTLineStructTimetableType() {
		return respPTLineStructTimetableType;
	}
	
	public enum TransportMode {
		Air,
        Train,
        LongDistanceTrain,
        LocalTrain,
        RapidTransit,
        Metro,
        Tramway,
        Coach,
        Bus,
        Ferry,
        Waterborne,
        PrivateVehicle,
        Walk,
        Trolleybus,
        Bicycle,
        Shuttle,
        Taxi,
        VAL,
        Other
	}
}
