package fr.certu.chouette.service.validation.amivif;

import java.util.Date;

public class AccessPoint extends Point {
	
	private String			name;							// 0..1
	private AccessPointType	accessPointType;				// 0..1
	private Date			openningTime;					// 0..1
	private Date			closingTime;					// 0..1
	private boolean			mobilityRestrictedSuitability;	// 0..1
	private boolean			stairsAvailability;				// 0..1
	private boolean			liftAvailability;				// 0..1
	private String			comment;						// 0..1
	private String			accessType;						// 1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAccessPointType(AccessPointType accessPointType) {
		this.accessPointType = accessPointType;
	}
	
	public AccessPointType getAccessPointType() {
		return accessPointType;
	}
	
	public void setOpenningTime(Date openningTime) {
		this.openningTime = openningTime;
	}
	
	public Date getOpenningTime() {
		return openningTime;
	}
	
	public void setClosingTime(Date closingTime) {
		this.closingTime = closingTime;
	}
	
	public Date getClosingTime() {
		return closingTime;
	}
	
	public void setMobilityRestrictedSuitability(boolean mobilityRestrictedSuitability) {
		this.mobilityRestrictedSuitability = mobilityRestrictedSuitability;
	}
	
	public boolean mobilityRestrictedSuitability() {
		return mobilityRestrictedSuitability;
	}
	
	public void setStairsAvailability(boolean stairsAvailability) {
		this.stairsAvailability = stairsAvailability;
	}
	
	public boolean stairsAvailability() {
		return stairsAvailability;
	}
	
	public void setLiftAvailability(boolean liftAvailability) {
		this.liftAvailability = liftAvailability;
	}
	
	public boolean liftAvailability() {
		return liftAvailability;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	
	public String getAccessType() {
		return accessType;
	}
	
	public enum AccessPointType {
		In,
		Out,
		InOut
	}
}
