package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VehicleJourney {
	
	private ChouetteLineDescription 		chouetteLineDescription;
	private ServiceStatusValueType 			serviceStatusValueType;
	private TransportMode 					transportMode;
	private String 							comment;
	private Date 							creationTime;
	private String 							creatorId;
	private String 							facility;
	private String 							objectId;
	private boolean 						hasObjectVersion 				= false;
	private int 							objectVersion;
	private boolean 						hasNumber 						= false;
	private int 							number;
	private List<Timetable> 				timetables 						= new ArrayList<Timetable>();
	private String							routeId;
	private ChouetteRoute					chouetteRoute;
	private String							journeyPatternId;
	private JourneyPattern                  journeyPattern;
	private String							timeSlotId;
	private TimeSlot						timeSlot;
	private String 							publishedJourneyName;
	private String 							publishedJourneyIdentifier;
	private String							vehicleTypeIdentifier;
	private String							lineIdShortcut;
	private String							routeIdShortcut;
	private String							operatorId;
	private List<VehicleJourneyAtStop>		vehicleJourneyAtStops			= new ArrayList<VehicleJourneyAtStop>();
	
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	
	public String getOperatorId() {
		return operatorId;
	}
	
	public void setRouteIdShortcut(String routeIdShortcut) {
		this.routeIdShortcut = routeIdShortcut;
	}
	
	public String getRouteIdShortcut() {
		return routeIdShortcut;
	}
	
	public void setLineIdShortcut(String lineIdShortcut) {
		this.lineIdShortcut = lineIdShortcut;
	}
	
	public String getLineIdShortcut() {
		return lineIdShortcut;
	}
	
	public void setVehicleTypeIdentifier(String vehicleTypeIdentifier) {
		this.vehicleTypeIdentifier = vehicleTypeIdentifier;
	}
	
	public String getVehicleTypeIdentifier() {
		return vehicleTypeIdentifier;
	}
	
	public void setPublishedJourneyIdentifier(String publishedJourneyIdentifier) {
		this.publishedJourneyIdentifier = publishedJourneyIdentifier;
	}
	
	public String getPublishedJourneyIdentifier() {
		return publishedJourneyIdentifier;
	}
	
	public void setPublishedJourneyName(String publishedJourneyName) {
		this.publishedJourneyName = publishedJourneyName;
	}
	
	public String getPublishedJourneyName() {
		return publishedJourneyName;
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
	
	public void setTimeSlotId(String timeSlotId) {
		this.timeSlotId = timeSlotId;
	}
	
	public String getTimeSlotId() {
		return timeSlotId;
	}
	
	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	public TimeSlot getTimeSlot() {
		return timeSlot;
	}
	
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	public String getRouteId() {
		return routeId;
	}
	
	public void setChouetteRoute(ChouetteRoute chouetteRoute) {
		this.chouetteRoute = chouetteRoute;
	}
	
	public ChouetteRoute getChouetteRoute() {
		return chouetteRoute;
	}
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void setServiceStatusValueType(ServiceStatusValueType serviceStatusValueType) {
		this.serviceStatusValueType = serviceStatusValueType;
	}
	
	public ServiceStatusValueType getServiceStatusValueType() {
		return serviceStatusValueType;
	}
	
	public void setTransportMode(TransportMode transportMode) {
		this.transportMode = transportMode;
	}
	
	public TransportMode getTransportMode() {
		return transportMode;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void setFacility(String facility) {
		this.facility = facility;
	}
	
	public String getFacility() {
		return facility;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectVersion(int objectVersion) {
		if (objectVersion >= 1) {
			hasObjectVersion = true;
		this.objectVersion = objectVersion;
                }
                else {
			hasObjectVersion = false;
		this.objectVersion = 1;
                }
	}
	
	public int getObjectVersion() {
		return objectVersion;
	}
	
	public boolean hasObjectVersion() {
		return hasObjectVersion;
	}
	
	public void setNumber(int number) {
		this.number = number;
		if (number >= 0)
			hasNumber = true;
		else
			hasNumber = false;
	}
	
	public int getNumber() {
		return number;
	}
	
	public boolean hasNumber() {
		return hasNumber;
	}
	
	public void addTimetable(Timetable timetable) throws IndexOutOfBoundsException {
		timetables.add(timetable);
	}
	
	public void addTimetable(int index, Timetable timetable) throws IndexOutOfBoundsException {
		timetables.add(index, timetable);
	}
	
	public void removeTimetable(int index) throws IndexOutOfBoundsException {
		timetables.remove(index);
	}
	
	public void removeTimetable(Timetable timetable) {
		timetables.remove(timetable);
	}
	
	public void clearTimetables() {
		timetables.clear();
	}
	
	public void setTimetables(List<Timetable> timetables) {
		this.timetables = timetables;
	}
	
	public List<Timetable> getTimetables() {
		return timetables;
	}
	
	public Timetable[] getTimetableAsTable() {
		int size = timetables.size();
		Timetable[] mArray = new Timetable[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (Timetable)timetables.get(index);
		return mArray;
	}
	
	public Timetable getTimetable(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > timetables.size()))
            throw new IndexOutOfBoundsException();
		return (Timetable) timetables.get(index);
	}
	
	public int getTimetableCount() {
        return timetables.size();
    }
	
	public void setTimetables(ArrayList<Timetable> timetables) {
        this.timetables = timetables;
    }
	
    public void setTimetables(Timetable[] arrayOfTimetables) {
    	timetables.clear();
        for (int i = 0; i < arrayOfTimetables.length; i++)
            timetables.add(arrayOfTimetables[i]);
    }
    
    public void setTimetable(int index, Timetable timetable) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > timetables.size()))
    		throw new IndexOutOfBoundsException();
    	timetables.set(index, timetable);
    }
	
	public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop) throws IndexOutOfBoundsException {
		vehicleJourneyAtStops.add(vehicleJourneyAtStop);
	}
	
	public void addVehicleJourneyAtStop(int index, VehicleJourneyAtStop vehicleJourneyAtStop) throws IndexOutOfBoundsException {
		vehicleJourneyAtStops.add(index, vehicleJourneyAtStop);
	}
	
	public void removeVehicleJourneyAtStop(int index) throws IndexOutOfBoundsException {
		vehicleJourneyAtStops.remove(index);
	}
	
	public void removeVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop) {
		vehicleJourneyAtStops.remove(vehicleJourneyAtStop);
	}
	
	public void clearVehicleJourneyAtStops() {
		vehicleJourneyAtStops.clear();
	}
	
	public void setVehicleJourneyAtStops(List<VehicleJourneyAtStop> vehicleJourneyAtStops) {
		this.vehicleJourneyAtStops = vehicleJourneyAtStops;
	}
	
	public List<VehicleJourneyAtStop> getVehicleJourneyAtStops() {
		return vehicleJourneyAtStops;
	}
	
	public VehicleJourneyAtStop[] getVehicleJourneyAtStopAsTable() {
		int size = vehicleJourneyAtStops.size();
		VehicleJourneyAtStop[] mArray = new VehicleJourneyAtStop[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (VehicleJourneyAtStop)vehicleJourneyAtStops.get(index);
		return mArray;
	}
	
	public VehicleJourneyAtStop getVehicleJourneyAtStop(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > vehicleJourneyAtStops.size()))
            throw new IndexOutOfBoundsException();
		return (VehicleJourneyAtStop) vehicleJourneyAtStops.get(index);
	}
	
	public int getVehicleJourneyAtStopCount() {
        return vehicleJourneyAtStops.size();
    }
	
	public void setVehicleJourneyAtStops(ArrayList<VehicleJourneyAtStop> vehicleJourneyAtStops) {
        this.vehicleJourneyAtStops = vehicleJourneyAtStops;
    }
	
    public void setVehicleJourneyAtStops(VehicleJourneyAtStop[] arrayOfVehicleJourneyAtStops) {
    	vehicleJourneyAtStops.clear();
        for (int i = 0; i < arrayOfVehicleJourneyAtStops.length; i++)
            vehicleJourneyAtStops.add(arrayOfVehicleJourneyAtStops[i]);
    }
    
    public void setVehicleJourneyAtStop(int index, VehicleJourneyAtStop vehicleJourneyAtStop) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > vehicleJourneyAtStops.size()))
    		throw new IndexOutOfBoundsException();
    	vehicleJourneyAtStops.set(index, vehicleJourneyAtStop);
    }
    
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<VehicleJourney>\n");
		stb.append("<RouteId>"+routeId+"</RouteId>\n");
		if (journeyPatternId != null)
			stb.append("<JourneyPatternId>"+journeyPatternId+"</JourneyPatternId>\n");
		if (publishedJourneyName != null)
			stb.append("<PublishedJourneyName>"+publishedJourneyName+"</PublishedJourneyName>\n");
		if (publishedJourneyIdentifier != null)
			stb.append("<PublishedJourneyIdentifier>"+publishedJourneyIdentifier+"</PublishedJourneyIdentifier>\n");
		if (transportMode != null)
			stb.append("<TransportMode>"+transportMode.toString()+"</TransportMode>\n");
		if (vehicleTypeIdentifier != null)
			stb.append("<VehicleTypeIdentifier>"+vehicleTypeIdentifier+"</VehicleTypeIdentifier>\n");
		if (serviceStatusValueType != null)
			stb.append("<StatusValue>"+serviceStatusValueType+"</StatusValue>\n");
		if (lineIdShortcut != null)
			stb.append("<LineIdShortcut>"+lineIdShortcut+"</LineIdShortcut>\n");
		if (routeIdShortcut != null)
			stb.append("<RouteIdShortcut>"+routeIdShortcut+"</RouteIdShortcut>\n");
		if (operatorId != null)
			stb.append("<OperatorId>"+operatorId+"</OperatorId>\n");
		if (facility != null)
			stb.append("<Facility>"+facility+"</Facility>\n");
		if (hasNumber)
			stb.append("<Number>"+number+"</Number>\n");
			
		stb.append("</VehicleJourney>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		return stb.toString();
	}
}
