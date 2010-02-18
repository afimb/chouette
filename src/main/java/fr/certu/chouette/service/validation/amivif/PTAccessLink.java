package fr.certu.chouette.service.validation.amivif;

public class PTAccessLink extends ConnectionLink {
	
	private String	pTAccessLinkComment; // 0..1
	
	public void setPTAccessLinkComment(String pTAccessLinkComment) {
		this.pTAccessLinkComment = pTAccessLinkComment;
	}
	
	public String getPTAccessLinkComment() {
		return pTAccessLinkComment;
	}
}
