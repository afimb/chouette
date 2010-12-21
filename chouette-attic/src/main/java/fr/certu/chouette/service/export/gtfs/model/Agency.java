/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import java.net.URL;
import java.util.TimeZone;

/**
 *
 * @author zbouziane
 */
public class Agency {

    String   agencyId;
    String   agencyName;
    URL      agencyURL;
    TimeZone agencyTimezone;
    String   agencyLang      = null;
    String   agencyPhone     = null;

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void getAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyURL(URL agencyURL) {
        this.agencyURL = agencyURL;
    }

    public URL getAgencyURL() {
        return agencyURL;
    }

    public void setAgencyTimezone(TimeZone agencyTimezone) {
        this.agencyTimezone = agencyTimezone;
    }

    public TimeZone getAgencyTimezone() {
        return agencyTimezone;
    }

    public void setAgencyLang(String agencyLang) {
        this.agencyLang = agencyLang;
    }

    public String getAgencyLang() {
        return agencyLang;
    }

    public void setAgencyPhone(String agencyPhone) {
        this.agencyPhone = agencyPhone;
    }

    public String getAgencyPhone() {
        return agencyPhone;
    }

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
