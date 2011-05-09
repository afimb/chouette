package fr.certu.chouette.service.validation;

import org.exolab.castor.types.Time;

public class TimeSlot {
	
	private ChouettePTNetwork 	chouettePTNetwork;
	private Time 				beginningSlotTime;
	private java.util.Date 		creationTime;
	private String 				creatorId;
	private Time 				endSlotTime;
	private Time 				firstDepartureTimeInSlot;
	private Time 				lastDepartureTimeInSlot;
	private String 				objectId;
	private boolean 			hasObjectVersion = false;
	private int 				objectVersion;
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
	}
	
	public void setBeginningSlotTime(Time beginningSlotTime) {
		this.beginningSlotTime = beginningSlotTime;
	}
	
	public Time getBeginningSlotTime() {
		return beginningSlotTime;
	}
	
	public void setCreationTime(java.util.Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public java.util.Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void setEndSlotTime(Time endSlotTime) {
		this.endSlotTime = endSlotTime;
	}
	
	public Time getEndSlotTime() {
		return endSlotTime;
	}
	
	public void setFirstDepartureTimeInSlot(Time firstDepartureTimeInSlot) {
		this.firstDepartureTimeInSlot = firstDepartureTimeInSlot;
	}
	
	public Time getFirstDepartureTimeInSlot() {
		return firstDepartureTimeInSlot;
	}
	
	public void setLastDepartureTimeInSlot(Time lastDepartureTimeInSlot) {
		this.lastDepartureTimeInSlot = lastDepartureTimeInSlot;
	}
	
	public Time getLastDepartureTimeInSlot() {
		return lastDepartureTimeInSlot;
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
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<TimeSlot>\n");
		stb.append("<BeginningSlotTime>"+beginningSlotTime+"</BeginningSlotTime>\n");
		stb.append("<EndSlotTime>"+endSlotTime+"</EndSlotTime>\n");
		stb.append("<FirstDepartureTimeInSlot>"+firstDepartureTimeInSlot+"</FirstDepartureTimeInSlot>\n");
		stb.append("<LastDepartureTimeInSlot>"+lastDepartureTimeInSlot+"</LastDepartureTimeInSlot>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</TimeSlot>\n");
		return stb.toString();
	}

	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<TimeSlot>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<BeginningSlotTime>"+beginningSlotTime+"</BeginningSlotTime>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<EndSlotTime>"+endSlotTime+"</EndSlotTime>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<FirstDepartureTimeInSlot>"+firstDepartureTimeInSlot+"</FirstDepartureTimeInSlot>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<LastDepartureTimeInSlot>"+lastDepartureTimeInSlot+"</LastDepartureTimeInSlot>\n");		
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
		stb.append("</TimeSlot>\n");
		return stb.toString();
	}
}
