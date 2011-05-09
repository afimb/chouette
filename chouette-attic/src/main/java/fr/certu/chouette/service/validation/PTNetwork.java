package fr.certu.chouette.service.validation;

import org.exolab.castor.types.Date;

public class PTNetwork {
	
	private ChouettePTNetwork 		chouettePTNetwork;
	private String 					comment;
	private java.util.Date 			creationTime;
	private String 					creatorId;
	private String 					description;
	private String 					name;
	private String					sourceName;
	private String					sourceIdentifier;
	private String 					objectId;
	private boolean 				hasObjectVersion 		= false;
	private int 					objectVersion;
	private Registration 			registration;
	private PTNetworkSourceType 	pTNetworkSourceType;
	private Date 					versionDate;
	private String[]				lineIds;
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
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
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String getSourceName() {
		return sourceName;
	}
	
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}
	
	public String getSourceIdentifier() {
		return sourceIdentifier;
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
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setPTNetworkSourceType(PTNetworkSourceType pTNetworkSourceType) {
		this.pTNetworkSourceType = pTNetworkSourceType;
	}
	
	public PTNetworkSourceType getPTNetworkSourceType() {
		return pTNetworkSourceType;
	}
	
	public void setVersionDate(Date  versionDate) {
		this.versionDate = versionDate;
	}
	
	public Date getVersiondate() {
		return versionDate;
	}
	
	public void setLineIds(String[] lineIds) {
		this.lineIds = lineIds;
	}
	
	public String[] getLineIds() {
		return lineIds;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<PTNetwork>\n");
		stb.append("<Name>"+name+"</Name>\n");
		if (registration != null)
			stb.append(registration.toString());
		if (sourceName != null)
			stb.append("<SourceName>"+sourceName+"</SourceName>\n");
		if (sourceIdentifier != null)
			stb.append("<SourceIdentifier>"+sourceIdentifier+"</SourceIdentifier>\n");
		if (pTNetworkSourceType != null)
			stb.append("<SourceType>"+pTNetworkSourceType+"</SourceType>\n");
		if (lineIds != null)
			if (lineIds.length > 0) {
				stb.append("<LineId>\n");
				for (int i = 0; i < lineIds.length; i++)
					stb.append(lineIds[i]);
				stb.append("</LineId>\n");
			}
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<VersionDate>"+versionDate+"</VersionDate>\n");
		if (description != null)
			stb.append("<Description>"+description+"</Description>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</PTNetwork>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<PTNetwork>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		if (registration != null)
			stb.append(registration.toString(indent+1, indentSize));
		if (sourceName != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<SourceName>"+sourceName+"</SourceName>\n");
		}
		if (sourceIdentifier != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<SourceIdentifier>"+sourceIdentifier+"</SourceIdentifier>\n");
		}
		if (pTNetworkSourceType != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<SourceType>"+pTNetworkSourceType+"</SourceType>\n");
		}
		if (lineIds != null)
			if (lineIds.length > 0) {
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
		stb.append("<VersionDate>"+versionDate+"</VersionDate>\n");
		if (description != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Description>"+description+"</Description>\n");
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
		stb.append("</PTNetwork>\n");
		return stb.toString();
	}
}
