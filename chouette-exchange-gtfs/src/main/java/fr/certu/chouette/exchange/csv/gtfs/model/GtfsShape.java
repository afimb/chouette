/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Zakaria BOUZIANE
 */
public class GtfsShape {

    @Getter
    @Setter
    /**
     * uniquely identifies a GtfsShape.
     * <b>Required</b>: A GtfsShape must have an identifier.
     */
    private String shapeId;
    @Getter
    @Setter
    /**
     * 
     */
    private SortedMap<Integer, Point> points;
    @Getter
    @Setter
    private Set<GtfsTrip> trips;

    public Point addPoint(String shapePtSequence, String shapePtLat, String shapePtLon, String shapeDistTraveled) {
        if (shapePtSequence == null) {
            return null;
        }
        Integer shapePtSequenceInt = null;
        try {
            shapePtSequenceInt = new Integer(shapePtSequence);
        } catch (NumberFormatException e) {
            return null;
        }
        if (points == null) {
            points = new TreeMap<Integer, Point>();
        }
        return points.put(shapePtSequenceInt, new Point(shapePtLat, shapePtLon, shapeDistTraveled));
    }

    public Point addPoint(int shapePtSequence, BigDecimal shapePtLat, BigDecimal shapePtLon, BigDecimal shapeDistTraveled) {
        if (points == null) {
            points = new TreeMap<Integer, Point>();
        }
        return points.put(new Integer(shapePtSequence), new Point(shapePtLat, shapePtLon, shapeDistTraveled));
    }

    public Point removePoint(Integer shapePtSequence) {
        if (shapePtSequence == null) {
            return null;
        }
        if (points == null) {
            return null;
        }
        return points.remove(shapePtSequence);
    }

    public boolean addTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trips == null) {
            trips = new HashSet<GtfsTrip>();
        }
        if (trip.getShape() != this) {
            trip.setShape(this);
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
        if (trip.getShape() == this) {
            trip.setShape(null);
        }
        return trips.remove(trip);
    }

    @Override
    public String toString() {
        String csvLine = "";
        Set<Integer> shapePtSequences = points.keySet();
        for (Integer shapePtSequence : shapePtSequences) {
            Point point = points.get(shapePtSequence);
            if (point != null) {
                if (shapeId != null) {
                    csvLine += shapeId;
                }
                csvLine += ",";
                if (point.getShapePtLat() != null) {
                    csvLine += point.getShapePtLat();
                }
                csvLine += ",";
                if (point.getShapePtLon() != null) {
                    csvLine += point.getShapePtLon();
                }
                csvLine += ",";
                csvLine += shapePtSequence.intValue();
                csvLine += ",";
                if (point.getShapeDistTraveled() != null) {
                    csvLine += point.getShapeDistTraveled();
                }
                csvLine += "\n";
            }
        }
        return csvLine;
    }

    public class Point {

        @Getter
        @Setter
        /**
         * Point's latitude within this GtfsShape.
         * <b>Required</b>: A Point must have a latitude.
         */
        private BigDecimal shapePtLat;
        @Getter
        @Setter
        /**
         * Point's longitude within this GtfsShape.
         * <b>Required</b>: A Point must have a longitude.
         */
        private BigDecimal shapePtLon;
        @Getter
        @Setter
        /**
         * Positions a GtfsShape Point as a distance traveled along a shape from the first shape point.
         * Itrepresents a real distance traveled along the route in units such as feet or kilometers.
         * This information allows the trip planner to determine how much of the shape to draw when
         * showing part of a trip on the map. 
         * <b>Optional</b>: If present it must match the value of shapeDistTraveled from StopTimes
         */
        private BigDecimal shapeDistTraveled;

        public Point(BigDecimal shapePtLat, BigDecimal shapePtLon, BigDecimal shapeDistTraveled) {
            setShapePtLat(shapePtLat);
            setShapePtLon(shapePtLon);
            setShapeDistTraveled(shapeDistTraveled);
        }

        public Point(String shapePtLat, String shapePtLon, String shapeDistTraveled) {
            setShapePtLatFromString(shapePtLat);
            setShapePtLonFromString(shapePtLon);
            setShapeDistTraveledFromString(shapeDistTraveled);
        }

        private void setShapePtLatFromString(String shapePtLat) {
            try {
                setShapePtLat(new BigDecimal(shapePtLat));
            } catch (NumberFormatException e) {
                setShapePtLat(null);
            }
        }

        private void setShapePtLonFromString(String shapePtLon) {
            try {
                setShapePtLon(new BigDecimal(shapePtLon));
            } catch (NumberFormatException e) {
                setShapePtLon(null);
            }
        }

        private void setShapeDistTraveledFromString(String shapeDistTraveled) {
            try {
                setShapeDistTraveled(new BigDecimal(shapeDistTraveled));
            } catch (NumberFormatException e) {
                setShapeDistTraveled(null);
            }
        }
    }
}
