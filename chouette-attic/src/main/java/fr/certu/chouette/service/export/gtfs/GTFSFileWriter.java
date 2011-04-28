package fr.certu.chouette.service.export.gtfs;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import chouette.schema.types.DayTypeType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.export.gtfs.model.Agency;
import fr.certu.chouette.service.export.gtfs.model.Calendar;
import fr.certu.chouette.service.export.gtfs.model.CalendarDates;
import fr.certu.chouette.service.export.gtfs.model.Route;
import fr.certu.chouette.service.export.gtfs.model.Stop;
import fr.certu.chouette.service.export.gtfs.model.StopTimes;
import fr.certu.chouette.service.export.gtfs.model.Trip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.apache.log4j.Logger;

public class GTFSFileWriter implements IGTFSFileWriter {

    private static final Logger logger         = Logger.getLogger(fr.certu.chouette.service.export.gtfs.GTFSFileWriter.class);
    private static final String JEU_CARACTERES = "UTF-8";
    private Map<String, List<Trip>> tripsByCourseId = new HashMap<String, List<Trip>>();
        
    public GTFSFileWriter() {
    }

    public void ecrire(ChouettePTNetworkTypeType chouette, File file) {
        FileOutputStream   fileOutputStream   = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream   = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, JEU_CARACTERES);
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch(IOException e) {
            throw new ServiceException(CodeIncident.ERR_XML_ECRITURE, e);
        }
        finally {
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                }
                catch(IOException e) {
                    throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch(IOException e) {
                    throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
                }
            }
        }
    }
    public void ecrire(ChouetteRemoveLineTypeType chouette, File file) {
    }

    public void write(List<ILectureEchange> lecturesEchanges, File _temp, String _nomFichier) {
        FileOutputStream   fileOutputStream   = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream   = new FileOutputStream(_temp);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, JEU_CARACTERES);
            if ("agency".equals(_nomFichier))
                writeAgenciesCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("stops".equals(_nomFichier))
                writeStopsCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("routes".equals(_nomFichier))
                writeRoutesCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("trips".equals(_nomFichier))
                writeTripsCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("stop_times".equals(_nomFichier))
                writeStopTimesCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("calendar".equals(_nomFichier))
                writeCalendarCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("calendar_dates".equals(_nomFichier))
                writeCalendarDatesCSVLines(lecturesEchanges, outputStreamWriter);
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch(IOException e) {
            throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
        }
    }

    private void writeAgenciesCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Agency> agencies = getAgencies(lecturesEchanges);
        outputStreamWriter.write("agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone\n");
        for (Agency agency : agencies)
            outputStreamWriter.write(agency.getCSVLine() + "\n");
    }

    private void writeStopsCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Stop> stops = getStops(lecturesEchanges);
        outputStreamWriter.write("stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station\n");
        for (Stop stop : stops)
            outputStreamWriter.write(stop.getCSVLine() + "\n");
    }

    private void writeRoutesCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Route> routes = getRoutes(lecturesEchanges);
        outputStreamWriter.write("route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n");
        for (Route route : routes)
            outputStreamWriter.write(route.getCSVLine() + "\n");
    }

    private void writeTripsCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Trip> trips = getTrips(lecturesEchanges);
        outputStreamWriter.write("route_id,service_id,trip_id,trip_headsign,trip_short_name,direction_id,block_id,shape_id\n");
        for (Trip trip : trips)
            outputStreamWriter.write(trip.getCSVLine() + "\n");
    }

    private void writeStopTimesCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<StopTimes> stopsTimes = getStopTimes(lecturesEchanges);
        outputStreamWriter.write("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled\n");
        for (StopTimes stopTimes : stopsTimes)
            outputStreamWriter.write(stopTimes.getCSVLine() + "\n");
    }

    private void writeCalendarCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Calendar> calendars = getCalendars(lecturesEchanges);
        outputStreamWriter.write("service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\n");
        for (Calendar calendar : calendars)
            outputStreamWriter.write(calendar.getCSVLine() + "\n");
    }

    private void writeCalendarDatesCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<CalendarDates> calendarsDates = getCalendarsDates(lecturesEchanges);
        outputStreamWriter.write("service_id,date,exception_type\n");
        for (CalendarDates calendarDates : calendarsDates)
            outputStreamWriter.write(calendarDates.getCSVLine() + "\n");
    }

    private Collection<Agency> getAgencies(List<ILectureEchange> lecturesEchanges) {
        Map<String, Agency> agencies = new HashMap<String, Agency>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            Transporteur transporteur = lectureEchange.getTransporteur();
            String objId = transporteur.getObjectId();
            Agency agency    = agencies.get(objId);
            if (agency == null) {
                agency = new Agency();
                agency.setAgencyId(objId);
                String name = transporteur.getName();
                if (transporteur.getShortName() != null)
                    name += " (" + transporteur.getShortName() + ")";
                if (transporteur.getRegistrationNumber() != null)
                    name += " (" + transporteur.getRegistrationNumber() + ")";
                agency.getAgencyName(name);
                try {
                    agency.setAgencyURL(new URL("http://www.this_agency.com"));
                }
                catch(MalformedURLException e) {
                }
                agency.setAgencyTimezone(TimeZone.getDefault());
                if (transporteur.getPhone() != null)
                    agency.setAgencyPhone(transporteur.getPhone());
                //agency.setLang();
                agencies.put(objId, agency);
            }
        }
        return agencies.values();
    }

    private Collection<Stop> getStops(List<ILectureEchange> lecturesEchanges) {
        Map<String, Stop> stops = new HashMap<String, Stop>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            List<PositionGeographique> positionsGeographiques = lectureEchange.getPositionsGeographiques();
            Map<String, String> zoneParenteParObjectId = lectureEchange.getZoneParenteParObjectId();
            for (PositionGeographique positionGeographique : positionsGeographiques) {
                String objId = positionGeographique.getObjectId();
                Stop stop = stops.get(objId);
                if (stop == null) {
                    stop = new Stop();
                    stop.setStopId(objId);
                    stop.setStopCode(positionGeographique.getRegistrationNumber());
                    stop.setStopName(positionGeographique.getName());
                    stop.setStopDesc(positionGeographique.getName()+" : "+positionGeographique.getComment());
                    stop.setStopLat(positionGeographique.getLatitude());
                    stop.setStopLon(positionGeographique.getLongitude());
                    //stop.setZoneId();
                    //stop.setStopUrl();
                    stop.setLocationType(positionGeographique.getAreaType());
                    String zoneParenteObjectId = zoneParenteParObjectId.get(stop.getStopId());
                    if (zoneParenteObjectId != null)
                        stop.setParentStation(zoneParenteObjectId);
                    stops.put(objId, stop);
                }
            }
        }
        return stops.values();
    }

    private Collection<Route> getRoutes(List<ILectureEchange> lecturesEchanges) {
        Collection<Route> routes = new ArrayList<Route>();
        for (ILectureEchange lectureEchange: lecturesEchanges) {
            Route route = new Route();
            route.setRouteId(lectureEchange.getLigneObjectId());
            route.setAgencyId(lectureEchange.getTransporteur().getObjectId());
            Ligne ligne = lectureEchange.getLigne();
            String routeShortName = "";
            //if (ligne.getPublishedName() != null)
	    //routeShortName = ligne.getPublishedName();
            //else
	    routeShortName = ligne.getNumber();
            route.setRouteShortName(routeShortName);
            String routeLongName = "";
            if (ligne.getFullName() != null)
                routeLongName = ligne.getFullName();
            else
                routeLongName = ligne.getName();
            //if (ligne.getNumber() != null)
	    //routeLongName += " (" + ligne.getNumber() + ")";
            route.setRouteLongName(routeLongName);
            if (ligne.getComment() != null)
                route.setRouteDesc(ligne.getComment());
            if (ligne.getTransportModeName() != null)
                route.setRouteType(ligne.getTransportModeName());
            try {
                route.setRouteURL(new URL("http://www.this_line.com"));
            }
            catch(MalformedURLException e) {
            }
            //route.setRouteColor(Color.WHITE);
            //route.setRouteTextColor(Color.BLACK);
            routes.add(route);
        }
        return routes;
    }
    
    private Collection<Trip> getTrips(List<ILectureEchange> lecturesEchanges) {
        Collection<Trip> trips = new ArrayList<Trip>();
        for (ILectureEchange lectureEchange: lecturesEchanges) {
            List<Course> courses = lectureEchange.getCourses();
            List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
            List<Itineraire> itineraires = lectureEchange.getItineraires();
            for (Course course : courses) {
                for (TableauMarche tableauMarche : tableauxMarche)
                    for (int i = 0; i < tableauMarche.getVehicleJourneyIdCount(); i++)
                        if (tableauMarche.getVehicleJourneyId(i).equals(course.getObjectId())) {
                            Trip trip = new Trip();
                            trip.setRouteId(lectureEchange.getLigneObjectId());
                            trip.setServiceId(tableauMarche.getObjectId());
                            trip.setTripId(course.getObjectId()+":"+tableauMarche.getObjectId());
                            //trip.setTripHeadsign(...);
                            String name = course.getPublishedJourneyName();
                            if (name == null)
                                name = ""+course.getNumber();
                            if (name.trim().length() == 0)
                                name = course.getComment();
                            trip.setTripShortName(name);
                            for (Itineraire itineraire : itineraires)
                                if (itineraire.getObjectId().equals(course.getRouteId())) {
                                    trip.setDirectionId(itineraire.getDirection());
                                    trip.setBlockId(itineraire.getObjectId());
                                }
                            //trip.setShapeId(...);
                            trips.add(trip);
                            if (tripsByCourseId.get(course.getObjectId()) == null)
                                tripsByCourseId.put(course.getObjectId(), new ArrayList<Trip>());
                            tripsByCourseId.get(course.getObjectId()).add(trip);
                        }
            }
        }
        return trips;
    }

    private Collection<StopTimes> getStopTimes(List<ILectureEchange> lecturesEchanges) {
        Collection<StopTimes> stopsTimes = new ArrayList<StopTimes>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            List<Horaire> horaires = lectureEchange.getHoraires();
            List<ArretItineraire> arretsItineraires = lectureEchange.getArrets();
            for (Horaire horaire : horaires) {
                String courseId = horaire.getVehicleJourneyId();
                List<Trip> trips = tripsByCourseId.get(courseId);
                if ((trips == null) || (trips.isEmpty()))
                    continue;
                Date arrivalTime = horaire.getArrivalTime();
                Date departureTime = horaire.getDepartureTime();
                String stopPointId = horaire.getStopPointId();
                String stopId = null;
                int stopSequence = -1;
                for (ArretItineraire arretItineraire : arretsItineraires)
                    if (arretItineraire.getObjectId().equals(stopPointId)) {
                        stopId = arretItineraire.getContainedIn();
                        stopSequence = arretItineraire.getPosition();
                    }
                for (Trip trip : trips) {
                    StopTimes stopTimes = new StopTimes();
                    stopTimes.setTripId(trip.getTripId());
                    stopTimes.setArrivalTime(arrivalTime);
                    stopTimes.setDepartureTime(departureTime);
                    stopTimes.setStopId(stopId);
		    stopTimes.setStopSequence(stopSequence);
                    //stopTimes.setStopHeadsign(...);
                    //stopTimes.setPickupType(...);
                    //stopTimes.setDropOffType(...);
                    //stopTimes.setShapeDistTraveled(...);
                    stopsTimes.add(stopTimes);
                }
            }
        }
        return stopsTimes;
    }

    private Collection<Calendar> getCalendars(List<ILectureEchange> lecturesEchanges) {
        Map<String, Calendar> calendars = new HashMap<String, Calendar>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
            for (TableauMarche tableauMarche : tableauxMarche) {
                String objId = tableauMarche.getObjectId();
                Calendar calendar = calendars.get(objId);
                if (calendar == null) {
                    List<Periode> periodes = tableauMarche.getPeriodes();
                    if (periodes != null) {
                        if (periodes.size() > 1)
                            logger.error("ON NE TRAITE PAS ENCORE LE CAS DE PLUSIEURS PERIODES DANS UN MEME TM.");
                        for (Periode periode : periodes) {
                            calendar = new Calendar();
                            calendar.setServiceId(objId);
                            calendar.setServiceId(objId);
                            Set<DayTypeType> dayTypeTypes = tableauMarche.getDayTypes();
                            for (DayTypeType dayTypeType : dayTypeTypes) {
                                if (dayTypeType.compareTo(DayTypeType.MONDAY) == 0)
                                    calendar.setMonday(true);
                                if (dayTypeType.compareTo(DayTypeType.TUESDAY) == 0)
                                    calendar.setTuesday(true);
                                if (dayTypeType.compareTo(DayTypeType.WEDNESDAY) == 0)
                                    calendar.setWednesday(true);
                                if (dayTypeType.compareTo(DayTypeType.THURSDAY) == 0)
                                    calendar.setThursday(true);
                                if (dayTypeType.compareTo(DayTypeType.FRIDAY) == 0)
                                    calendar.setFriday(true);
                                if (dayTypeType.compareTo(DayTypeType.SATURDAY) == 0)
                                    calendar.setSaturday(true);
                                if (dayTypeType.compareTo(DayTypeType.SUNDAY) == 0)
                                    calendar.setSunday(true);
                            }
                            calendar.setStartDate(periode.getDebut());
                            calendar.setEndDate(periode.getFin());
                            calendars.put(objId, calendar);
                        }
                    }
                }
            }
        }
        return calendars.values();
    }

    private Collection<CalendarDates> getCalendarsDates(List<ILectureEchange> lecturesEchanges) {
        Map<String, CalendarDates> calendarsDates = new HashMap<String, CalendarDates>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
            for (TableauMarche tableauMarche : tableauxMarche) {
                String objId = tableauMarche.getObjectId();
                List<Date> dates = tableauMarche.getDates();
                for (Date date : dates) {
                    String dateSt = Calendar.sdf.format(date);
                    CalendarDates calendarDates = calendarsDates.get(objId+dateSt);
                    if (calendarDates == null) {
                        calendarDates = new CalendarDates();
                        calendarDates.setServiceId(objId);
                        calendarDates.setDate(date);
                        calendarsDates.put(objId+dateSt, calendarDates);
                    }
                }
            }
        }
        return calendarsDates.values();
    }
}
