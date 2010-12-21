package fr.certu.chouette.service.validation.amivif;

import java.math.BigDecimal;

public class Link extends LocationTridentObject {
	
	private String				name;				// 0..1
	private String				startOfLinkId;		// 1
	private String				endOfLinkId;		// 1
	private BigDecimal			linkDistance;		// 0..1
	
	public void setLink(Link link) {
		super.setLocationTridentObject(link);
		this.setName(link.getName());
		this.setStartOfLinkId(link.getStartOfLinkId());
		this.setEndOfLinkId(link.getEndOfLinkId());
		this.setLinkDistance(link.getLinkDistance());
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setStartOfLinkId(String startOfLinkId) {
		this.startOfLinkId = startOfLinkId;
	}
	
	public String getStartOfLinkId() {
		return startOfLinkId;
	}
	
	public void setEndOfLinkId(String endOfLinkId) {
		this.endOfLinkId = endOfLinkId;
	}
	
	public String getEndOfLinkId() {
		return endOfLinkId;
	}
	
	public void setLinkDistance(BigDecimal linkDistance) {
		this.linkDistance = linkDistance;
	}
	
	public BigDecimal getLinkDistance() {
		return linkDistance;
	}
}
