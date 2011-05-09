package fr.certu.chouette.service.validation;

import java.math.BigDecimal;
import java.util.Date;

public class AreaCentroid {
	
	private ChouetteArea 	chouetteArea;
	private Address 		address;
	private String 			comment;
	private Date 			creationTime;
	private String 			creatorId;
	private BigDecimal 		latitude;
	private BigDecimal 		longitude;
	private LongLatType 	longLatType;
	private String 			name;
	private String 			objectId;
	private boolean 		hasObjectVersion 	= false;
	private int 			objectVersion;
	private ProjectedPoint 	projectedPoint;
	private String 			containedIn;
	private StopArea 		containerStopArea;
	
	public void setChouetteArea(ChouetteArea chouetteArea) {
		this.chouetteArea = chouetteArea;
	}
	
	public ChouetteArea getChouetteArea() {
		return chouetteArea;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
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
	
	public void setLongLatType(LongLatType longLatType) {
		this.longLatType = longLatType;
	}
	
	public LongLatType getLongLatType() {
		return longLatType;
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
	
	public void setProjectedPoint(ProjectedPoint projectedPoint) {
		this.projectedPoint = projectedPoint;
	}
	
	public ProjectedPoint getProjectedPoint() {
		return projectedPoint;
	}
	
	public void setContainedIn(String containedIn) {
		this.containedIn = containedIn;
	}
	
	public String getContainedIn() {
		return containedIn;
	}
	
	public void setContainerStopArea(StopArea containerStopArea) {
		this.containerStopArea = containerStopArea;
	}
	
	public StopArea getContainerStopArea() {
		return containerStopArea;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<AreaCentroid>\n");
		stb.append("<Name>"+name+"</Name>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<Longitude>"+longitude.toString()+"</Longitude>\n");
		stb.append("<Latitude>"+latitude.toString()+"</Latitude>\n");
		stb.append("<LongLatType>"+longLatType.toString()+"</LongLatType>\n");
		if (address != null)
			stb.append(address.toString());
		if (projectedPoint != null)
			stb.append(projectedPoint.toString());
		stb.append("<ContainedIn>"+containedIn+"</ContainedIn>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</AreaCentroid>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<AreaCentroid>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		if (comment != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Longitude>"+longitude.toString()+"</Longitude>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Latitude>"+latitude.toString()+"</Latitude>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<LongLatType>"+longLatType.toString()+"</LongLatType>\n");
		if (address != null)
			stb.append("<Address>"+address.toString(indent+1, indentSize)+"</Address>\n");
		if (projectedPoint != null)
			stb.append(projectedPoint.toString(indent+1, indentSize));
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ContainedIn>"+containedIn+"</ContainedIn>\n");
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
		stb.append("</AreaCentroid>\n");
		return stb.toString();
	}
}
