package fr.certu.chouette.exchange.netex.importer.converters;



import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


public class NeptuneConverter {
    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private VTDNav vTDNav;
    private PTNetworkConverter networkConverter;
    private CompanyConverter companyConverter;
    private LineConverter lineConverter;
    private GroupOfLinesConverter groupOfLinesConverter;
    private RouteConverter routeConverter;
    private JourneyPatternConverter journeyPatternConverter;
    private VehicleJourneyConverter vehicleJourneyConverter;
    private VehicleJourneyAtStopConverter vehicleJourneyAtStopConverter;
    private TimetableConverter timetableConverter;
    private StopAreaConverter stopAreaConverter;
    private ConnectionLinkConverter connectionLinkConverter;
    
    public NeptuneConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        networkConverter = new PTNetworkConverter(vTDNav);
        companyConverter = new CompanyConverter(vTDNav);
        lineConverter = new LineConverter(vTDNav);
        groupOfLinesConverter = new GroupOfLinesConverter(vTDNav);
        routeConverter = new RouteConverter(vTDNav);
        journeyPatternConverter = new JourneyPatternConverter(vTDNav);
        vehicleJourneyConverter = new VehicleJourneyConverter(vTDNav);
        vehicleJourneyAtStopConverter = new VehicleJourneyAtStopConverter(vTDNav);
        timetableConverter = new TimetableConverter(vTDNav);
        stopAreaConverter = new StopAreaConverter(vTDNav);
        connectionLinkConverter = new ConnectionLinkConverter(vTDNav);
    }
    
    public Line convert(Report report) throws XPathParseException, XPathEvalException, NavException, ParseException
    {
        PTNetwork network = networkConverter.convert();        
        Company company = companyConverter.convert();        
        Line line = lineConverter.convert();
        List<GroupOfLine> groupOfLines = groupOfLinesConverter.convert();
        List<Route> routes = routeConverter.convert();
        List<JourneyPattern> journeyPatterns = journeyPatternConverter.convert();
        List<VehicleJourney> vehicleJourneys = vehicleJourneyConverter.convert();
        List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourneyAtStopConverter.convert();
        List<Timetable> timetables = timetableConverter.convert();
        List<StopArea> stopAreas = stopAreaConverter.convert();
        List<ConnectionLink> connectionLinks = connectionLinkConverter.convert();
        
        // Ids
        Map<String,StopPoint> stopPointByObjectId = routeConverter.getStopPointByObjectId();
        
        // Link line with network and company
        line.setPtNetwork(network);                
        line.setCompany(company);             
        for (GroupOfLine groupOfLine : groupOfLines) {
            line.addGroupOfLine(groupOfLine);
        }
        
        // Link route with journey patterns        
        Map<String, List<JourneyPattern>> journeyPatternByRouteObjectId = journeyPatternConverter.getJourneyPatternByRouteObjectId();
        for (Route route : routes) {
            // Hack because we use only one line perhaps to do before
            line.addRoute(route);            
            List<JourneyPattern> journeyPatternsOfRoute = journeyPatternByRouteObjectId.get(route.getObjectId());
            route.setJourneyPatterns(journeyPatternsOfRoute);
            // setters don't set reverse link: must be set after
            for (JourneyPattern journeyPattern : journeyPatternsOfRoute) 
            {
            	journeyPattern.setRoute(route);
			}
        }
        
        Map<String, List<VehicleJourney>> vehicleJourneysByJPObjectId = vehicleJourneyConverter.getVehicleJourneysByJPObjectId();
        // Link journey pattern with stop points and vehicle journeys
        for (JourneyPattern journeyPattern : journeyPatterns) {
            for (String stopPointId : journeyPattern.getStopPointIds()) {
                if (stopPointByObjectId.get(stopPointId) != null)
                    journeyPattern.addStopPoint(stopPointByObjectId.get(stopPointId));
            }
            List<VehicleJourney> vehicleJourneysOfJourneyPattern = vehicleJourneysByJPObjectId.get(journeyPattern.getObjectId());
            journeyPattern.setVehicleJourneys(vehicleJourneysOfJourneyPattern);
            // setters don't set reverse link: must be set after
            for (VehicleJourney vehicleJourney : vehicleJourneysOfJourneyPattern) 
            {
            	vehicleJourney.setJourneyPattern(journeyPattern);
            	vehicleJourney.setRoute(journeyPattern.getRoute());
			}
        }
                                   
        Map<String, List<VehicleJourneyAtStop>> vehicleJourneyAtStopsByVJObjectId = vehicleJourneyAtStopConverter.getVehicleJourneyAtStopsByVJObjectId();
        Map<String, List<String>> timetablesByVehicleJourneyObjectId = vehicleJourneyConverter.getTimetablesByVehicleJourneyObjectId();
        Map<String, Timetable> timetablesByObjectId = timetableConverter.getTimetablesByObjectId();
        // Link vehicle journeys with vehicle journey at stop and time tables
        for (VehicleJourney vehicleJourney : vehicleJourneys) {
        	List<VehicleJourneyAtStop> vjassOfVj = vehicleJourneyAtStopsByVJObjectId.get(vehicleJourney.getObjectId());
            vehicleJourney.setVehicleJourneyAtStops( vjassOfVj );
            // setters don't set reverse link: must be set after
            for (VehicleJourneyAtStop vehicleJourneyAtStop : vjassOfVj) 
            {
            	vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
            	StopPoint stopPoint = stopPointByObjectId.get(vehicleJourneyAtStop.getStopPointId());
            	vehicleJourneyAtStop.setStopPoint(stopPoint);
			}
            
            List<String> timetablesObjectId = timetablesByVehicleJourneyObjectId.get(vehicleJourney.getObjectId());
            for (String timetableObjectId : timetablesObjectId) 
            {
                vehicleJourney.addTimetable( timetablesByObjectId.get(timetableObjectId) );
            }            
        }        
        
        // Link stop point with stop area
        Map<String,StopArea> stopAreaByObjectId = stopAreaConverter.getStopAreaByObjectId();      
        for (StopPoint stopPoint : stopPointByObjectId.values()) {
            StopArea stopArea = stopAreaByObjectId.get( stopPoint.getContainedInStopAreaId() );
            stopArea.addContainedStopPoint(stopPoint);
        }
        
        // Replace objectId references by object for all 
        // connectionLink.startOfLink and EndOfLink
        for ( ConnectionLink connectionLink : connectionLinks) {
            connectionLink.setStartOfLink( stopAreaByObjectId.get( connectionLink.getStartOfLinkId()));
            connectionLink.setEndOfLink( stopAreaByObjectId.get( connectionLink.getEndOfLinkId()));
        }
		// report for save
		ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK, line.getObjectId());
		report.addItem(item);
		// report objects count
		{
			ExchangeReportItem countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ROUTE_COUNT,Report.STATE.OK,routes.size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,Report.STATE.OK,journeyPatterns.size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,Report.STATE.OK,vehicleJourneys.size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.STOP_AREA_COUNT,Report.STATE.OK,stopAreas.size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,Report.STATE.OK,connectionLinks.size());
            item.addItem(countItem);
//			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ACCES_POINT_COUNT,Report.STATE.OK,accessPoints.size());
//            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.TIME_TABLE_COUNT,Report.STATE.OK,timetables.size());
            item.addItem(countItem);
		}
        
        return line;
    }
    
}
