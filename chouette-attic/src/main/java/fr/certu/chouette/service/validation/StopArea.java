package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopArea {
	
	private ChouetteArea 			chouetteArea;
	private List<String> 			boundaryPoints 			= new ArrayList<String>();
	private String 					centroidOfArea;
	private AreaCentroid			areaCentroid;
	private String 					comment;
	private Date 					creationTime;
	private String 					creatorId;
	private String 					name;
	private String 					objectId;
	private boolean 				hasObjectVersion 		= false;
	private int 					objectVersion;
	private StopAreaExtension 		stopAreaExtension;
	private String[] 				containedStopIds;
	private List<StopPoint> 		containedStopPoints 	= new ArrayList<StopPoint>();
	private List<StopArea> 			containedStopAreas 		= new ArrayList<StopArea>();
	private List<StopArea> 			containedInITLs 		= new ArrayList<StopArea>();
	private StopArea 				containedInStopArea 	= null;
	private List<ConnectionLink> 	connectionLinkStarts 	= new ArrayList<ConnectionLink>();
	private List<ConnectionLink> 	connectionLinkEnds 		= new ArrayList<ConnectionLink>();
	
	public void setChouetteArea(ChouetteArea chouetteArea) {
		this.chouetteArea = chouetteArea;
	}
	
	public ChouetteArea getChouetteArea() {
		return chouetteArea;
	}
	
	public void addBoundaryPoint(String boundaryPoint) throws IndexOutOfBoundsException {
		boundaryPoints.add(boundaryPoint);
	}
	
	public void addBoundaryPoint(int index, String boundaryPoint) throws IndexOutOfBoundsException {
		boundaryPoints.add(index, boundaryPoint);
	}
	
	public void removeBoundaryPoint(int index) throws IndexOutOfBoundsException {
		boundaryPoints.remove(index);
	}
	
	public void removeBoundaryPoint(String boundaryPoint) {
		boundaryPoints.remove(boundaryPoint);
	}
	
	public void clearBoundaryPoints() {
		boundaryPoints.clear();
	}
	
	public void setBoundaryPoints(List<String> boundaryPoints) {
		this.boundaryPoints = boundaryPoints;
	}
	
	public List<String> getBoundaryPoints() {
		return boundaryPoints;
	}
	
	public String[] getBoundaryPointAsTable() {
		int size = boundaryPoints.size();
		String[] mArray = new String[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (String)boundaryPoints.get(index);
		return mArray;
	}
	
	public String getBoundaryPoint(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > boundaryPoints.size()))
            throw new IndexOutOfBoundsException();
		return (String) boundaryPoints.get(index);
	}
	
	public int getBoundaryPointCount() {
        return boundaryPoints.size();
    }
	
	public void setBoundaryPoints(ArrayList<String> boundaryPoints) {
        this.boundaryPoints = boundaryPoints;
    }
	
    public void setBoundaryPoints(String[] arrayOfBoundaryPoints) {
    	boundaryPoints.clear();
        for (int i = 0; i < arrayOfBoundaryPoints.length; i++)
            boundaryPoints.add(arrayOfBoundaryPoints[i]);
    }
    
    public void setBoundaryPoint(int index, String boundaryPoint) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > boundaryPoints.size()))
    		throw new IndexOutOfBoundsException();
    	boundaryPoints.set(index, boundaryPoint);
    }
	
	public void setCentroidOfArea(String centroidOfArea) {
		this.centroidOfArea = centroidOfArea;
	}
	
	public String getCentroidOfArea() {
		return centroidOfArea;
	}
	
	public void setAreaCentroid(AreaCentroid areaCentroid) {
		this.areaCentroid = areaCentroid;
	}
	
	public AreaCentroid getAreaCentroid() {
		return areaCentroid;
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
	
	public void setStopAreaExtension(StopAreaExtension stopAreaExtension) {
		this.stopAreaExtension = stopAreaExtension;
	}
	
	public StopAreaExtension getStopAreaExtension() {
		return stopAreaExtension;
	}
	
	public void setContainedStopIds(String[] containedStopIds) {
		this.containedStopIds = containedStopIds;
	}
	
	public String[] getContainedStopIds() {
		return containedStopIds;
	}
	
	public void addContainedStopPoint(StopPoint containedStopPoint) throws IndexOutOfBoundsException {
		containedStopPoints.add(containedStopPoint);
	}
	
	public void addContainedStopPoint(int index, StopPoint containedStopPoint) throws IndexOutOfBoundsException {
		containedStopPoints.add(index, containedStopPoint);
	}
	
	public void removeContainedStopPoint(int index) throws IndexOutOfBoundsException {
		containedStopPoints.remove(index);
	}
	
	public void removeContainedStopPoint(StopPoint containedStopPoint) {
		containedStopPoints.remove(containedStopPoint);
	}
	
	public void clearContainedStopPoints() {
		containedStopPoints.clear();
	}
	
	public void setContainedStopPoints(List<StopPoint> containedStopPoints) {
		this.containedStopPoints = containedStopPoints;
	}
	
	public List<StopPoint> getContainedStopPoints() {
		return containedStopPoints;
	}
	
	public StopPoint[] getContainedStopPointAsTable() {
		int size = containedStopPoints.size();
		StopPoint[] mArray = new StopPoint[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopPoint)containedStopPoints.get(index);
		return mArray;
	}
	
	public StopPoint getContainedStopPoint(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > containedStopPoints.size()))
            throw new IndexOutOfBoundsException();
		return (StopPoint) containedStopPoints.get(index);
	}
	
	public int getContainedStopPointCount() {
        return containedStopPoints.size();
    }
	
	public void setContainedStopPoints(ArrayList<StopPoint> containedStopPoints) {
        this.containedStopPoints = containedStopPoints;
    }
	
    public void setContainedStopPoints(StopPoint[] arrayOfContainedStopPoints) {
    	containedStopPoints.clear();
        for (int i = 0; i < arrayOfContainedStopPoints.length; i++)
            containedStopPoints.add(arrayOfContainedStopPoints[i]);
    }
    
    public void setContainedStopPoint(int index, StopPoint containedStopPoint) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > containedStopPoints.size()))
    		throw new IndexOutOfBoundsException();
    	containedStopPoints.set(index, containedStopPoint);
    }
	
	public void addContainedStopArea(StopArea containedStopArea) throws IndexOutOfBoundsException {
		containedStopAreas.add(containedStopArea);
	}
	
	public void addContainedStopArea(int index, StopArea containedStopArea) throws IndexOutOfBoundsException {
		containedStopAreas.add(index, containedStopArea);
	}
	
	public void removeContainedStopArea(int index) throws IndexOutOfBoundsException {
		containedStopAreas.remove(index);
	}
	
	public void removeContainedStopArea(StopArea containedStopArea) {
		containedStopAreas.remove(containedStopArea);
	}
	
	public void clearContainedStopAreas() {
		containedStopAreas.clear();
	}
	
	public void setContainedStopAreas(List<StopArea> containedStopAreas) {
		this.containedStopAreas = containedStopAreas;
	}
	
	public List<StopArea> getContainedStopAreas() {
		return containedStopAreas;
	}
	
	public StopArea[] getContainedStopAreaAsTable() {
		int size = containedStopAreas.size();
		StopArea[] mArray = new StopArea[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopArea)containedStopAreas.get(index);
		return mArray;
	}
	
	public StopArea getContainedStopArea(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > containedStopAreas.size()))
            throw new IndexOutOfBoundsException();
		return (StopArea) containedStopAreas.get(index);
	}
	
	public int getContainedStopAreaCount() {
        return containedStopAreas.size();
    }
	
	public void setContainedStopAreas(ArrayList<StopArea> containedStopAreas) {
        this.containedStopAreas = containedStopAreas;
    }
	
    public void setContainedStopAreas(StopArea[] arrayOfContainedStopAreas) {
    	containedStopAreas.clear();
        for (int i = 0; i < arrayOfContainedStopAreas.length; i++)
            containedStopAreas.add(arrayOfContainedStopAreas[i]);
    }
    
    public void setContainedStopArea(int index, StopArea containedStopArea) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > containedStopAreas.size()))
    		throw new IndexOutOfBoundsException();
    	containedStopAreas.set(index, containedStopArea);
    }
	
	public void addContainedInITL(StopArea containedInITL) throws IndexOutOfBoundsException {
		containedInITLs.add(containedInITL);
	}
	
	public void addContainedInITL(int index, StopArea containedInITL) throws IndexOutOfBoundsException {
		containedInITLs.add(index, containedInITL);
	}
	
	public void removeContainedInITL(int index) throws IndexOutOfBoundsException {
		containedInITLs.remove(index);
	}
	
	public void removeContainedInITL(StopArea containedInITL) {
		containedInITLs.remove(containedInITL);
	}
	
	public void clearContainedInITLs() {
		containedInITLs.clear();
	}
	
	public void setContainedInITLs(List<StopArea> containedInITLs) {
		this.containedInITLs = containedInITLs;
	}
	
	public List<StopArea> getContainedInITLs() {
		return containedInITLs;
	}
	
	public StopArea[] getContainedInITLAsTable() {
		int size = containedInITLs.size();
		StopArea[] mArray = new StopArea[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopArea)containedInITLs.get(index);
		return mArray;
	}
	
	public StopArea getContainedInITL(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > containedInITLs.size()))
            throw new IndexOutOfBoundsException();
		return (StopArea) containedInITLs.get(index);
	}
	
	public int getContainedInITLCount() {
        return containedInITLs.size();
    }
	
	public void setContainedInITLs(ArrayList<StopArea> containedInITLs) {
        this.containedInITLs = containedInITLs;
    }
	
    public void setContainedInITLs(StopArea[] arrayOfContainedInITLs) {
    	containedInITLs.clear();
        for (int i = 0; i < arrayOfContainedInITLs.length; i++)
            containedInITLs.add(arrayOfContainedInITLs[i]);
    }
    
    public void setContainedInITL(int index, StopArea containedInITL) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > containedInITLs.size()))
    		throw new IndexOutOfBoundsException();
    	containedInITLs.set(index, containedInITL);
    }
    
	public void setContainedInStopArea(StopArea containedInStopArea) {
		this.containedInStopArea = containedInStopArea;
	}
	
	public StopArea getContainedInStopArea() {
		return containedInStopArea;
	}
	
	public void addConnectionLinkStart(ConnectionLink connectionLinkStart) throws IndexOutOfBoundsException {
		connectionLinkStarts.add(connectionLinkStart);
	}
	
	public void addConnectionLinkStart(int index, ConnectionLink connectionLinkStart) throws IndexOutOfBoundsException {
		connectionLinkStarts.add(index, connectionLinkStart);
	}
	
	public void removeConnectionLinkStart(int index) throws IndexOutOfBoundsException {
		connectionLinkStarts.remove(index);
	}
	
	public void removeConnectionLinkStart(ConnectionLink connectionLinkStart) {
		connectionLinkStarts.remove(connectionLinkStart);
	}
	
	public void clearConnectionLinkStarts() {
		connectionLinkStarts.clear();
	}
	
	public void setConnectionLinkStarts(List<ConnectionLink> connectionLinkStarts) {
		this.connectionLinkStarts = connectionLinkStarts;
	}
	
	public List<ConnectionLink> getConnectionLinkStarts() {
		return connectionLinkStarts;
	}
	
	public ConnectionLink[] getConnectionLinkStartAsTable() {
		int size = connectionLinkStarts.size();
		ConnectionLink[] mArray = new ConnectionLink[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ConnectionLink)connectionLinkStarts.get(index);
		return mArray;
	}
	
	public ConnectionLink getConnectionLinkStart(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > connectionLinkStarts.size()))
            throw new IndexOutOfBoundsException();
		return (ConnectionLink) connectionLinkStarts.get(index);
	}
	
	public int getConnectionLinkStartCount() {
        return connectionLinkStarts.size();
    }
	
	public void setConnectionLinkStarts(ArrayList<ConnectionLink> connectionLinkStarts) {
        this.connectionLinkStarts = connectionLinkStarts;
    }
	
    public void setConnectionLinkStarts(ConnectionLink[] arrayOfConnectionLinkStarts) {
    	connectionLinkStarts.clear();
        for (int i = 0; i < arrayOfConnectionLinkStarts.length; i++)
            connectionLinkStarts.add(arrayOfConnectionLinkStarts[i]);
    }
    
    public void setConnectionLinkStart(int index, ConnectionLink connectionLinkStart) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > connectionLinkStarts.size()))
    		throw new IndexOutOfBoundsException();
    	connectionLinkStarts.set(index, connectionLinkStart);
    }
	
	public void addConnectionLinkEnd(ConnectionLink connectionLinkEnd) throws IndexOutOfBoundsException {
		connectionLinkEnds.add(connectionLinkEnd);
	}
	
	public void addConnectionLinkEnd(int index, ConnectionLink connectionLinkEnd) throws IndexOutOfBoundsException {
		connectionLinkEnds.add(index, connectionLinkEnd);
	}
	
	public void removeConnectionLinkEnd(int index) throws IndexOutOfBoundsException {
		connectionLinkEnds.remove(index);
	}
	
	public void removeConnectionLinkEnd(ConnectionLink connectionLinkEnd) {
		connectionLinkEnds.remove(connectionLinkEnd);
	}
	
	public void clearConnectionLinkEnds() {
		connectionLinkEnds.clear();
	}
	
	public void setConnectionLinkEnds(List<ConnectionLink> connectionLinkEnds) {
		this.connectionLinkEnds = connectionLinkEnds;
	}
	
	public List<ConnectionLink> getConnectionLinkEnds() {
		return connectionLinkEnds;
	}
	
	public ConnectionLink[] getConnectionLinkEndAsTable() {
		int size = connectionLinkEnds.size();
		ConnectionLink[] mArray = new ConnectionLink[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ConnectionLink)connectionLinkEnds.get(index);
		return mArray;
	}
	
	public ConnectionLink getConnectionLinkEnd(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > connectionLinkEnds.size()))
            throw new IndexOutOfBoundsException();
		return (ConnectionLink) connectionLinkEnds.get(index);
	}
	
	public int getConnectionLinkEndCount() {
        return connectionLinkEnds.size();
    }
	
	public void setConnectionLinkEnds(ArrayList<ConnectionLink> connectionLinkEnds) {
        this.connectionLinkEnds = connectionLinkEnds;
    }
	
    public void setConnectionLinkEnds(ConnectionLink[] arrayOfConnectionLinkEnds) {
    	connectionLinkEnds.clear();
        for (int i = 0; i < arrayOfConnectionLinkEnds.length; i++)
            connectionLinkEnds.add(arrayOfConnectionLinkEnds[i]);
    }
    
    public void setConnectionLinkEnd(int index, ConnectionLink connectionLinkEnd) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > connectionLinkEnds.size()))
    		throw new IndexOutOfBoundsException();
    	connectionLinkEnds.set(index, connectionLinkEnd);
    }
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<StopArea>\n");
		if (stopAreaExtension != null)
			stb.append(stopAreaExtension.toString());
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		if (name != null)
			stb.append("<Name>"+name+"</Name>\n");
		stb.append("<Contains\n>");
		for (int i = 0; i < containedStopIds.length; i++)
			stb.append(containedStopIds[i]+"\n");
		stb.append("</Contains\n>");
		if (boundaryPoints.size() > 0) {
			stb.append("<BoundaryPoint\n>");
			for (int i = 0; i < boundaryPoints.size(); i++)
				stb.append(boundaryPoints.get(i).toString()+"\n");
			stb.append("</BoundaryPoint\n>");
		}
		if (centroidOfArea != null)
			stb.append("<CentroidOfArea>"+centroidOfArea+"</CentroidOfArea>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId+"</CreatorId>\n");
		stb.append("</StopArea>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StopArea>\n");
		if (stopAreaExtension != null)
			stb.append(stopAreaExtension.toString(indent+1, indentSize));
		if (comment != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");
		}
		if (name != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Name>"+name+"</Name>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Contains\n>");
		for (int i = 0; i < containedStopIds.length; i++) {
			for (int j = 0; j < indent+2; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append(containedStopIds[i]+"\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Contains\n>");
		if (boundaryPoints.size() > 0) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<BoundaryPoint\n>");
			for (int i = 0; i < boundaryPoints.size(); i++) {
				for (int j = 0; j < indent+2; j++)
					for (int k = 0; k < indentSize; k++)
						stb.append(" ");
				stb.append(boundaryPoints.get(i).toString()+"\n");
			}
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("</BoundaryPoint\n>");
		}
		if (centroidOfArea != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CentroidOfArea>"+centroidOfArea+"</CentroidOfArea>\n");
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
			stb.append("<CreationTime>"+creationTime+"</CreationTime>\n");
		}
		if (creatorId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreatorId>"+creatorId+"</CreatorId>\n");
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</StopArea>\n");
		return stb.toString();
	}
}
