package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exolab.castor.types.Duration;

public class VehicleJourney extends TridentObject {
	
	private String						routeId;																// 1
	private Route						route;																	// 1
	private String						journeyPatternId;														// 0..1
	private JourneyPattern				journeyPattern;															// 0..1
	private String						publishedJourneyName;													// 0..1
	private String						publishedJourneyIdentifier;												// 0..1
	private TransportMode				transportMode;															// 0..1
	private String						vehicleTypeIdentifier;													// 0..1
	private StatusValue					statusValue;															// 0..1
	private String						lineIdShortcut;															// 0..1
	private Line						line;																	// 0..1
	private String						routeIdShortcut;														// 0..1
	private String						operatorId;																// 0..1
	private Company						operator;																// 0..1
	private String						facility;																// 0..1
	private int							number;																	// 0..1
	private List<VehicleJourneyAtStop>	vehicleJourneyAtStops		= new ArrayList<VehicleJourneyAtStop>();	// 2..w
	private String						comment;																// 0..1
	
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
	
	public void setJourneyPatternId(String journeyPatternId) {
		this.journeyPatternId = journeyPatternId;
	}
	
	public String getJourneyPatternId() {
		return journeyPatternId;
	}
	
	public void setJourneyPattern(JourneyPattern journeyPattern) {
		this.journeyPattern = journeyPattern;
	}
	
	public JourneyPattern getJourneyPattern() {
		return journeyPattern;
	}
	
	public void setPublishedJourneyName(String publishedJourneyName) {
		this.publishedJourneyName = publishedJourneyName;
	}
	
	public String getPublishedJourneyName() {
		return publishedJourneyName;
	}
	
	public void setPublishedJourneyIdentifier(String publishedJourneyIdentifier) {
		this.publishedJourneyIdentifier = publishedJourneyIdentifier;
	}
	
	public String getPublishedJourneyIdentifier() {
		return publishedJourneyIdentifier;
	}
	
	public void setTransportMode(TransportMode transportMode) {
		this.transportMode= transportMode;
	}
	
	public TransportMode getTransportMode() {
		return transportMode;
	}
	
	public void setVehicleTypeIdentifier(String vehicleTypeIdentifier) {
		this.vehicleTypeIdentifier = vehicleTypeIdentifier;
	}
	
	public String getVehicleTypeIdentifier() {
		return vehicleTypeIdentifier;
	}
	
	public void setStatusValue(StatusValue statusValue) {
		this.statusValue = statusValue;
	}
	
	public StatusValue getStatusValue() {
		return statusValue;
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
	
	public void setRouteIdShortcut(String routeIdShortcut) {
		this.routeIdShortcut = routeIdShortcut;
	}
	
	public String getRouteIdShortcut() {
		return routeIdShortcut;
	}
	
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	
	public String getOperatorId() {
		return operatorId;
	}
	
	public void setOperator(Company operator) {
		this.operator = operator;
	}
	
	public Company getOperator() {
		return operator;
	}
	
	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	public String getFacility() {
		return facility;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setVehicleJourneyAtStops(List<VehicleJourneyAtStop> vehicleJourneyAtStops) {
		this.vehicleJourneyAtStops = vehicleJourneyAtStops;
	}
	
	public List<VehicleJourneyAtStop> getVehicleJourneyAtStops() {
		return vehicleJourneyAtStops;
	}
	
	public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop) {
		vehicleJourneyAtStops.add(vehicleJourneyAtStop);
	}
	
	public void removeVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop) {
		vehicleJourneyAtStops.remove(vehicleJourneyAtStop);
	}
	
	public void removeVehicleJourneyAtStop(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneyAtStopsCount()))
			throw new IndexOutOfBoundsException();
		vehicleJourneyAtStops.remove(i);
	}
	
	public VehicleJourneyAtStop getVehicleJourneyAtStop(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneyAtStopsCount()))
			throw new IndexOutOfBoundsException();
		return (VehicleJourneyAtStop)vehicleJourneyAtStops.get(i);
	}
	
	public int getVehicleJourneyAtStopsCount() {
		if (vehicleJourneyAtStops == null)
			return 0;
		return vehicleJourneyAtStops.size();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
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
	
	public enum StatusValue {
        Normal,
        Delayed,
        Cancelled,
        Disrupted,
        ReducedService,
        IncreasedService,
        Rerouted,
        NotStopping,
        Early
	}
	
	public class VehicleJourneyAtStop {
		
		private String							stopPointId;					// 1
		private	StopPoint						stopPoint;						// 1
		private String							vehicleJourneyId;				// 0..1
		private VehicleJourney					vehicleJourney;					// 0..1
		private String							connectingServiceId;			// 0..1
		private Date							arrivalTime;					// 0..1
		private Date							departureTime;					// 0..1
		private Date							waitingTime;					// 0..1
		private Duration						headwayFrequency;				// 0..1
		private BoardingAlightingPossibility	boardingAlightingPossibility;	// 0..1
		private int								order;							// 0..1 GRAETER THAN OR EQUAL TO 0
		
		public void setStopPointId(String stopPointId) {
			this.stopPointId = stopPointId;
		}
		
		public String getStopPointId() {
			return stopPointId;
		}
		
		public void setStopPoint(StopPoint stopPoint) {
			this.stopPoint = stopPoint;
		}
		
		public StopPoint getStopPoint() {
			return stopPoint;
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
		
		public void setConnectingServiceId(String connectingServiceId) {
			this.connectingServiceId = connectingServiceId;
		}
		
		public String getConnectingServiceId() {
			return connectingServiceId;
		}
		
		public void setArrivalTime(Date arrivalTime) {
			this.arrivalTime = arrivalTime;
		}
		
		public Date getArrivalTime() {
			return arrivalTime;
		}
		
		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}
		
		public Date getDepartureTime() {
			return departureTime;
		}
		
		public void setWaitingTime(Date waitingTime) {
			this.waitingTime = waitingTime;
		}
		
		public Date getWaitingTime() {
			return waitingTime;
		}
		
		public void setHeadwayFrequency(Duration duration) {
			this.headwayFrequency = duration;
		}
		
		public Duration getHeadwayFrequency() {
			return headwayFrequency;
		}
		
		public void setBoardingAlightingPossibility(BoardingAlightingPossibility boardingAlightingPossibility) {
			this.boardingAlightingPossibility = boardingAlightingPossibility;
		}
		
		public BoardingAlightingPossibility getBoardingAlightingPossibility() {
			return boardingAlightingPossibility;
		}
		
		public void setOrder(int order) {
			this.order = order;
		}
		
		public int getOrder() {
			return order;
		}
	}
	
	public enum BoardingAlightingPossibility {
		BoardAndAlight,
        AlightOnly,
        BoardOnly,
        NeitherBoardOrAlight,
        BoardAndAlightOnRequest,
        AlightOnRequest,
        BoardOnRequest
	}
}
