package fr.certu.chouette.exchange.csv.importer.producer;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.exchange.csv.importer.report.CSVReportItem;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.report.Report;

public class LineProducer extends AbstractModelProducer<Line>
{
	private static final Logger      logger                    = Logger.getLogger(LineProducer.class);
	public static final String       LINE_NAME_TITLE           = "Nom de la ligne";
	private static final String      PUBLISHED_LINE_NAME_TITLE = "Nom public";
	private static final String      NUMBER_TITLE              = "Numero de la ligne";
	private static final String      CODE_TITLE                = "Code de la ligne";
	private static final String      COMMENT_TITLE             = "Commentaire de la ligne";
	private static final String      TRANSPORT_MODE_NAME_TITLE = "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)";

	private static final String      DIRECTION_TITLE           = "Direction (ALLER/RETOUR)";
	private static final String      TIMETABLE_TITLE           = "Calendriers d'application";
	private static final String      SPECIFIC_TITLE            = "Particularités";
	private static final String      STOPHEADER_TITLE          = "Liste des arrêts";

	private static final int         X_COLUMN                  = 0;
	private static final int         Y_COLUMN                  = 1;
	private static final int         LATITUDE_COLUMN           = 2;
	private static final int         LONGITUDE_COLUMN          = 3;
	private static final int         ADDRESS_COLUMN            = 4;
	private static final int         ZIPCODE_COLUMN            = 5;
	private static final int         AREAZONE_COLUMN           = 6;
	private static final int         STOPNAME_COLUMN           = 7;
	private static final Set<String> VALID_SPECIFICS           = new HashSet<String>();
	static
	{
		VALID_SPECIFICS.add("TAD");
	}

	private String                   projectedPointType        = "epsg:27582";

	@Override
	public Line produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix, Report report)
	{
		Line line = new Line();
		if (firstLine[TITLE_COLUMN].equals(LINE_NAME_TITLE))
		{
			line.setName(firstLine[TITLE_COLUMN + 1]);
			logger.debug("line " + line.getName() + " created");
		}
		else
		{
			logger.debug("no linename on " + firstLine[TITLE_COLUMN]);
			CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR,
					firstLine[TITLE_COLUMN] + "<>" + LINE_NAME_TITLE);
			report.addItem(reportItem);
			return null;
		}
		try
		{ 
			line.setPublishedName(loadStringParam(csvReader, PUBLISHED_LINE_NAME_TITLE));
			line.setNumber(loadStringParam(csvReader, NUMBER_TITLE));
			if (line.getNumber().isEmpty())
			{
				throw new ExchangeException(ExchangeExceptionCode.MANDATORY_VALUE, NUMBER_TITLE, "ligne");
			}
			{
				// CODE_TITLE Optional ; use number if missing
				try
				{
					String[] currentLine = csvReader.readNext();
					if (currentLine[TITLE_COLUMN].equals(CODE_TITLE))
					{
						line.setRegistrationNumber(currentLine[TITLE_COLUMN + 1]);
						if (line.getRegistrationNumber().isEmpty())
						{
							throw new ExchangeException(ExchangeExceptionCode.MANDATORY_VALUE, CODE_TITLE, "ligne");
						}
						line.setComment(loadStringParam(csvReader, COMMENT_TITLE));
					}
					else if (currentLine[TITLE_COLUMN].equals(COMMENT_TITLE))
					{
						line.setRegistrationNumber(line.getNumber());
						line.setComment(currentLine[TITLE_COLUMN + 1]);
					}
					else
					{
						throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, COMMENT_TITLE, "ligne");
					}
				}
				catch (IOException e)
				{
					throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
				}
			}
			line.setTransportModeName(TransportModeNameEnum.valueOf(loadStringParam(csvReader, TRANSPORT_MODE_NAME_TITLE)));
			line.setObjectId(objectIdPrefix + ":" + Line.LINE_KEY + ":" + toIdString(line.getRegistrationNumber()));
			if (!NeptuneIdentifiedObject.checkObjectId(line.getObjectId()))
			{
				CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, line.getName(), line.getObjectId());
				report.addItem(reportItem);
				return null;
			}

			loadRoutes(line, csvReader, objectIdPrefix, report);
		}
		catch (ExchangeException e)
		{
			logger.error("invalid line", e);
			CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.INVALID_LINE, Report.STATE.ERROR,
					e.getLocalizedMessage());
			report.addItem(reportItem);
			return null;
		}
		//      CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.OK_LINE, Report.STATE.OK, line.getName());
		//      report.addItem(reportItem);
		return line;
	}

	private void loadRoutes(Line line, CSVReader csvReader, String objectIdPrefix, Report report)
			throws ExchangeException
			{
		try
		{
			String lineName = line.getName();
			String[] directions = csvReader.readNext();
			if (checkLine(directions))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, DIRECTION_TITLE, lineName);
			if (!DIRECTION_TITLE.equals(directions[TITLE_COLUMN]))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, DIRECTION_TITLE, lineName);
			boolean hole = false;
			int waybackRouteColumn = TITLE_COLUMN + 1;
			int lastVehicleJourneyRank = directions.length; // last VehicleJourney
			// rank (excluded)
			for (int i = TITLE_COLUMN + 1; i < directions.length; i++)
			{
				String value = getValue(i, directions);
				if (hole)
				{
					if (value != null)
						throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, DIRECTION_TITLE,
								Integer.toString(i), lineName);
				}
				else
				{
					if (value == null)
					{
						if (i <= TITLE_COLUMN + 2)
							throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, DIRECTION_TITLE,
									Integer.toString(i), lineName);
						else
						{
							lastVehicleJourneyRank = i;
							hole = true;
						}
					}
					else if (!value.equals(getValue(waybackRouteColumn, directions)))
					{
						waybackRouteColumn = i;
					}

				}
			}
			String[] timetables = csvReader.readNext();
			if (checkLine(timetables))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, TIMETABLE_TITLE, lineName);
			if (!TIMETABLE_TITLE.equals(timetables[TITLE_COLUMN]))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, TIMETABLE_TITLE, lineName);
			if (timetables.length < lastVehicleJourneyRank)
				throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, TIMETABLE_TITLE,
						Integer.toString(timetables.length), lineName);

			for (int i = TITLE_COLUMN + 1; i < lastVehicleJourneyRank; i++)
			{
				if (timetables[i] == null || timetables[i].isEmpty())
					throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, TIMETABLE_TITLE, Integer.toString(i),
							lineName);
			}
			String[] specific = csvReader.readNext();
			if (checkLine(specific))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, SPECIFIC_TITLE, lineName);
			if (!SPECIFIC_TITLE.equals(specific[TITLE_COLUMN]))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, SPECIFIC_TITLE, lineName);
			if (specific.length < lastVehicleJourneyRank)
				throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, SPECIFIC_TITLE,
						Integer.toString(specific.length), lineName);
			String[] header = csvReader.readNext();
			if (checkLine(header))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, STOPHEADER_TITLE, lineName);
			if (!STOPHEADER_TITLE.equals(header[TITLE_COLUMN]))
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, STOPHEADER_TITLE, lineName);
			List<String[]> arrets = new ArrayList<String[]>();
			String[] arret = csvReader.readNext();
			boolean end = checkLine(arret);
			while (!end)
			{
				if (arret[TITLE_COLUMN] == null || arret[TITLE_COLUMN].isEmpty())
					throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, STOPHEADER_TITLE, lineName);
				if (arret.length < lastVehicleJourneyRank)
					throw new ExchangeException(ExchangeExceptionCode.MISSING_VALUES, arret[TITLE_COLUMN],
							Integer.toString(arret.length), lineName);
				arrets.add(arret);
				arret = csvReader.readNext();
				end = checkLine(arret);
			}

			ChouetteAreaEnum areaType = ChouetteAreaEnum.BOARDINGPOSITION;
			Map<String, StopArea> physicals = new HashMap<String, StopArea>();
			Map<String, StopArea> commercials = new HashMap<String, StopArea>();
			switch (line.getTransportModeName())
			{
			case METRO:
			case TRAIN:
			case TRAMWAY:
				areaType = ChouetteAreaEnum.QUAY;
				break;
			}
			// build first route (column TITLE_COLUMN+1)
			int routeColumn = TITLE_COLUMN + 1;
			int journeyColumn = routeColumn;
			Route route = new Route();
			int wayBackRouteRank = 0;
			{
				route.setLine(line);
				line.addRoute(route);
				route.setName(directions[routeColumn]);
				// logger.debug("route "+route.getName()+" created");
				PTDirectionEnum direction = PTDirectionEnum.fromValue(directions[routeColumn].substring(0, 1));
				route.setDirection(direction);
				route.setWayBack(direction.toString());
				route.setObjectId(objectIdPrefix + ":" + Route.ROUTE_KEY + ":" + toIdString(line.getRegistrationNumber()) + "_"
						+ route.getWayBack());
				if (!NeptuneIdentifiedObject.checkObjectId(route.getObjectId()))
				{
					CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, route.getName(), route.getObjectId());
					report.addItem(reportItem);
				}
				// build stopPoint on route and stopArea (BP or Q)
				int rank = 1;
				String baseId = objectIdPrefix + ":" + StopPoint.STOPPOINT_KEY + ":" + toIdString(line.getRegistrationNumber()) + "_"
						+ route.getWayBack() + "_";
				if ("00:00".equals(getValue(routeColumn, arrets.get(0))) ||  "00:00:00".equals(getValue(routeColumn, arrets.get(0))))
					journeyColumn++;
				for (String[] a : arrets)
				{
					if (getValue(routeColumn, a) == null)
						break;
					wayBackRouteRank++;
					StopPoint pt = new StopPoint();
					pt.setObjectId(baseId + rank);
					pt.setRoute(route);
					rank++;

					route.addStopPoint(pt);
					// build physicals and commercials
					StopArea physical = physicals.get(getValue(STOPNAME_COLUMN, a));
					if (physical == null)
					{
						physical = buildPhysical(a, objectIdPrefix, areaType, commercials, report);
						physicals.put(physical.getName(), physical);
					}
					pt.setContainedInStopArea(physical);
					physical.addContainedStopPoint(pt);
					// logger.debug("add "+physical.getName()+" to route");
				}
				route.rebuildPTLinks();
				buildJourneys(route, arrets, timetables, specific, 0, wayBackRouteRank, journeyColumn, waybackRouteColumn);
			}
			// build second route 
			if (waybackRouteColumn != routeColumn)
			{
				journeyColumn = waybackRouteColumn;
				Route wayback = new Route();
				wayback.setLine(line);
				line.addRoute(wayback);
				wayback.setName(directions[waybackRouteColumn]);
				// logger.debug("route "+wayback.getName()+" created");

				PTDirectionEnum direction = PTDirectionEnum.fromValue(directions[waybackRouteColumn].substring(0, 1));
				wayback.setDirection(direction);
				wayback.setWayBack(direction.toString());
				wayback.setObjectId(objectIdPrefix + ":" + Route.ROUTE_KEY + ":" + toIdString(line.getRegistrationNumber()) + "_"
						+ wayback.getWayBack());
				if (!NeptuneIdentifiedObject.checkObjectId(wayback.getObjectId()))
				{
					CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, wayback.getName(), wayback.getObjectId());
					report.addItem(reportItem);
				}

				// connect route couple
				route.setWayBackRouteId(wayback.getObjectId());
				wayback.setWayBackRouteId(route.getObjectId());

				// build stopPoint on route and stopArea (BP or Q)
				int rank = 1;
				String baseId = objectIdPrefix + ":" + StopPoint.STOPPOINT_KEY + ":" + toIdString(line.getRegistrationNumber()) + "_"
						+ wayback.getWayBack() + "_";
				if ("00:00".equals(getValue(waybackRouteColumn, arrets.get(wayBackRouteRank))) || "00:00:00".equals(getValue(waybackRouteColumn, arrets.get(wayBackRouteRank))))
					journeyColumn++;
				for (int i = wayBackRouteRank; i < arrets.size(); i++)
				{
					String[] a = arrets.get(i);
					StopPoint pt = new StopPoint();
					pt.setObjectId(baseId + rank);
					pt.setRoute(wayback);
					rank++;

					wayback.addStopPoint(pt);
					// build physicals and commercials
					StopArea physical = physicals.get(getValue(STOPNAME_COLUMN, a));
					if (physical == null)
					{
						physical = buildPhysical(a, objectIdPrefix, areaType, commercials, report);
						physicals.put(physical.getName(), physical);
					}
					pt.setContainedInStopArea(physical);
					// logger.debug("add "+physical.getName()+" to wayback route");
					physical.addContainedStopPoint(pt);
				}
				wayback.rebuildPTLinks();
				buildJourneys(wayback, arrets, timetables, specific, wayBackRouteRank, arrets.size(), journeyColumn,
						lastVehicleJourneyRank);
			}

		}
		catch (IOException e)
		{
			logger.error("CSV reading failed", e);
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e.getMessage());
		}

			}

	/**
	 * @param route
	 * @param arrets
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 */
	private void buildJourneys(Route route, List<String[]> arrets, String[] timetables, String[] specifics,
			int startRow, int endRow, int startColumn, int endColumn) throws ExchangeException
			{
		int rank = 1;
		int journeyRank = 1;
		List<StopPoint> stopPoints = route.getStopPoints();
		// logger.debug("creating vehicleJourneys for "+route.getName());
		// logger.debug("   column range = "+startColumn+" "+endColumn);
		// logger.debug("   row    range = "+startRow+" "+endRow);
		for (int col = startColumn; col < endColumn; col++)
		{
			VehicleJourney vj = new VehicleJourney();
			vj.setComment(timetables[col]);
			// logger.debug("creating vehicleJourney with "+timetables[col]+" for timetable");
			String specific = getValue(col, specifics);
			if (specific != null)
			{
				if (VALID_SPECIFICS.contains(specific))
				{
					vj.setVehicleTypeIdentifier(specific);
					// logger.debug(" specific found : "+specific);
				}
			}
			vj.setObjectId(route.getObjectId().replace(Route.ROUTE_KEY, VehicleJourney.VEHICLEJOURNEY_KEY) + "_" + rank);
			rank++;
			for (int row = startRow; row < endRow; row++)
			{
				Time departureTime = getTimeValue(col, arrets.get(row));
				if (departureTime != null)
				{
					VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();
					vjas.setVehicleJourney(vj);
					vjas.setStopPoint(stopPoints.get(row - startRow));
					vjas.setDepartureTime(departureTime);
					vjas.setArrivalTime(departureTime);
					vj.addVehicleJourneyAtStop(vjas);
					// logger.debug(" passing time for : "+vjas.getStopPoint().getContainedInStopArea().getName());
				}
			}
			if (vj.getVehicleJourneyAtStops().size() > 0)
			{
				vj.setRoute(route);
				if (vj.checkJourneyPattern())
				{
					JourneyPattern journey = vj.getJourneyPattern();
					if (!journey.getObjectId().contains(":"))
					{
						// newly created journey
						journey.setObjectId(route.getObjectId().replace(Route.ROUTE_KEY, JourneyPattern.JOURNEYPATTERN_KEY)
								+ "_" + journeyRank);
						journeyRank++;
						route.addJourneyPattern(journey);
						journey.setRoute(route);
					}
				}
			}
			else
			{
				logger.debug("no passing time for vehicleJourney , ignored");
			}
		}
		for (JourneyPattern journeyPattern : route.getJourneyPatterns()) 
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

	/**
	 * @param stopData
	 * @param objectIdPrefix
	 * @param areaType
	 * @param commercials
	 * @return
	 */
	private StopArea buildPhysical(String[] stopData, String objectIdPrefix, ChouetteAreaEnum areaType,
			Map<String, StopArea> commercials, Report report)
	{
		StopArea physical;
		physical = new StopArea();
		physical.setAreaType(areaType);
		physical.setName(getValue(STOPNAME_COLUMN, stopData));
		physical.setObjectId(objectIdPrefix + ":" + StopArea.STOPAREA_KEY + ":BP_" + toIdString(physical.getName()));
		physical.setLatitude(getBigDecimalValue(LATITUDE_COLUMN, stopData));
		physical.setLongitude(getBigDecimalValue(LONGITUDE_COLUMN, stopData));
		if (physical.getLatitude() == null || physical.getLongitude() == null)
		{
			logger.warn("stop without coordinates : " + physical.getName());
			CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.STOP_WITHOUT_COORDS, Report.STATE.WARNING,
					physical.getName());
			report.addItem(reportItem);
		}
		if (getValue(X_COLUMN, stopData) != null)
		{
			physical.setX(getBigDecimalValue(X_COLUMN, stopData));
			physical.setY(getBigDecimalValue(Y_COLUMN, stopData));
			physical.setProjectionType(projectedPointType);
		}
		if (getValue(ADDRESS_COLUMN, stopData) != null || getValue(ZIPCODE_COLUMN, stopData) != null)
		{
			physical.setStreetName(getValue(ADDRESS_COLUMN, stopData));
			physical.setCountryCode(getValue(ZIPCODE_COLUMN, stopData));
			if (physical.getCountryCode() != null)
				physical.setObjectId(physical.getObjectId() + "_" + toIdString(physical.getCountryCode()));
		}
		StopArea commercial = commercials.get(getValue(AREAZONE_COLUMN, stopData));
		if (commercial == null)
		{
			commercial = buildCommercial(stopData, objectIdPrefix,report);
			commercials.put(commercial.getName(), commercial);
		}
		commercial.addContainedStopArea(physical);
		if (!NeptuneIdentifiedObject.checkObjectId(physical.getObjectId()))
		{
			CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, physical.getName(), physical.getObjectId());
			report.addItem(reportItem);
		}
		physical.setParent(commercial);
		return physical;
	}

	/**
	 * @param stopData
	 * @param objectIdPrefix
	 * @return
	 */
	private StopArea buildCommercial(String[] stopData, String objectIdPrefix,Report report)
	{
		StopArea commercial;
		commercial = new StopArea();
		commercial.setAreaType(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
		commercial.setName(getValue(AREAZONE_COLUMN, stopData));
		commercial.setObjectId(objectIdPrefix + ":" + StopArea.STOPAREA_KEY + ":C_" + toIdString(commercial.getName()));
		if (getValue(ADDRESS_COLUMN, stopData) != null || getValue(ZIPCODE_COLUMN, stopData) != null)
		{
			commercial.setStreetName(getValue(ADDRESS_COLUMN, stopData));
			commercial.setCountryCode(getValue(ZIPCODE_COLUMN, stopData));
			if (commercial.getCountryCode() != null)
				commercial.setObjectId(commercial.getObjectId() + "_" + toIdString(commercial.getCountryCode()));
		}
		if (!NeptuneIdentifiedObject.checkObjectId(commercial.getObjectId()))
		{
			CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, commercial.getName(), commercial.getObjectId());
			report.addItem(reportItem);
		}
		return commercial;
	}

	protected String loadStringParam(CSVReader csvReader, String title) throws ExchangeException
	{

		try
		{
			String[] currentLine = csvReader.readNext();
			if (currentLine[TITLE_COLUMN].equals(title))
			{
				return currentLine[TITLE_COLUMN + 1];
			}
			else
			{
				throw new ExchangeException(ExchangeExceptionCode.MISSING_TAG, title, "ligne");
			}
		}
		catch (IOException e)
		{
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
		}
	}
}
