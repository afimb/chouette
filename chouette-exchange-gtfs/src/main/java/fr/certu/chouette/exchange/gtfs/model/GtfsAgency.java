/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.net.URL;
import java.util.TimeZone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor
public class GtfsAgency extends GtfsBean
{
	@Getter @Setter private String   agencyId;
	@Getter @Setter private String   agencyName;
	@Getter @Setter private URL      agencyURL;
	@Getter @Setter private TimeZone agencyTimezone;
	// optional items
	@Getter @Setter private String   agencyLang      = null;
	@Getter @Setter private String   agencyPhone     = null;

	public static final String header = "agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone";

	public String getCSVLine() {
		String csvLine = agencyId + "," + agencyName + "," +agencyURL.toString() + ',' + agencyTimezone.getID() + ",";
		if (agencyLang != null)
			csvLine += agencyLang;
		csvLine += ",";
		if (agencyPhone != null)
			csvLine += agencyPhone;
		return csvLine;
	}
}
