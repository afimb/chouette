/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.gtfs.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public class ModelAssembler 
{

	@Getter @Setter private List<Line> lines = new ArrayList<Line>();
	@Getter @Setter private List<Route> routes= new ArrayList<Route>();
	@Getter @Setter private List<Company> companies = new ArrayList<Company>();
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
	@Getter @Setter private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
	@Getter @Setter private List<StopPoint> stopPoints = new ArrayList<StopPoint>();
	@Getter @Setter private List<StopArea> stopAreas = new ArrayList<StopArea>();
	@Getter @Setter private List<Timetable> timetables = new ArrayList<Timetable>();
	@Getter @Setter private List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();

	private Map<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>> populatedDictionaries = new HashMap<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>>();
	private Map<String, Company> companiesDictionary = new HashMap<String, Company>();

	private Report report;
	//	private Map<String, Line> linesDictionary = new HashMap<String, Line>();
	//	private Map<String, Route> routesDictionary = new HashMap<String, Route>();
	//	private Map<String, JourneyPattern> journeyPatternsDictionary = new HashMap<String, JourneyPattern>();
	//	private Map<String, VehicleJourney> vehicleJourneysDictionary = new HashMap<String, VehicleJourney>();
	//	private Map<String, StopPoint> stopPointsDictionary = new HashMap<String, StopPoint>();
	//	private Map<String, StopArea> stopAreasDictionary = new HashMap<String, StopArea>();
	//	private Map<String, Timetable> timetablesDictionary = new HashMap<String, Timetable>();

	public void connect(Report report)
	{
		this.report = report;
		populateDictionaries();
		connectLines();
		connectRoutes();
		connectCompanies();
		connectPTNetwork();
		connectJourneyPatterns();
		connectVehicleJourneys();
		connectStopPoints();
		connectStopAreas();
		connectTimetables();
		reportLines();
	}

	private void reportLines()
	{
		for (Line line : lines) 
		{
			ReportItem importItem = new ExchangeReportItem(ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK);
			report.addItem(importItem);
			importItem.addMessageArgs(line.getName());
			line.complete();
			ExchangeReportItem countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ROUTE_COUNT,Report.STATE.OK,line.getRoutes().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,Report.STATE.OK,line.getJourneyPatterns().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,Report.STATE.OK,line.getVehicleJourneys().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.STOP_AREA_COUNT,Report.STATE.OK,line.getStopAreas().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,Report.STATE.OK,line.getConnectionLinks().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ACCES_POINT_COUNT,Report.STATE.OK,line.getAccessPoints().size());
			importItem.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.TIME_TABLE_COUNT,Report.STATE.OK,line.getTimetables().size());
			importItem.addItem(countItem);

		}

	}

	private void populateDictionaries()
	{

		populateDictionnary(companies, companiesDictionary);
		//		populateDictionnary(lines, linesDictionary);
		//		populateDictionnary(routes, routesDictionary);
		//		populateDictionnary(journeyPatterns, journeyPatternsDictionary);
		//		populateDictionnary(vehicleJourneys, vehicleJourneysDictionary);
		//		populateDictionnary(stopPoints, stopPointsDictionary);
		//		populateDictionnary(stopAreas, stopAreasDictionary);
		//		populateDictionnary(timetables, timetablesDictionary);
	}

	private <T extends NeptuneIdentifiedObject> void populateDictionnary(List<T> list, Map<String,T> dictionnary)
	{

		for(T item : list)
		{
			if(item != null && item.getObjectId() != null)
			{
				dictionnary.put(item.getObjectId(), item);
			}
		}
		if(list.size() > 0)
		{
			populatedDictionaries.put(list.get(0).getClass(), dictionnary);
		}
	}

	private void connectLines()
	{
		for (Line line : lines) 
		{
			line.setPtNetwork(ptNetwork);
			//			line.setRoutes(getObjectsFromIds(line.getRouteIds(), Route.class));
			if (line.getComment() == null)
			{
				// use for default : first company
				if (!companies.isEmpty()) 
				   line.setCompany(companies.get(0));
			}
			else
			{
				line.setCompany(getObjectFromId(line.getComment(), Company.class));
				line.setComment(null);
			}

			switch (line.getTransportModeName())
			{
			case LocalTrain :
			case LongDistanceTrain :
			case Metro :
			case RapidTransit :
			case Tramway :
			case Train :
				changeBoardingPositionToQuay(line);
				break;
			default :
				break;
			}
		}
	}


	/**
	 * force boarding position for rail type lines to quay
	 * 
	 * @param line
	 */
	private void changeBoardingPositionToQuay(Line line) 
	{
		for (Route route : line.getRoutes()) 
		{
			for (StopPoint point : route.getStopPoints()) 
			{
				if (point.getContainedInStopArea() != null)
				{
					point.getContainedInStopArea().setAreaType(ChouetteAreaEnum.Quay);
				}
			}
		}

	}

	private void connectRoutes()
	{
	}


	private void connectCompanies()
	{
	}

	private void connectPTNetwork() 
	{
		ptNetwork.setLines(lines);
	}

	private void connectJourneyPatterns() 
	{
		for (JourneyPattern journeyPattern : journeyPatterns) 
		{
			for (StopPoint point : journeyPattern.getStopPoints())
			{
				if (journeyPattern.getArrivalStopPoint() == null || journeyPattern.getArrivalStopPoint().before(point))
				{
					journeyPattern.setArrivalStopPoint(point);
				}
				if (journeyPattern.getDepartureStopPoint() == null || journeyPattern.getDepartureStopPoint().after(point))
				{
					journeyPattern.setDepartureStopPoint(point);
				}
			}			
		}

	}


	private void connectVehicleJourneys()
	{
	}

	private void connectStopPoints() 
	{
	}


	private void connectStopAreas() 
	{
	}

	private void connectTimetables() 
	{
		// purge not connected timetables
		for (Iterator<Timetable> tm = timetables.iterator(); tm.hasNext();) 
		{
			if (tm.next().getVehicleJourneys().isEmpty())
			{
				tm.remove();
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends NeptuneIdentifiedObject> T getObjectFromId(String id, Class<T> dictionaryClass)
	{

		Map<String, ? extends NeptuneIdentifiedObject> dictionary =  populatedDictionaries.get(dictionaryClass);
		T object = null;

		if (dictionary != null)
			object = (T)dictionary.get(id);

		return object;
	}
}
