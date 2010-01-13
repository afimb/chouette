package fr.certu.chouette.service.validation.amivif;

import org.exolab.castor.types.Duration;

public class ConnectionLink extends Link {

	private StopPoint				startOfLink;							// 1
	private StopPoint				endOfLink;								// 1
	private StopPointInConnection	startOfLinkInConnection;				// 1
	private StopPointInConnection	endOfLinkInConnection;					// 1
	private ConnectionLinkType		connectionLinkType;						// 0..1
	private Duration				defaultDuration;						// 0..1
	private Duration				frequentTravellerDuration;				// 0..1
	private Duration				occasionalTravellerDuration;			// 0..1
	private Duration				mobilityRestrictedTravellerDuration;	// 0..1
	private boolean					mobilityRestrictedSuitability;			// 0..1
	private boolean					stairsAvailability;						// 0..1
	private boolean					liftAvailability;						// 0..1
	private String					comment;								// 0..1
	private boolean					display;								// 0..1
	
	public void setStartOfLink(StopPoint startOfLink) {
		this.startOfLink = startOfLink;
	}
	
	public StopPoint getStartOfLink() {
		return startOfLink;
	}
	
	public void setEndOfLink(StopPoint endOfLink) {
		this.endOfLink = endOfLink;
	}
	
	public StopPoint getEndOfLink() {
		return endOfLink;
	}
	
	public void setStartOfLinkInConnection(StopPointInConnection startOfLinkInConnection) {
		this.startOfLinkInConnection = startOfLinkInConnection;
	}
	
	public StopPointInConnection getStartOfLinkInConnection() {
		return startOfLinkInConnection;
	}
	
	public void setEndOfLinkInConnection(StopPointInConnection endOfLinkInConnection) {
		this.endOfLinkInConnection = endOfLinkInConnection;
	}
	
	public StopPointInConnection getEndOfLinkInConnection() {
		return endOfLinkInConnection;
	}
	
	public void setConnectionLinkType(ConnectionLinkType connectionLinkType) {
		this.connectionLinkType = connectionLinkType;
	}
	
	public ConnectionLinkType getConnectionLinkType() {
		return connectionLinkType;
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
	
	public void setOccasionalTravellerDuration(Duration occasionalTravellerDuration) {
		this.occasionalTravellerDuration = occasionalTravellerDuration;
	}
	
	public Duration getOccasionalTravellerDuration() {
		return occasionalTravellerDuration;
	}
	
	public void setMobilityRestrictedTravellerDuration(Duration mobilityRestrictedTravellerDuration) {
		this.mobilityRestrictedTravellerDuration = mobilityRestrictedTravellerDuration;
	}
	
	public Duration getMobilityRestrictedTravellerDuration() {
		return mobilityRestrictedTravellerDuration;
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
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	public boolean display() {
		return display;
	}
	
	public enum ConnectionLinkType {
		Underground,
		Overground,
		Mixed
	}
}
