/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * A set of dates and at most one period for one or more routes.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsCalendar {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public static int MONDAY = 1;
    public static int TUESDAY = 2;
    public static int WEDNESDAY = 4;
    public static int THURSDAY = 8;
    public static int FRIDAY = 16;
    public static int SATURDAY = 32;
    public static int SUNDAY = 64;
    @Getter
    @Setter
    /**
     * Uniquely identifies a set of dates and at most one period for one or more routes.
     * <b>Required</b> : Each Calndar must have an ID.
     * A date can only be included or exluded.
     */
    private String serviceId;
    @Getter
    @Setter
    /**
     * The included dates
     * <b>Optional</b>: No need to be specified.
     */
    private Set<Date> includedDates;
    @Getter
    @Setter
    /**
     * The excluded dates
     * <b>Optional</b>: No need to be specified.
     */
    private Set<Date> excludedDates;
    @Getter
    @Setter
    /**
     * The period of this calendar
     * <b>Optional</b>: No need to be specified.
     */
    private Period period;
    @Getter
    @Setter
    /**
     * The set of Trips for which this GtfsCalendar is valid.
     */
    private Set<GtfsTrip> trips;

    public boolean addIncludedDate(Date date) {
        if (date == null) {
            return false;
        }
        if (includedDates == null) {
            includedDates = new HashSet<Date>();
        }
        return includedDates.add(date);
    }

    public boolean addIncludedDate(String date) {
        Date theDate = null;
        try {
            theDate = sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return addIncludedDate(theDate);
    }

    public boolean removeIncludedDate(Date date) {
        if (date == null) {
            return false;
        }
        if (includedDates == null) {
            return false;
        }
        return includedDates.remove(date);
    }

    public boolean removeIncludedDate(String date) {
        Date theDate = null;
        try {
            theDate = sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return removeIncludedDate(theDate);
    }

    public boolean addExcludedDate(Date date) {
        if (date == null) {
            return false;
        }
        if (excludedDates == null) {
            excludedDates = new HashSet<Date>();
        }
        return excludedDates.add(date);
    }

    public boolean addExcludedDate(String date) {
        Date theDate = null;
        try {
            theDate = sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return addExcludedDate(theDate);
    }

    public boolean removeExcludedDate(Date date) {
        if (date == null) {
            return false;
        }
        if (excludedDates == null) {
            return false;
        }
        return excludedDates.remove(date);
    }

    public boolean removeExcludedDate(String date) {
        Date theDate = null;
        try {
            theDate = sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return removeExcludedDate(theDate);
    }

    public boolean addTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trip.getCalendar() != this) {
            trip.setCalendar(this);
        }
        if (trips == null) {
            trips = new HashSet<GtfsTrip>();
        }
        return trips.add(trip);
    }

    public boolean removeTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trips == null) {
            return false;
        }
        return trips.remove(trip);
    }

    public String toStringDates() {
        String csvLine = "";
        if (includedDates != null) {
            for (Date date : includedDates) {
                if (serviceId != null) {
                    csvLine += serviceId;
                }
                csvLine += ",";
                String theDate = "";
                try {
                    theDate = sdf.format(date);
                } catch (Exception e) {
                    //date is not in a correct format
                }
                csvLine += theDate + ",1\n";
            }
        }
        if (excludedDates != null) {
            for (Date date : excludedDates) {
                if (serviceId != null) {
                    csvLine += serviceId;
                }
                csvLine += ",";
                String theDate = "";
                try {
                    theDate = sdf.format(date);
                } catch (Exception e) {
                    //date is not in a correct format
                }
                csvLine += theDate + ",2\n";
            }
        }
        return csvLine;
    }

    public String toStringPeriod() {
        String csvLine = "";
        if (period != null) {
            csvLine += period.toString();
        }
        return csvLine;
    }

    public class Period {

        @Getter
        @Setter
        /**
         * The period start date
         */
        private Date startDate;
        @Getter
        @Setter
        /**
         * The period end date
         */
        private Date endDate;
        @Getter
        @Setter
        /**
         * Encoding of valid days in the week
         */
        private int validDays;

        public void addValidDay(int day) {
            if (day == MONDAY) {
                validDays += (((validDays & MONDAY) == MONDAY) ? 0 : MONDAY);
            } else if (day == TUESDAY) {
                validDays += (((validDays & TUESDAY) == TUESDAY) ? 0 : TUESDAY);
            } else if (day == WEDNESDAY) {
                validDays += (((validDays & WEDNESDAY) == WEDNESDAY) ? 0 : WEDNESDAY);
            } else if (day == THURSDAY) {
                validDays += (((validDays & THURSDAY) == THURSDAY) ? 0 : THURSDAY);
            } else if (day == FRIDAY) {
                validDays += (((validDays & FRIDAY) == FRIDAY) ? 0 : FRIDAY);
            } else if (day == SATURDAY) {
                validDays += (((validDays & SATURDAY) == SATURDAY) ? 0 : SATURDAY);
            } else if (day == SUNDAY) {
                validDays += (((validDays & SUNDAY) == SUNDAY) ? 0 : SUNDAY);
            }
        }

        @Override
        public String toString() {
            String csvLine = "";
            if (getServiceId() != null) {
                csvLine += getServiceId();
            }
            csvLine += ",";
            csvLine += (((validDays & MONDAY) == MONDAY) ? "1," : "0,");
            csvLine += (((validDays & TUESDAY) == TUESDAY) ? "1," : "0,");
            csvLine += (((validDays & WEDNESDAY) == WEDNESDAY) ? "1," : "0,");
            csvLine += (((validDays & THURSDAY) == THURSDAY) ? "1," : "0,");
            csvLine += (((validDays & FRIDAY) == FRIDAY) ? "1," : "0,");
            csvLine += (((validDays & SATURDAY) == SATURDAY) ? "1," : "0,");
            csvLine += (((validDays & SUNDAY) == SUNDAY) ? "1," : "0,");
            String theDate = "";
            try {
                theDate = sdf.format(startDate);
            } catch (Exception e) {
                //startOfDate is not in a correct format
            }
            csvLine += theDate + ",";
            try {
                theDate = sdf.format(endDate);
            } catch (Exception e) {
                //endOfDate is not in a correct format
            }
            csvLine += theDate + "\n";
            return csvLine;
        }
    }
}
