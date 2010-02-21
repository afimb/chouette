package fr.certu.chouette.service.validation;

public class StopAreaExtension {
	
	private StopArea 			stopArea;
	private ChouetteAreaType 	chouetteAreaType;
	private int 				fareCode;
	private boolean 			hasFareCode 		= false;
	private String 				nearestTopicName;
	private Registration 		registration;
	
	public void setStopArea(StopArea stopArea) {
		this.stopArea = stopArea;
	}
	
	public StopArea getStopArea() {
		return stopArea;
	}
	
	public void setType(ChouetteAreaType chouetteAreaType) {
		this.chouetteAreaType = chouetteAreaType;
	}
	
	public ChouetteAreaType getType() {
		return chouetteAreaType;
	}
	
	public void setFareCode(int fareCode) {
		this.fareCode = fareCode;
		if (fareCode >= 0)
			hasFareCode = true;
		else
			hasFareCode = false;
	}
	
	public int getFareCode() {
		return fareCode;
	}
	
	public boolean hasFareCode() {
		return hasFareCode;
	}
	
	public void setNearestTopicName(String nearestTopicName) {
		this.nearestTopicName = nearestTopicName;
	}
	
	public String getNearestTopicName() {
		return nearestTopicName;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<StopAreaExtension>\n");
		stb.append("<AreaType>"+chouetteAreaType.toString()+"</AreaType>\n");
		if (nearestTopicName != null)
			stb.append("<NearestTopicName>"+nearestTopicName+"</NearestTopicName>\n");
		if (hasFareCode)
			stb.append("<FareCode>"+fareCode+"</FareCode>\n");
		if (registration != null)
			stb.append(registration.toString());
		stb.append("</StopAreaExtension>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StopAreaExtension>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<AreaType>"+chouetteAreaType.toString()+"</AreaType>\n");
		if (nearestTopicName != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<NearestTopicName>"+nearestTopicName+"</NearestTopicName>\n");
		}
		if (hasFareCode) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<FareCode>"+fareCode+"</FareCode>\n");
		}
		if (registration != null)
			stb.append(registration.toString(indent+1, indentSize));
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</StopAreaExtension>\n");
		return stb.toString();
	}
}
