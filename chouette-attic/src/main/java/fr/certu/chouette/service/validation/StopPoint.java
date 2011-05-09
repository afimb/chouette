package fr.certu.chouette.service.validation;

import java.math.BigDecimal;
import java.util.Date;

public class StopPoint {
	
	private ChouetteLineDescription 	chouetteLineDescription;
	private Address 					address;
	private LongLatType 				longLatType;
	private ProjectedPoint 				projectedPoint;
	private String 						comment;
	private Date 						creationTime;
	private String 						creatorId;
	private BigDecimal 					latitude;
	private BigDecimal 					longitude;
	private String 						name;
	private String 						objectId;
	private boolean 					hasObjectVersion			= false;
	private int 						objectVersion;
	private StopArea 					containedInStopArea 		= null;
	private String 						containedInStopAreaId;
	private String						lineIdShortcut;
	private Line						line;
	private String						ptNetworkIdShortcut;
	private PTNetwork					ptNetwork;
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setLongLatType(LongLatType longLatType) {
		this.longLatType = longLatType;
	}
	
	public LongLatType getLongLatType() {
		return longLatType;
	}
	
	public void setProjectedPoint(ProjectedPoint projectedPoint) {
		this.projectedPoint = projectedPoint;
	}
	
	public ProjectedPoint getProjectedPoint() {
		return projectedPoint;
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
	
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	public BigDecimal getLongitude() {
		return longitude;
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
	
	public void setContainedInStopArea(StopArea containedInStopArea) {
		this.containedInStopArea = containedInStopArea;
	}
	
	public StopArea getContainedInStopArea() {
		return containedInStopArea;
	}
	
	public void setContainedInStopAreaId(String containedInStopAreaId) {
		this.containedInStopAreaId = containedInStopAreaId;
	}
	
	public String getContainedInStopAreaId() {
		return containedInStopAreaId;
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
	
	public void setPtNetworkIdShortcut(String ptNetworkIdShortcut) {
		this.ptNetworkIdShortcut = ptNetworkIdShortcut;
	}
	
	public String getPtNetworkIdShortcut() {
		return ptNetworkIdShortcut;
	}
	
	public void setPtNetwork(PTNetwork ptNetwork) {
		this.ptNetwork = ptNetwork;
	}
	
	public PTNetwork getPtNetwork() {
		return ptNetwork;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<StopPoint>\n");
		stb.append("<Name>"+name+"</Name>\n");
		if (lineIdShortcut != null)
			stb.append("<LineIdShortcut>"+lineIdShortcut+"</LineIdShortcut>\n");
		if (ptNetworkIdShortcut != null)
			stb.append("<PtNetworkIdShortcut>"+ptNetworkIdShortcut+"</PtNetworkIdShortcut>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<Longitude>"+longitude+"</Longitude>\n");
		stb.append("<Latitude>"+latitude+"</Latitude>\n");
		stb.append("<LongLatType>"+longLatType+"</LongLatType>\n");
		if (address != null)
			stb.append(address.toString());
		if (projectedPoint != null)
			stb.append(projectedPoint.toString());
		stb.append("<ContainedIn>"+containedInStopAreaId+"</ContainedIn>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</StopPoint>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StopPoint>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		if (lineIdShortcut != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<LineIdShortcut>"+lineIdShortcut+"</LineIdShortcut>\n");
		}
		if (ptNetworkIdShortcut != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<PtNetworkIdShortcut>"+ptNetworkIdShortcut+"</PtNetworkIdShortcut>\n");
		}
		if (comment != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Longitude>"+longitude+"</Longitude>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Latitude>"+latitude+"</Latitude>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<LongLatType>"+longLatType+"</LongLatType>\n");
		if (address != null)
			stb.append(address.toString(indent+1, indentSize));
		if (projectedPoint != null)
			stb.append(projectedPoint.toString(indent+1, indentSize));
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ContainedIn>"+containedInStopAreaId+"</ContainedIn>\n");
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
		stb.append("</StopPoint>\n");
		return stb.toString();
	}
}
