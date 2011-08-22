/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;

/**
 * Agencies provides transit data in the feed.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsAgency {

    @Getter
    @Setter
    /**
     * Uniquely identifies a transit agency.
     * <b>Optionnal</b>: No need to give it for data from a single agency
     */
    private String agencyId;
    @Getter
    @Setter
    /**
     * Full name for a transit agency.
     * <b>Required</b> : An agency must have a name.
     */
    private String agencyName;
    @Getter
    @Setter
    /**
     * The URL of the agency.
     * <b>Required</b>: An agency must have a valid URL.
     */
    private URL agencyURL;
    @Getter
    @Setter
    /**
     * The timezone where the transit agency is located.
     * <b>Required</b>: The timezone of the agency location must be set to a valid value.
     */
    private TimeZone agencyTimezone;
    @Getter
    @Setter
    /**
     * A two-letter ISO 639-1 code for the primary language used by this transit agency.
     * <b>Optional</b>: No need to specify the primary language.
     */
    private String agencyLang = null;
    @Getter
    @Setter
    /**
     * A single voice telephone number for the specified agency.
     * <b>Optional</b>: No need to give the agency phone.
     */
    private String agencyPhone = null;
    @Getter
    @Setter
    /**
     * The set of routes that the transit agency runs.
     * In the <b>General Transit Feed Specification</b>, a GtfsRoute can belong to only one Agancy.
     */
    private Set<GtfsRoute> routes;

    public URL setAgencyURLFromString(String url_string) {
        try {
            setAgencyURL(new URL(url_string));
        } catch (Exception e) {
            return null;
        }
        return agencyURL;
    }

    public boolean setAgencyTimeZoneFromString(String agencyTimezone_string) {
        if (agencyTimezone_string == null) {
            setAgencyTimezone(TimeZone.getDefault());
            return false;
        }
        agencyTimezone_string = agencyTimezone_string.trim();
        String[] ids = TimeZone.getAvailableIDs();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                if (agencyTimezone_string.equals(ids[i])) {
                    setAgencyTimezone(TimeZone.getTimeZone(agencyTimezone_string));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean setAgencyLangFromString(String agencyLang) {
        if (agencyLang == null) {
            setAgencyLang(null);
            return false;
        }
        agencyLang = agencyLang.trim();
        String[] languages = Locale.getISOLanguages();
        if (languages == null) {
            setAgencyLang(null);
            return false;
        }
        for (int i = 0; i < languages.length; i++) {
            if (agencyLang.equals(languages[i])) {
                setAgencyLang(agencyLang);
                return true;
            }
        }
        setAgencyLang(null);
        return false;
    }

    public boolean setAgencyPhoneFromString(String agencyPhone) {
        if (agencyPhone == null) {
            setAgencyPhone(null);
            return false;
        }
        agencyPhone = agencyPhone.trim();
        if (agencyPhone.length() == 0) {
            setAgencyPhone(null);
            return false;
        }
        for (int i = 0; i < agencyPhone.length(); i++) {
            char ch = agencyPhone.charAt(i);
            if (ch != '.' && ch != '_' && ch != ',' && (ch < '0' || ch > '9')
                    && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
                setAgencyPhone(null);
                return false;
            }
        }
        setAgencyPhone(agencyPhone);
        return true;
    }

    public boolean addRoute(GtfsRoute route) {
        if (route == null) {
            return false;
        }
        if (routes == null) {
            routes = new HashSet<GtfsRoute>();
        }
        if (route.getAgency() != this) {
            route.setAgency(this);
        }
        return routes.add(route);
    }

    public boolean removeRoute(GtfsRoute route) {
        if (route == null) {
            return false;
        }
        if (route.getAgency() == this) {
            route.setAgency(null);
        }
        if (routes == null) {
            return false;
        }
        return routes.remove(route);
    }

    @Override
    public String toString() {
        String csvLine = "";
        if (agencyId != null) {
            csvLine += agencyId;
        }
        csvLine += ",";
        if (agencyName != null) {
            csvLine += agencyName;
        }
        csvLine += ",";
        if (agencyURL != null) {
            csvLine += agencyURL.toString();
        }
        csvLine += ",";
        if (agencyTimezone != null) {
            csvLine += agencyTimezone.getID();
        }
        csvLine += ",";
        if (agencyLang != null) {
            csvLine += agencyLang;
        }
        csvLine += ",";
        if (agencyPhone != null) {
            csvLine += agencyPhone;
        }
        csvLine += "\n";
        return csvLine;
    }
}
