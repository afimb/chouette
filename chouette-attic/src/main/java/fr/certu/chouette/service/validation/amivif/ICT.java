package fr.certu.chouette.service.validation.amivif;

public class ICT extends TridentObject {
	
	private ICTType			ictType;								// 1
	private int				section;								// 0..1
	private String			routeId;								// 1
	private Route			route;									// 1
	private String[]		stopPointIds		= new String[2];	// 2
	private StopPoint[]		stopPoints			= new StopPoint[2];	// 2
	private String			vehicleJourneyId;						// 0..1
	private	VehicleJourney	vehicleJourney;							// 0..1
	
	public void setICTType(ICTType ictType) {
		this.ictType = ictType;
	}
	
	public ICTType getICTType() {
		return ictType;
	}
	
	public void setSection(int section) {
		this.section = section;
	}
	
	public int getSection() {
		return section;
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
	
	public void setStopPointId(String[] stopPointIds) throws IndexOutOfBoundsException {
		if ((stopPointIds == null) || (stopPointIds.length != 2))
			throw new IndexOutOfBoundsException();
		this.stopPointIds = stopPointIds;
	}
	
	public String[] getStopPointId() {
		return stopPointIds;
	}
	
	public void setStopPoint(StopPoint[] stopPoints) throws IndexOutOfBoundsException {
		if ((stopPoints == null) || (stopPoints.length != 2))
			throw new IndexOutOfBoundsException();
		this.stopPoints = stopPoints;
	}
	
	public StopPoint[] getStopPoint() {
		return stopPoints;
	}
	
	public void setVehicleJourneyId(String vehicleJourneyId) {
		this.vehicleJourneyId = vehicleJourneyId;
	}
	
	public String getVehicleJourneyId() {
		return vehicleJourneyId;
	}
	
	public void setVehicleJourney(VehicleJourney vehicleJourney) {
		this.vehicleJourney = vehicleJourney;
	}
	
	public VehicleJourney getVehicleJourney() {
		return vehicleJourney;
	}
	
	public enum ICTType {
		ITL,
		Section
	}
}
