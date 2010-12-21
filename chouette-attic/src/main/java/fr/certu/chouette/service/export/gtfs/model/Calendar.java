/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author zbouziane
 */
public class Calendar {

    private String  serviceId;
    private boolean monday     = false;
    private boolean tuesday    = false;
    private boolean wednesday  = false;
    private boolean thursday   = false;
    private boolean friday     = false;
    private boolean saturday   = false;
    private boolean sunday     = false;
    private Date    startDate  = null;
    private Date    endDate    = null;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCSVLine() {
        String csvLine = serviceId + ",";
        csvLine += (monday ? "1," : "0,");
        csvLine += (tuesday ? "1," : "0,");
        csvLine += (wednesday ? "1," : "0,");
        csvLine += (thursday ? "1," : "0,");
        csvLine += (friday ? "1," : "0,");
        csvLine += (saturday ? "1," : "0,");
        csvLine += (sunday ? "1," : "0,");
        csvLine += sdf.format(startDate);
        csvLine += ",";
        csvLine += sdf.format(endDate);
        return csvLine;
    }
}
