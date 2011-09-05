package fr.certu.chouette.model.neptune.type;

import lombok.Getter;
import lombok.Setter;

/**
 * Address for accesspoints or stopareas
 * 
 * @author michel
 *
 */
public class Address 
{
	// constant for persistence fields
	public static final String COUNTRY_CODE="countryCode"; 
	public static final String STREET_NAME="streetName"; 

	/**
	 * address street name 
	 */
	@Getter @Setter private String streetName;
	/**
	 * address city or district code
	 */
	@Getter @Setter private String countryCode;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("streetName=").append(streetName).append(" contryCode=").append(countryCode);
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
