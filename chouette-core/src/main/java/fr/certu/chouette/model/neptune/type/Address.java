package fr.certu.chouette.model.neptune.type;

import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.StopArea;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Address for accesspoints or stopareas
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class Address 
{
	// constant for persistence fields

	/**
	 * address street name 
	 */
	@Getter @Setter private String streetName;
	/**
	 * address city or district code
	 */
	@Getter @Setter private String countryCode;
	
	public Address(StopArea area) 
	{
		this.streetName = area.getStreetName();
		this.countryCode = area.getCountryCode();
	}

	public Address(AccessPoint access) 
	{
		this.streetName = access.getStreetName();
		this.countryCode = access.getCountryCode();
	}

	public void populateStoparea(StopArea area)
	{
		area.setStreetName(streetName);
		area.setCountryCode(countryCode);
	}
	
	public void populateAccessPoint(AccessPoint access)
	{
		access.setStreetName(streetName);
		access.setCountryCode(countryCode);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("streetName=").append(streetName).append(" countryCode=").append(countryCode);
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (streetName == null) {
			if (other.streetName != null)
				return false;
		} else if (!streetName.equals(other.streetName))
			return false;
		return true;
	}
}
