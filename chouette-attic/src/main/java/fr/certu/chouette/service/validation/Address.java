package fr.certu.chouette.service.validation;

public class Address {
	
	private String 			countryCode;
	private String 			streetName;
	private AreaCentroid 	areaCentroid;
	private StopPoint 		stopPoint;
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	
	public String getStreetName() {
		return streetName;
	}
	
	public void setAreaCentroid(AreaCentroid areaCentroid) {
		this.areaCentroid = areaCentroid;
	}
	
	public AreaCentroid getAreaCentroid() {
		return areaCentroid;
	}
	
	public void setStopPoint(StopPoint stopPoint) {
		this.stopPoint = stopPoint;
	}
	
	public StopPoint getStopPoint() {
		return stopPoint;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Address>\n");
		stb.append("<CountryCode>"+countryCode+"</CountryCode>\n");
		stb.append("<StreetName>"+streetName+"</StreetName>\n");
		stb.append("</Address>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Address>\n");
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<CountryCode>"+countryCode+"</CountryCode>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StreetName>"+streetName+"</StreetName>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Address>\n");
		return stb.toString();
	}
}
