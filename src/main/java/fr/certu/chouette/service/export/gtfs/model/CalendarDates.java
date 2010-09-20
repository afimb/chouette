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
public class CalendarDates {

    private String serviceId;
    private Date   date;
    private int    exceptionType = 1;

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setExceptionType(int exceptionType) {
        this.exceptionType = exceptionType;
    }

    public int getExceptionType() {
        return exceptionType;
    }

    public String getCSVLine() {
        String csvLine = serviceId + "," + Calendar.sdf.format(date) + "," +exceptionType;
        return csvLine;
    }
}
