package fr.certu.chouette.service.validation;

import java.util.Date;

public class GroupOfLine {
	
	private ChouettePTNetwork 	chouettePTNetwork;
	private String 				comment;
	private Date 				creationTime;
	private String 				creatorId;
	private String 				name;
	private String 				objectId;
	private boolean 			hasObjectVersion 	= false;
	private int 				objectVersion;
	private String[]			lineIds;
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
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
	
	public void setLineIds(String[] lineIds) {
		this.lineIds = lineIds;
	}
	
	public String[] getLineIds() {
		return lineIds;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<GroupOfLine>\n");
		stb.append("<Name>"+name+"</Name>\n");
		stb.append("<LineId>\n");
		for (int i = 0; i < lineIds.length; i++)
			stb.append(lineIds[i]);
		stb.append("</LineId>\n");		
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</GroupOfLine>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<GroupOgLine>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<LineId>\n");
		for (int i = 0; i < lineIds.length; i++) {
			for (int j = 0; j < indent+2; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append(lineIds[i]);
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</LineId>\n");
		if (comment != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
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
		stb.append("</GroupOgLine>\n");
		return stb.toString();
	}
}
