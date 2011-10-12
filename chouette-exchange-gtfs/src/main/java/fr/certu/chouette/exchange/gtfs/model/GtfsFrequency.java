/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author michel
 *
 */
@NoArgsConstructor

public class GtfsFrequency extends GtfsBean
{
	@Getter @Setter private String tripId;
	@Getter @Setter private GtfsTime startTime ;
	@Getter @Setter private GtfsTime endTime;
	@Getter @Setter private int headwaySecs;
	@Getter @Setter private GtfsTrip trip;
	@Getter @Setter private boolean exactTimes;

	public static final String header = "trip_id,start_time,end_time,headway_secs"; //,exact_times";
	
	public String getCSVLine() {
		String csvLine = tripId + ",";
		csvLine += startTime;
		csvLine += ",";
		csvLine += endTime;
		csvLine += ",";
		csvLine += headwaySecs;
		return csvLine;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((tripId == null) ? 0 : tripId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GtfsFrequency)) {
			return false;
		}
		GtfsFrequency other = (GtfsFrequency) obj;
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		if (tripId == null) {
			if (other.tripId != null) {
				return false;
			}
		} else if (!tripId.equals(other.tripId)) {
			return false;
		}
		return true;
	}


}
