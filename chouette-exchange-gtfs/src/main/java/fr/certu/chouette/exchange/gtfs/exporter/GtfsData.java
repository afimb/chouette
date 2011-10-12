/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsShape;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;

/**
 *
 */
public class GtfsData
{
   @Getter private List<GtfsAgency> agencies = new ArrayList<GtfsAgency>();
   @Getter private List<GtfsCalendar> calendars = new ArrayList<GtfsCalendar>();
   @Getter private List<GtfsCalendarDate> calendardates = new ArrayList<GtfsCalendarDate>();
   @Getter private List<GtfsFrequency> frequencies = new ArrayList<GtfsFrequency>();
   @Getter private List<GtfsRoute> routes = new ArrayList<GtfsRoute>();
   @Getter private List<GtfsShape> shapes = new ArrayList<GtfsShape>();
   @Getter private List<GtfsStop> stops = new ArrayList<GtfsStop>();
   @Getter private List<GtfsStopTime> stoptimes = new ArrayList<GtfsStopTime>();
   @Getter private List<GtfsTrip> trip = new ArrayList<GtfsTrip>();
   
}
