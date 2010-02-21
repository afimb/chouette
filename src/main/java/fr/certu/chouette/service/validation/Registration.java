package fr.certu.chouette.service.validation;

public class Registration {
	
	private Company 			company;
	private PTNetwork 			pTNetwork;
	private StopAreaExtension 	stopAreaExtension;
	private Line 				line;
	private JourneyPattern 		journeyPattern;
	private String 				registrationNumber;
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setPTNetwork(PTNetwork pTNetwork) {
		this.pTNetwork = pTNetwork;
	}
	
	public PTNetwork getPTNetwork() {
		return pTNetwork;
	}
	
	public void setStopAreaExtension(StopAreaExtension stopAreaExtension) {
		this.stopAreaExtension = stopAreaExtension;
	}
	
	public StopAreaExtension getStopAreaExtension() {
		return stopAreaExtension;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setJourneyPattern(JourneyPattern journeyPattern) {
		this.journeyPattern = journeyPattern;
	}
	
	public JourneyPattern getJourneyPattern() {
		return journeyPattern;
	}
	
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Registration>"+registrationNumber+"</Registration>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Registration>"+registrationNumber+"</Registration>\n");
		return stb.toString();
	}
}
