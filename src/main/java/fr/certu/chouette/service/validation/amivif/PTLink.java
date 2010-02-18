package fr.certu.chouette.service.validation.amivif;

public class PTLink extends Link {
	
	private StopPoint			startOfLink;		// 1
	private StopPoint			endOfLink;			// 1
	private String				comment;			// 0..1
	
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
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
}
