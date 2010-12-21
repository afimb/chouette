package fr.certu.chouette.service.validation.amivif;

public class StopPoint extends Point {
	
	private String								name;																				// 1
	private String								lineIdShortcut;																		// 0..1
	private Line								line;																				// 0..1
	private String								ptNetworkIdShortcut;																// 0..1
	private TransportNetwork					transportNetwork;																	// 0..1
	private String								comment;																			// 0..1
	private String								codeUIC;																			// 0..1
	private int									upFareZone;																			// 0..1
	private int									downFareZone;
	
	public void setStopPoint(StopPoint stopPoint) {
		super.setPoint(stopPoint);
		this.setName(stopPoint.getName());
		this.setLineIdShortcut(stopPoint.getLineIdShortcut());
		this.setLine(stopPoint.getLine());
		this.setPTNetworkIdShortcut(stopPoint.getPTNetworkIdShortcut());
		this.setTransportNetwork(stopPoint.getTransportNetwork());
		this.setComment(stopPoint.getComment());
		this.setCodeUIC(stopPoint.getCodeUIC());
		this.setUpFareZone(stopPoint.getUpFareZone());
		this.setDownFareZone(stopPoint.getDownFareZone());
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLineIdShortcut(String lineIdShortcut) {
		this.lineIdShortcut = lineIdShortcut;
	}
	
	public String getLineIdShortcut() {
		return lineIdShortcut;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setPTNetworkIdShortcut(String ptNetworkIdShortcut) {
		this.ptNetworkIdShortcut = ptNetworkIdShortcut;
	}
	
	public String getPTNetworkIdShortcut() {
		return ptNetworkIdShortcut;
	}
	
	public void setTransportNetwork(TransportNetwork transportNetwork) {
		this.transportNetwork = transportNetwork;
	}
	
	public TransportNetwork getTransportNetwork() {
		return transportNetwork;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setCodeUIC(String codeUIC) {
		this.codeUIC = codeUIC;
	}
	
	public String getCodeUIC() {
		return codeUIC;
	}
	
	public void setUpFareZone(int upFareZone) {
		this.upFareZone = upFareZone;
	}
	
	public int getUpFareZone() {
		return upFareZone;
	}
	
	public void setDownFareZone(int downFareZone) {
		this.downFareZone = downFareZone;
	}
	
	public int getDownFareZone() {
		return downFareZone;
	}
}
