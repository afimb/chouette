/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import chouette.schema.types.ChouetteAreaType;
import java.math.BigDecimal;
import java.net.URL;

/**
 *
 * @author zbouziane
 */
public class Stop {

    private String     stop_id;
    private String     stop_code      = null;
    private String     stop_name;
    private String     stop_desc      = null;
    private BigDecimal stop_lat;
    private BigDecimal stop_lon;
    private String     zone_id        = null;
    private URL        stop_url       = null;
    private int        location_type  = -1;
    private String     parent_station = null;

    public void setStopId(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStopId() {
        return stop_id;
    }

    public void setStopCode(String stop_code) {
        this.stop_code = stop_code;
    }

    public String getStopCode() {
        return stop_code;
    }

    public void setStopName(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getStopName() {
        return stop_name;
    }

    public void setStopDesc(String stop_desc) {
        this.stop_desc = stop_desc;
    }

    public String getStopDesc() {
        return stop_desc;
    }

    public void setStopLat(BigDecimal stop_lat) {
        this.stop_lat = stop_lat;
    }

    public BigDecimal getStopLat() {
        return stop_lat;
    }

    public void setStopLon(BigDecimal stop_lon) {
        this.stop_lon = stop_lon;
    }

    public BigDecimal getStopLon() {
        return stop_lon;
    }

    public void setZoneId(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getZoneId() {
        return zone_id;
    }

    public void setStopUrl(URL stop_url) {
        this.stop_url = stop_url;
    }

    public URL getStopUrl() {
        return stop_url;
    }

    public void setLocationType(int location_type) {
        this.location_type = location_type;
    }

    public int getLocationType() {
        return location_type;
    }

    public void setLocationType(ChouetteAreaType chouetteAreaType) {
        if (chouetteAreaType.compareTo(ChouetteAreaType.BOARDINGPOSITION) == 0)
            setLocationType(0);
        else if(chouetteAreaType.compareTo(ChouetteAreaType.QUAY) == 0)
            setLocationType(0);
        else if(chouetteAreaType.compareTo(ChouetteAreaType.COMMERCIALSTOPPOINT) == 0)
            setLocationType(1);
        else if(chouetteAreaType.compareTo(ChouetteAreaType.STOPPLACE) == 0)
            setLocationType(1);
    }

    public void setParentStation(String parent_station) {
        this.parent_station = parent_station;
    }

    public String getParentStation() {
        return parent_station;
    }

    public String getCSVLine() {
        String csvLine = stop_id + ",";
        if (stop_code != null)
            csvLine += stop_code;
        csvLine += "," + stop_name + ",";
        if (stop_desc != null)
            csvLine += stop_desc;
        csvLine += "," + stop_lat + "," + stop_lon + ",";
        if (zone_id != null)
            csvLine += zone_id;
        csvLine += ",";
        if (stop_url != null)
            csvLine += stop_url.toString();
        csvLine += ",";
        if (location_type != -1)
            csvLine += location_type;
        csvLine += ",";
        if (parent_station != null)
            csvLine += parent_station;
        return csvLine;
    }
}
