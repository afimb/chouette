package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JourneyPattern {
	
	private ChouetteLineDescription 	chouetteLineDescription;
	private Registration 				registration;
	private String 						comment;
	private Date 						creationTime;
	private String 						creatorId;
	private String 						origin;
	private String 						destination;
	private String 						name;
	private String 						objectId;
	private boolean 					hasObjectVersion 				= false;
	private int 						objectVersion;
	private String 						publishedName;
	private String[]					stopPointList;
	private List<StopPoint>				stopPoints						= new ArrayList<StopPoint>();
	private String						lineIdShortcut;
	private String 						routeId;
	private ChouetteRoute				route;
	
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	
	public String getRouteId() {
		return routeId;
	}
	
	public void setRoute(ChouetteRoute route) {
		this.route = route;
	}
	
	public ChouetteRoute getRoute() {
		return route;
	}
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
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
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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
	
	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}
	
	public String getPublishedName() {
		return publishedName;
	}
	
	public void setStopPointList(String[] stopPointList) {
		this.stopPointList = stopPointList;
	}
	
	public String[] getStopPointList() {
		return stopPointList;
	}
	
	public void addStopPoint(StopPoint stopPoint) throws IndexOutOfBoundsException {
		stopPoints.add(stopPoint);
	}
	
	public void addStopPoint(int index, StopPoint stopPoint) throws IndexOutOfBoundsException {
		stopPoints.add(index, stopPoint);
	}
	
	public void removeStopPoint(int index) throws IndexOutOfBoundsException {
		stopPoints.remove(index);
	}
	
	public void removeStopPoint(StopPoint stopPoint) {
		stopPoints.remove(stopPoint);
	}
	
	public void clearStopPoints() {
		stopPoints.clear();
	}
	
	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
	
	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}
	
	public StopPoint[] getStopPointAsTable() {
		int size = stopPoints.size();
		StopPoint[] mArray = new StopPoint[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopPoint)stopPoints.get(index);
		return mArray;
	}
	
	public StopPoint getStopPoint(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > stopPoints.size()))
            throw new IndexOutOfBoundsException();
		return (StopPoint) stopPoints.get(index);
	}
	
	public int getStopPointCount() {
        return stopPoints.size();
    }
	
	public void setStopPoints(ArrayList<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }
	
    public void setStopPoints(StopPoint[] arrayOfStopPoints) {
    	stopPoints.clear();
        for (int i = 0; i < arrayOfStopPoints.length; i++)
            stopPoints.add(arrayOfStopPoints[i]);
    }
    
    public void setStopPoint(int index, StopPoint stopPoint) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > stopPoints.size()))
    		throw new IndexOutOfBoundsException();
    	stopPoints.set(index, stopPoint);
    }
	
	public void setLineIdShortcut(String lineIdShortcut) {
		this.lineIdShortcut = lineIdShortcut;
	}
	
	public String getLineIdShortcut() {
		return lineIdShortcut;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<JourneyPattern\n>");
		stb.append("</JourneyPattern\n>");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		return stb.toString();
	}
}
