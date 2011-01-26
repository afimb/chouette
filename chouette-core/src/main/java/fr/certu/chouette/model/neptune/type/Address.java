package fr.certu.chouette.model.neptune.type;

import lombok.Getter;
import lombok.Setter;

public class Address {
	@Getter @Setter private String streetName;
	@Getter @Setter private String countryCode;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("streetName=").append(streetName).append(" contryCode=").append(countryCode);
		return sb.toString();
	}
}
