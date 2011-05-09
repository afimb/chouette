package fr.certu.chouette.service.validation;

import java.math.BigDecimal;
import java.util.Date;

import org.exolab.castor.types.Duration;

public class ConnectionLink {
	
	private ChouettePTNetwork 	chouettePTNetwork;
	private String 				comment;
	private Date 				creationTime;
	private String 				creatorId;
	private Duration 			defaultDuration;
	private Duration 			frequentTravellerDuration;
	private boolean 			liftAvailability;
	private BigDecimal 			linkDistance;
	private ConnectionLinkType 	linkType;
	private boolean 			mobilityRestrictedSuitability;
	private Duration 			mobilityRestrictedTravellerDuration;
	private String 				name;
	private String 				objectId;
	private boolean 			hasObjectVersion 						= false;
	private int 				objectVersion;
	private Duration 			occasionalTravellerDuration;
	private boolean 			stairsAvailability 						= false;	
	private String 				startOfLinkId;
	private String 				endOfLinkId;
	private StopArea 			startOfLink 							= null;
	private StopArea 			endOfLink 								= null;
	
	public void setStartOfLinkId(String startOfLinkId) {
		this.startOfLinkId = startOfLinkId;
	}
	
	public String getStartOfLinkId() {
		return startOfLinkId;
	}
	
	public void setStartOfLink(StopArea startOfLink) {
		this.startOfLink = startOfLink;
	}
	
	public StopArea getStartOfLink() {
		return startOfLink;
	}
	
	public void setEndOfLinkId(String endOfLinkId) {
		this.endOfLinkId = endOfLinkId;
	}
	
	public String getEndOfLinkId() {
		return endOfLinkId;
	}
	
	public void setEndOfLink(StopArea endOfLink) {
		this.endOfLink = endOfLink;
	}
	
	public StopArea getEndOfLink() {
		return endOfLink;
	}
	
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
	
	public void setDefaultDuration(Duration defaultDuration) {
		this.defaultDuration = defaultDuration;
	}
	
	public Duration getDefaultDuration() {
		return defaultDuration;
	}
	
	public void setFrequentTravellerDuration(Duration frequentTravellerDuration) {
		this.frequentTravellerDuration = frequentTravellerDuration;
	}
	
	public Duration getFrequentTravellerDuration() {
		return frequentTravellerDuration;
	}
	
	public void setLiftAvailability(boolean liftAvailability) {
		this.liftAvailability = liftAvailability;
	}
	
	public boolean liftAvailability() {
		return liftAvailability;
	}
	
	public void setLinkDistance(BigDecimal linkDistance) {
		this.linkDistance = linkDistance;
	}
	
	public BigDecimal getLinkDistance() {
		return linkDistance;
	}
	
	public void setLinkType(ConnectionLinkType linkType) {
		this.linkType = linkType;
	}
	
	public ConnectionLinkType getLinkType() {
		return linkType;
	}
	
	public void setMobilityRestrictedSuitability(boolean mobilityRestrictedSuitability) {
		this.mobilityRestrictedSuitability = mobilityRestrictedSuitability;
	}
	
	public boolean mobilityRestrictedSuitability() {
		return mobilityRestrictedSuitability;
	}
	
	public void setMobilityRestrictedTravellerDuration(Duration mobilityRestrictedTravellerDuration) {
		this.mobilityRestrictedTravellerDuration = mobilityRestrictedTravellerDuration;
	}
	
	public Duration getMobilityRestrictedTravellerDuration() {
		return mobilityRestrictedTravellerDuration;
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
	
	public void setOccasionalTravellerDuration(Duration occasionalTravellerDuration) {
		this.occasionalTravellerDuration = occasionalTravellerDuration;
	}

	public Duration getOccasionalTravellerDuration() {
		return occasionalTravellerDuration;
	}
	
	public void setStairsAvailability(boolean stairsAvailability) {
		this.stairsAvailability = stairsAvailability;
	}
	
	public boolean hasStairsAvailability() {
		return stairsAvailability;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<ConnectionLink>\n");
		if (linkType != null)
			stb.append("<LinkType>"+linkType+"</LinkType>\n");
		if (defaultDuration != null)
			stb.append("<DefaultDuration>"+defaultDuration.toString()+"</DefaultDuration>\n");
		if (frequentTravellerDuration != null)
			stb.append("<FrequentTravellerDuration>"+frequentTravellerDuration.toString()+"</FrequentTravellerDuration>\n");
		if (occasionalTravellerDuration != null)
			stb.append("<OccasionalTravellerDuration>"+occasionalTravellerDuration.toString()+"</OccasionalTravellerDuration>\n");
		if (mobilityRestrictedTravellerDuration != null)
			stb.append("<MobilityRestrictedTravellerDuration>"+mobilityRestrictedTravellerDuration.toString()+"</MobilityRestrictedTravellerDuration>\n");
		stb.append("<MobilityRestrictedSuitability>"+mobilityRestrictedSuitability+"</MobilityRestrictedSuitability>\n");
		stb.append("<StairsAvailability>"+stairsAvailability+"</StairsAvailability>\n");
		stb.append("<LiftAvailability>"+liftAvailability+"</LiftAvailability>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		if (name != null)
			stb.append("<Name>"+name+"</Name>\n");
		stb.append("<StartOfLink>"+startOfLink+"</StartOfLink>\n");
		stb.append("<EndOfLink>"+endOfLink+"</EndOfLink>\n");
		if (linkDistance != null)
			stb.append("<LinkDistance>"+linkDistance+"</LinkDistance>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</ConnectionLink>\n");
		return stb.toString();
	}

	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ConnectionLink>\n");
		if (linkType != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<LinkType>"+linkType+"</LinkType>\n");
		}
		if (defaultDuration != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<DefaultDuration>"+defaultDuration.toString()+"</DefaultDuration>\n");
		}
		if (frequentTravellerDuration != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<FrequentTravellerDuration>"+frequentTravellerDuration.toString()+"</FrequentTravellerDuration>\n");
		}
		if (occasionalTravellerDuration != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<OccasionalTravellerDuration>"+occasionalTravellerDuration.toString()+"</OccasionalTravellerDuration>\n");
		}
		if (mobilityRestrictedTravellerDuration != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<MobilityRestrictedTravellerDuration>"+mobilityRestrictedTravellerDuration.toString()+"</MobilityRestrictedTravellerDuration>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<MobilityRestrictedSuitability>"+mobilityRestrictedSuitability+"</MobilityRestrictedSuitability>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StairsAvailability>"+stairsAvailability+"</StairsAvailability>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<LiftAvailability>"+liftAvailability+"</LiftAvailability>\n");
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
		stb.append("<StartOfLink>"+startOfLink+"</StartOfLink>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<EndOfLink>"+endOfLink+"</EndOfLink>\n");
		if (linkDistance != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<LinkDistance>"+linkDistance+"</LinkDistance>\n");
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
		stb.append("</ConnectionLink>\n");
		return stb.toString();
	}
}
