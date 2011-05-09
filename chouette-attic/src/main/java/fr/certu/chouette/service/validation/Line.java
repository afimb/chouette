package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Line {
	
	private ChouetteLineDescription 	chouetteLineDescription;
	private Registration 				registration;
	private String 						comment;
	private Date 						creationTime;
	private String 						creatorId;
	private String 						name;
	private String 						number;
	private String 						objectId;
	private boolean 					hasObjectVersion			= false;
	private int 						objectVersion;
	private String 						publishedName;
	private TransportMode 				transportMode;
	private String[] 					routeIds;
	private List<ChouetteRoute>			routes						= new ArrayList<ChouetteRoute>();
	private String[]					lineEnds;
	private List<StopArea>				stopAreaLineEnds			= new ArrayList<StopArea>();
	private List<StopPoint>				stopPointLineEnds			= new ArrayList<StopPoint>();
	private String 						ptNetworkIdShortcut;
	private PTNetwork					pTNetwork;
	
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
	
	public void setTransportMode(TransportMode transportMode) {
		this.transportMode = transportMode;
	}
	
	public TransportMode getTransportMode() {
		return transportMode;
	}
	
	public void setRouteIds(String[] routeIds) {
		this.routeIds = routeIds;
	}
	
	public String[] getRouteIds() {
		return routeIds;
	}
	
	public void addRoute(ChouetteRoute route) throws IndexOutOfBoundsException {
		routes.add(route);
	}
	
	public void addRoute(int index, ChouetteRoute route) throws IndexOutOfBoundsException {
		routes.add(index, route);
	}
	
	public void removeRoute(int index) throws IndexOutOfBoundsException {
		routes.remove(index);
	}
	
	public void removeRoute(ChouetteRoute route) {
		routes.remove(route);
	}
	
	public void clearRoutes() {
		routes.clear();
	}
	
	public void setRoutes(List<ChouetteRoute> routes) {
		this.routes = routes;
	}
	
	public List<ChouetteRoute> getRoutes() {
		return routes;
	}
	
	public ChouetteRoute[] getRouteAsTable() {
		int size = routes.size();
		ChouetteRoute[] mArray = new ChouetteRoute[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ChouetteRoute)routes.get(index);
		return mArray;
	}
	
	public ChouetteRoute getRoute(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > routes.size()))
            throw new IndexOutOfBoundsException();
		return (ChouetteRoute) routes.get(index);
	}
	
	public int getRouteCount() {
        return routes.size();
    }
	
	public void setRoutes(ArrayList<ChouetteRoute> routes) {
        this.routes = routes;
    }
	
    public void setRoutes(ChouetteRoute[] arrayOfRoutes) {
    	routes.clear();
        for (int i = 0; i < arrayOfRoutes.length; i++)
            routes.add(arrayOfRoutes[i]);
    }
    
    public void setRoute(int index, ChouetteRoute route) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > routes.size()))
    		throw new IndexOutOfBoundsException();
    	routes.set(index, route);
    }
	
	public void setLineEnds(String[] lineEnds) {
		this.lineEnds = lineEnds;
	}
	
	public String[] getLineEnds() {
		return lineEnds;
	}
	
	public void addStopAreaLineEnd(StopArea stopAreaLineEnd) throws IndexOutOfBoundsException {
		stopAreaLineEnds.add(stopAreaLineEnd);
	}
	
	public void addStopAreaLineEnd(int index, StopArea stopAreaLineEnd) throws IndexOutOfBoundsException {
		stopAreaLineEnds.add(index, stopAreaLineEnd);
	}
	
	public void removeStopAreaLineEnd(int index) throws IndexOutOfBoundsException {
		stopAreaLineEnds.remove(index);
	}
	
	public void removeStopAreaLineEnd(StopArea stopAreaLineEnd) {
		stopAreaLineEnds.remove(stopAreaLineEnd);
	}
	
	public void clearStopAreaLineEnds() {
		stopAreaLineEnds.clear();
	}
	
	public void setStopAreaLineEnds(List<StopArea> stopAreaLineEnds) {
		this.stopAreaLineEnds = stopAreaLineEnds;
	}
	
	public List<StopArea> getStopAreaLineEnds() {
		return stopAreaLineEnds;
	}
	
	public StopArea[] getStopAreaLineEndAsTable() {
		int size = stopAreaLineEnds.size();
		StopArea[] mArray = new StopArea[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopArea)stopAreaLineEnds.get(index);
		return mArray;
	}
	
	public StopArea getStopAreaLineEnd(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > stopAreaLineEnds.size()))
            throw new IndexOutOfBoundsException();
		return (StopArea) stopAreaLineEnds.get(index);
	}
	
	public int getStopAreaLineEndCount() {
        return stopAreaLineEnds.size();
    }
	
	public void setStopAreaLineEnds(ArrayList<StopArea> stopAreaLineEnds) {
        this.stopAreaLineEnds = stopAreaLineEnds;
    }
	
    public void setStopAreaLineEnds(StopArea[] arrayOfStopAreaLineEnds) {
    	stopAreaLineEnds.clear();
        for (int i = 0; i < arrayOfStopAreaLineEnds.length; i++)
            stopAreaLineEnds.add(arrayOfStopAreaLineEnds[i]);
    }
    
    public void setStopAreaLineEnd(int index, StopArea stopAreaLineEnd) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > stopAreaLineEnds.size()))
    		throw new IndexOutOfBoundsException();
    	stopAreaLineEnds.set(index, stopAreaLineEnd);
    }
	
	public void addStopPointLineEnd(StopPoint stopPointLineEnd) throws IndexOutOfBoundsException {
		stopPointLineEnds.add(stopPointLineEnd);
	}
	
	public void addStopPointLineEnd(int index, StopPoint stopPointLineEnd) throws IndexOutOfBoundsException {
		stopPointLineEnds.add(index, stopPointLineEnd);
	}
	
	public void removeStopPointLineEnd(int index) throws IndexOutOfBoundsException {
		stopPointLineEnds.remove(index);
	}
	
	public void removeStopPointLineEnd(StopPoint stopPointLineEnd) {
		stopPointLineEnds.remove(stopPointLineEnd);
	}
	
	public void clearStopPointLineEnds() {
		stopPointLineEnds.clear();
	}
	
	public void setStopPointLineEnds(List<StopPoint> stopPointLineEnds) {
		this.stopPointLineEnds = stopPointLineEnds;
	}
	
	public List<StopPoint> getStopPointLineEnds() {
		return stopPointLineEnds;
	}
	
	public StopPoint[] getStopPointLineEndAsTable() {
		int size = stopPointLineEnds.size();
		StopPoint[] mArray = new StopPoint[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopPoint)stopPointLineEnds.get(index);
		return mArray;
	}
	
	public StopPoint getStopPointLineEnd(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > stopPointLineEnds.size()))
            throw new IndexOutOfBoundsException();
		return (StopPoint) stopPointLineEnds.get(index);
	}
	
	public int getStopPointLineEndCount() {
        return stopPointLineEnds.size();
    }
	
	public void setStopPointLineEnds(ArrayList<StopPoint> stopPointLineEnds) {
        this.stopPointLineEnds = stopPointLineEnds;
    }
	
    public void setStopPointLineEnds(StopPoint[] arrayOfStopPointLineEnds) {
    	stopPointLineEnds.clear();
        for (int i = 0; i < arrayOfStopPointLineEnds.length; i++)
            stopPointLineEnds.add(arrayOfStopPointLineEnds[i]);
    }
    
    public void setStopPointLineEnd(int index, StopPoint stopPointLineEnd) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > stopPointLineEnds.size()))
    		throw new IndexOutOfBoundsException();
    	stopPointLineEnds.set(index, stopPointLineEnd);
    }
	
	public void setPtNetworkIdShortcut(String ptNetworkIdShortcut) {
		this.ptNetworkIdShortcut = ptNetworkIdShortcut;
	}
	
	public String getPtNetworkIdShortcut() {
		return ptNetworkIdShortcut;
	}
	
	public void setPTNetwork(PTNetwork pTNetwork) {
		this.pTNetwork = pTNetwork;
	}
	
	public PTNetwork getPTNetwork() {
		return pTNetwork;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Line>\n");
		if (name != null)
			stb.append("<Name>"+name+"</Name>\n");
		if (number != null)
			stb.append("<Number>"+number+"</Number>\n");
		if (publishedName != null)
			stb.append("<PublishedName>"+publishedName+"</PublishedName>\n");
		if (transportMode != null)
			stb.append("<TransportModeName>"+transportMode+"</TransportModeName>\n");
		if (lineEnds != null)
			if (lineEnds.length > 0) {
				stb.append("<LineEnd>\n");
				for (int i = 0; i < lineEnds.length; i++)
					stb.append(lineEnds[i]);
				stb.append("</LineEnd>\n");
			}
		stb.append("<RouteId>\n");
		for (int i = 0; i < lineEnds.length; i++)
			stb.append(lineEnds[i]);
		stb.append("</RouteId>\n");
		if (registration != null)
			stb.append(registration.toString());
		if (ptNetworkIdShortcut != null)
			stb.append("<PtNetworkIdShortcut>"+ptNetworkIdShortcut+"</PtNetworkIdShortcut>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</Line>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Line>\n");
		if (name != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Name>"+name+"</Name>\n");
		}
		if (number != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Number>"+number+"</Number>\n");
		}
		if (publishedName != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<PublishedName>"+publishedName+"</PublishedName>\n");
		}
		if (transportMode != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<TransportModeName>"+transportMode+"</TransportModeName>\n");
		}
		if (lineEnds != null)
			if (lineEnds.length > 0) {
				for (int j = 0; j < indent+1; j++)
					for (int k = 0; k < indentSize; k++)
						stb.append(" ");
				stb.append("<LineEnd>\n");
				for (int i = 0; i < lineEnds.length; i++) {
					for (int j = 0; j < indent+2; j++)
						for (int k = 0; k < indentSize; k++)
							stb.append(" ");
					stb.append(lineEnds[i]);
				}
				for (int j = 0; j < indent+1; j++)
					for (int k = 0; k < indentSize; k++)
						stb.append(" ");
				stb.append("</LineEnd>\n");
			}
		for (int j = 0; j < indent+1; j++)
			for (int k = 0; k < indentSize; k++)
				stb.append(" ");
		stb.append("<RouteId>\n");
		for (int i = 0; i < lineEnds.length; i++) {
			for (int j = 0; j < indent+2; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append(lineEnds[i]);
		}
		for (int j = 0; j < indent+1; j++)
			for (int k = 0; k < indentSize; k++)
				stb.append(" ");
		stb.append("</RouteId>\n");
		if (registration != null)
			stb.append(registration.toString(indent+1, indentSize));
		if (ptNetworkIdShortcut != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<PtNetworkIdShortcut>"+ptNetworkIdShortcut+"</PtNetworkIdShortcut>\n");
		}
		if (comment != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");			
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		}
		if (creationTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		}
		if (creatorId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Line>\n");
		return stb.toString();
	}
}
