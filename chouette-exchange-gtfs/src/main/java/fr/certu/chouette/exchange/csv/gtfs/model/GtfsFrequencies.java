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
 * Represent schedules that don't have a fixed list of stop times.
 * When trips are defined in GtfsFrequencies, the trip planner ignores the absolute values of the 
 * arrivalTime and departureTime fields for those Trips in StopTimes. Instead, the StopTimes defines 
 * the sequence of Stops and the time difference between each Stop.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsFrequencies {

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    @Getter
    @Setter
    /**
     * Identifies a GtfsTrip on which the specified GtfsFrequencies of service apply.
     * <b>Required</b>: GtfsFrequencies are applied to Trips.
     */
    private GtfsTrip trip;
    @Getter
    @Setter
    /**
     * Schedules that don't have a fixed list of stop times.
     * <b>Required</b>: This list cannot be empty.
     */
    private Set<Frequency> frequencies;

    public boolean addFrequency(Date startTime, Date endTime, int headwaySecs) {
        if (startTime == null || endTime == null || headwaySecs <= 0) {
            return false;
        }
        if (frequencies == null) {
            frequencies = new HashSet<Frequency>();
        }
        return frequencies.add(new Frequency(startTime, endTime, headwaySecs));
    }

    public boolean addFrequency(String startTime, String endTime, String headwaySecs) {
        if (startTime == null || endTime == null || headwaySecs == null) {
            return false;
        }
        if (frequencies == null) {
            frequencies = new HashSet<Frequency>();
        }
        return frequencies.add(new Frequency(startTime, endTime, headwaySecs));
    }

    public boolean removeFrequency(Frequency frequency) {
        if (frequency == null) {
            return false;
        }
        if (frequencies == null) {
            return false;
        }
        return frequencies.remove(frequency);
    }

    @Override
    public String toString() {
        String csvLine = "";
        if (frequencies != null) {
            for (Frequency frequency : frequencies) {
                if (trip != null) {
                    if (trip.getTripId() != null) {
                        csvLine += trip.getTripId();
                    }
                }
                csvLine += ",";
                if (frequency.getStartTime() != null) {
                    csvLine += frequency.getStartTime();
                }
                csvLine += ",";
                if (frequency.getEndTime() != null) {
                    csvLine += frequency.getEndTime();
                }
                csvLine += ",";
                if (frequency.getHeadwaySecs() > 0) {
                    csvLine += frequency.getHeadwaySecs();
                }
                csvLine += "\n";
            }
        }
        return csvLine;
    }

    public class Frequency {

        @Getter
        @Setter
        /**
         * Specifies the time at which service begins with the specified frequency.
         * The time is measured from "noon minus 12h" (effectively midnight, except for days
         * on which daylight savings time changes occur) at the beginning of the service date.
         * For times occurring after midnight, enter the time as a value greater than 24:00:00
         * in HH:MM:SS local time for the day on which the trip schedule begins. E.g. 25:35:00.
         * <b>Required</b>: A Frequency must have a Start Time.
         */
        private Date startTime;
        @Getter
        @Setter
        /**
         * 
         * <b>Required</b>: A Frequency must have an End Time.
         */
        private Date endTime;
        @Getter
        @Setter
        /**
         * Indicates the time between departures from the same stop (headway) for this trip type,
         * during the time interval specified by startTime and endTime. The headway value must be
         * entered in seconds.
         * 
         * Periods in which headways are defined (the rows in frequencies.txt) shouldn't overlap for
         * the same trip, since it's hard to determine what should be inferred from two overlapping headways.
         * However, a headway period may begin at the exact same time that another one ends, for instance:
         * 
         * A, 05:00:00, 07:00:00, 600
         * B, 07:00:00, 12:00:00, 1200
         * 
         * <b>Required</b>: A Frequency must have a Headway.
         */
        private int headwaySecs;

        public Frequency(Date startTime, Date endTime, int headwaySecs) {
            setStartTime(startTime);
            setEndTime(endTime);
            setHeadwaySecs(headwaySecs);
        }

        public Frequency(String startTime, String endTime, String headwaySecs) {
            setStartTimeFromString(startTime);
            setEndTimeFromString(endTime);
            setHeadwaySecsFromString(headwaySecs);
        }

        private void setStartTimeFromString(String startTime) {
            try {
                setStartTime(sdf.parse(startTime));
            } catch (ParseException e) {
                setStartTime(null);
            }
        }

        private void setEndTimeFromString(String endTime) {
            try {
                setEndTime(sdf.parse(endTime));
            } catch (ParseException e) {
                setEndTime(null);
            }
        }

        private void setHeadwaySecsFromString(String headwaySecs) {
            try {
                setHeadwaySecs(Integer.parseInt(headwaySecs));
            } catch (NumberFormatException e) {
                setHeadwaySecs(-1);
            }
        }
    }
}
