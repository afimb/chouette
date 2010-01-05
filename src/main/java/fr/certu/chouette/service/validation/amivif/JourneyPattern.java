package fr.certu.chouette.service.validation.amivif;

import java.util.List;

public class JourneyPattern extends TridentObject {
	
	private String			name;					// 0..1
	private String			publishedName;			// 0..1
	private String			routeId;				// 1
	private Route			route;					// 1
	private String			origin;					// 0..1
	private StopPoint		originStopPoint;		// 0..1
	private String			destination;			// 0..1
	private StopPoint		destinationStopPoint;	// 0..1
	private List<String>	stopPointIds;			// 2..w
	private List<StopPoint>	stopPoints;				// 2..w
	private Registration	registration;			// 0..1
	private String			lineIdShortcut;			// 0..1
	private Line			line;					// 0..1
	private String			comment;				// 0..1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}
	
	public String getPublishedName() {
		return publishedName;
	}
	
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	public String getRouteId() {
		return routeId;
	}
	
	public void setRoute(Route route) {
		this.route = route;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOriginStopPoint(StopPoint originStopPoint) {
		this.originStopPoint = originStopPoint;
	}
	
	public StopPoint getOriginStopPoint() {
		return originStopPoint;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setDestinationStopPoint(StopPoint destinationStopPoint) {
		this.destinationStopPoint = destinationStopPoint;
	}
	
	public StopPoint getDestinationStopPoint() {
		return destinationStopPoint;
	}
	
	public void setStopPointIds(List<String> stopPointIds) {
		this.stopPointIds = stopPointIds;
	}
	
	public List<String> getStopPointIds() {
		return stopPointIds;
	}
	
	public void addStopPointId(String stopPointId) {
		stopPointIds.add(stopPointId);
	}
	
	public void removeStopPointId(String stopPointId) {
		stopPointIds.remove(stopPointId);
	}
	
	public void removeStopPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointIdsCount()))
			throw new IndexOutOfBoundsException();
		stopPointIds.remove(i);
	}
	
	public String getStopPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)stopPointIds.get(i);
	}
	
	public int getStopPointIdsCount() {
		if (stopPointIds == null)
			return 0;
		return stopPointIds.size();
	}
	
	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
	
	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}
	
	public void addStopPoint(StopPoint stopPoint) {
		stopPoints.add(stopPoint);
	}
	
	public void removeStopPoint(StopPoint stopPoint) {
		stopPoints.remove(stopPoint);
	}
	
	public void removeStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		stopPoints.remove(i);
	}
	
	public StopPoint getStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		return (StopPoint)stopPoints.get(i);
	}
	
	public int getStopPointsCount() {
		if (stopPoints == null)
			return 0;
		return stopPoints.size();
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setLineIdShortcut(String lineIdShortcut) {
		this.lineIdShortcut = lineIdShortcut;
	}
	
	public String getLineIdShortcut() {
		return lineIdShortcut;
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
