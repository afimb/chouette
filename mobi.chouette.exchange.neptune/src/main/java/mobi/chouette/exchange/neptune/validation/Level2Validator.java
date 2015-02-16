package mobi.chouette.exchange.neptune.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.RoutingConstraint;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

import com.google.common.collect.BiMap;

@Log4j
public class Level2Validator implements Constant
{
	private static final String NETWORK_1 = "2-NEPTUNE-Network-1";
	private static final String GROUP_OF_LINE_1 = "2-NEPTUNE-GroupOfLine-1";
	private static final String STOP_AREA_1 = "2-NEPTUNE-StopArea-1";
	private static final String STOP_AREA_2 = "2-NEPTUNE-StopArea-2";
	private static final String STOP_AREA_3 = "2-NEPTUNE-StopArea-3";
	private static final String STOP_AREA_4 = "2-NEPTUNE-StopArea-4";
	private static final String STOP_AREA_5 = "2-NEPTUNE-StopArea-5";
	private static final String STOP_AREA_6 = "2-NEPTUNE-StopArea-6";
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";
	private static final String ITL_3 = "2-NEPTUNE-ITL-3";
	private static final String ITL_4 = "2-NEPTUNE-ITL-4";
	private static final String ITL_5 = "2-NEPTUNE-ITL-5";
	private static final String AREA_CENTROID_1 = "2-NEPTUNE-AreaCentroid-1";
	private static final String AREA_CENTROID_2 = "2-NEPTUNE-AreaCentroid-2";
	private static final String CONNECTION_LINK_1 = "2-NEPTUNE-ConnectionLink-1";
	private static final String ACCESS_POINT_1 = "2-NEPTUNE-AccessPoint-1";
	private static final String ACCESS_POINT_2 = "2-NEPTUNE-AccessPoint-2";
	private static final String ACCESS_POINT_3 = "2-NEPTUNE-AccessPoint-3";
	private static final String ACCESS_POINT_4 = "2-NEPTUNE-AccessPoint-4";
	private static final String ACCESS_POINT_5 = "2-NEPTUNE-AccessPoint-5";
	private static final String ACCESS_POINT_6 = "2-NEPTUNE-AccessPoint-6";
	private static final String ACCESS_POINT_7 = "2-NEPTUNE-AccessPoint-7";
	private static final String ACCESS_LINK_1 = "2-NEPTUNE-AccessLink-1";
	private static final String ACCESS_LINK_2 = "2-NEPTUNE-AccessLink-2";
	private static final String LINE_1 = "2-NEPTUNE-Line-1";
	private static final String LINE_2 = "2-NEPTUNE-Line-2";
	private static final String LINE_3 = "2-NEPTUNE-Line-3";
	private static final String LINE_4 = "2-NEPTUNE-Line-4";
	private static final String LINE_5 = "2-NEPTUNE-Line-5";
	private static final String ROUTE_1 = "2-NEPTUNE-Route-1";
	private static final String ROUTE_2 = "2-NEPTUNE-Route-2";
	private static final String ROUTE_3 = "2-NEPTUNE-Route-3";
	private static final String ROUTE_4 = "2-NEPTUNE-Route-4";
	private static final String ROUTE_5 = "2-NEPTUNE-Route-5";
	private static final String ROUTE_6 = "2-NEPTUNE-Route-6";
	private static final String ROUTE_7 = "2-NEPTUNE-Route-7";
	private static final String ROUTE_8 = "2-NEPTUNE-Route-8";
	private static final String ROUTE_9 = "2-NEPTUNE-Route-9";
	private static final String ROUTE_10 = "2-NEPTUNE-Route-10";
	private static final String ROUTE_11 = "2-NEPTUNE-Route-11";
	private static final String ROUTE_12 = "2-NEPTUNE-Route-12";
	private static final String PT_LINK_1 = "2-NEPTUNE-PtLink-1";
	private static final String JOURNEY_PATTERN_1 = "2-NEPTUNE-JourneyPattern-1";
	private static final String JOURNEY_PATTERN_2 = "2-NEPTUNE-JourneyPattern-2";
	private static final String JOURNEY_PATTERN_3 = "2-NEPTUNE-JourneyPattern-3";
	private static final String STOP_POINT_1 = "2-NEPTUNE-StopPoint-1";
	private static final String STOP_POINT_2 = "2-NEPTUNE-StopPoint-2";
	private static final String STOP_POINT_3 = "2-NEPTUNE-StopPoint-3";
	private static final String STOP_POINT_4 = "2-NEPTUNE-StopPoint-4";
	private static final String TIMETABLE_1 = "2-NEPTUNE-Timetable-1";
	private static final String TIMETABLE_2 = "2-NEPTUNE-Timetable-2";
	private static final String VEHICLE_JOURNEY_1 = "2-NEPTUNE-VehicleJourney-1";
	private static final String VEHICLE_JOURNEY_2 = "2-NEPTUNE-VehicleJourney-2";
	private static final String VEHICLE_JOURNEY_3 = "2-NEPTUNE-VehicleJourney-3";
	private static final String VEHICLE_JOURNEY_4 = "2-NEPTUNE-VehicleJourney-4";
	private static final String VEHICLE_JOURNEY_5 = "2-NEPTUNE-VehicleJourney-5";
	private static final String VEHICLE_JOURNEY_6 = "2-NEPTUNE-VehicleJourney-6";
	private static final String VEHICLE_JOURNEY_7 = "2-NEPTUNE-VehicleJourney-7";
	private static final String VEHICLE_JOURNEY_AT_STOP_1 = "2-NEPTUNE-VehicleJourneyAtStop-1";
	private static final String VEHICLE_JOURNEY_AT_STOP_2 = "2-NEPTUNE-VehicleJourneyAtStop-2";
	private static final String VEHICLE_JOURNEY_AT_STOP_3 = "2-NEPTUNE-VehicleJourneyAtStop-3";
	private static final String VEHICLE_JOURNEY_AT_STOP_4 = "2-NEPTUNE-VehicleJourneyAtStop-4";
	private static final String FACILITY_1 = "2-NEPTUNE-Facility-1";
	private static final String FACILITY_2 = "2-NEPTUNE-Facility-2";
	private static final String FACILITY_3 = "2-NEPTUNE-Facility-3";
	private static final String FACILITY_4 = "2-NEPTUNE-Facility-4";
	private static final String FACILITY_5 = "2-NEPTUNE-Facility-5";
	private static final String FACILITY_6 = "2-NEPTUNE-Facility-6";

	private Map<String, List<String>> sequenceOfRoutes = new HashMap<String, List<String>>(); // will

	private Referential referential ;
	private Map<String,FileLocation> locations ; 
	private ValidationReport validationReport;
	private NeptuneObjectFactory factory ;
	private BiMap<String, String> stopareaAreacentroidMap ;

	@SuppressWarnings("unchecked")
	public Level2Validator(Context context)
	{
		validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		referential = (Referential) context.get(REFERENTIAL);
		locations = (Map<String, FileLocation>) context.get(OBJECT_LOCALISATION);
		factory =  (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);
		stopareaAreacentroidMap = (BiMap<String, String>) context.get(STOPAREA_AREACENTROID_MAP);
		initValidation();
	}
	private void initValidation()
	{
		String prefix = "2-NEPTUNE-";
		int order = addItemToValidation( prefix, "Common", 3, 1,
				"W", "E", "E");
		order = addItemToValidation( prefix, "Network", 1, order,
				"W");
		order = addItemToValidation( prefix, "GroupOfLine", 1,
				order, "W");
		order = addItemToValidation( prefix, "StopArea", 6, order,
				"E", "E", "E", "E", "E", "E");
		order = addItemToValidation( prefix, "ITL", 5, order, "E",
				"E", "E", "E", "E");
		order = addItemToValidation( prefix, "AreaCentroid", 2,
				order, "E", "E");
		order = addItemToValidation( prefix, "ConnectionLink", 1,
				order, "E");
		order = addItemToValidation( prefix, "AccessPoint", 7,
				order, "E", "E", "E", "E", "E", "E", "E");
		order = addItemToValidation( prefix, "AccessLink", 2,
				order, "E", "E");
		order = addItemToValidation( prefix, "Line", 5, order,
				"E", "W", "W", "E", "E");
		order = addItemToValidation( prefix, "Route", 12, order,
				"E", "E", "E", "E", "E", "E", "E", "E", "W", "E", "W", "W");
		order = addItemToValidation( prefix, "PtLink", 1, order,
				"E");
		order = addItemToValidation( prefix, "JourneyPattern", 3,
				order, "E", "E", "E");
		order = addItemToValidation( prefix, "StopPoint", 4,
				order, "E", "E", "E", "E");
		order = addItemToValidation( prefix, "Timetable", 2,
				order, "W", "W");
		order = addItemToValidation( prefix, "VehicleJourney", 7,
				order, "E", "E", "E", "E", "E", "E", "W");
		order = addItemToValidation( prefix,
				"VehicleJourneyAtStop", 4, order, "E", "E", "E", "E");
		order = addItemToValidation( prefix, "Facility", 6, order,
				"E", "E", "E", "E", "E", "E");
	}

	private int addItemToValidation(
			String prefix, String name, int count, int order, String... severities)
	{
		for (int i = 1; i <= count; i++)
		{
			if (severities[i - 1].equals("W"))
			{
				validationReport.getCheckPoints().add(new CheckPoint(prefix + name + "-"
						+ i, order++, CheckPoint.RESULT.UNCHECK,
						CheckPoint.SEVERITY.WARNING));
			} else
			{
				validationReport.getCheckPoints().add(new CheckPoint(prefix + name + "-"
						+ i, order++, CheckPoint.RESULT.UNCHECK,
						CheckPoint.SEVERITY.ERROR));
			}
		}
		return order;
	}

	/**
	 * add a detail on a checkpoint
	 * 
	 * @param checkPointKey
	 * @param item
	 */
	protected void addValidationError(String checkPointKey, Detail item)
	{
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		checkPoint.addDetail(item);

	}


	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param checkPointKey
	 */
	protected void prepareCheckPoint(String checkPointKey)
	{
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		if (checkPoint.getDetails().isEmpty())
			checkPoint.setState(CheckPoint.RESULT.OK);
	}


	public void validate()
	{
		long startTime = System.currentTimeMillis();
		validateNetwork();
		validateGroupOfLines();
		validateStopAreas();
		validateITL();
//		validateAreaCentroids();
//		validateConnectionLinks();
//		validateAccessPoints();
//		validateAccessLink();
//		validateLine();
//		validateRoutes();
//		validatePtLink();
//		validateJourneyPattern();
//		validateStopPoints();
//		validateTimetables();
//		validateVehicleJourneys();
//		validateFacilities();
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime) / 1000;
		long millis = (endTime - startTime) % 1000;
		log.info("validating all in " + duration + " s " + millis + " ms");

	}

	private void validateNetwork()
	{
		// 2-NEPTUNE-PtNetwork-1 : check if lineId of line is present in list
		//	      if (!isListEmpty(ptNetwork.getLineId()))
		//	      {
		//	         prepareCheckPoint(NETWORK_1);
		//	         String lineId = line.getObjectId();
		//	         if (!ptNetwork.getLineId().contains(lineId))
		//	         {
		//	            Locator srcLoc = ptNetwork.sourceLocation();
		//	            Map<String, Object> map = new HashMap<String, Object>();
		//	            map.put("lineId", lineId);
		//	            ReportLocation location = new ReportLocation(sourceFile,
		//	                  srcLoc.getLineNumber(), srcLoc.getColumnNumber());
		//	            DetailReportItem detail = new DetailReportItem(NETWORK_1,
		//	                  ptNetwork.getObjectId(), Report.STATE.WARNING, location, map);
		//	            addValidationError(NETWORK_1, detail);
		//	         }
		//	      }
	}

	private void validateGroupOfLines()
	{
		// 2-NEPTUNE-GroupOfLine-1 : check if lineId of line is present in list
		//	      for (GroupOfLineType groupOfLine : groupOfLines.values())
		//	      {
		//	         if (!isListEmpty(groupOfLine.getLineId()))
		//	         {
		//	            prepareCheckPoint(GROUP_OF_LINE_1);
		//	            String lineId = line.getObjectId();
		//	            if (!groupOfLine.getLineId().contains(lineId))
		//	            {
		//	               Locator srcLoc = groupOfLine.sourceLocation();
		//	               Map<String, Object> map = new HashMap<String, Object>();
		//	               map.put("lineId", lineId);
		//	               ReportLocation location = new ReportLocation(sourceFile,
		//	                     srcLoc.getLineNumber(), srcLoc.getColumnNumber());
		//	               DetailReportItem detail = new DetailReportItem(GROUP_OF_LINE_1,
		//	                     groupOfLine.getObjectId(), Report.STATE.WARNING, location,
		//	                     map);
		//	               addValidationError(GROUP_OF_LINE_1, detail);
		//	            }
		//	         }
		//	      }
	}

	private void validateStopAreas()
	{
		Map<String, StopArea> stopAreas = referential.getStopAreas();
		if (!stopAreas.isEmpty())
			prepareCheckPoint(STOP_AREA_1); // TODO check if stopareas are in file

		for (StopArea stopArea : stopAreas.values())
		{
			// check if object is in file
			if (!locations.containsKey(stopArea.getObjectId())) continue;
			FileLocation sourceLocation = locations.get(stopArea.getObjectId());
			switch (stopArea.getAreaType())
			{
			case StopPlace:
			{
				prepareCheckPoint(STOP_AREA_2);
				// 2-NEPTUNE-StopArea-2 : if stoparea is StopPlace :
				// check if it refers only stopareas of type stopplace
				// or commercialstoppoints
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					if (!child.getAreaType().equals(ChouetteAreaEnum.StopPlace) && 
							!child.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								STOP_AREA_2,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(STOP_AREA_2, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_2,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(STOP_AREA_2, errorItem);
				}
			}
			break;
			case CommercialStopPoint:
			{
				// 2-NEPTUNE-StopArea-3 : if stoparea is
				// commercialStopPoint : check if it refers only
				// stopareas of type quay or boardingPosition
				prepareCheckPoint(STOP_AREA_3);
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					if (!child.getAreaType().equals(ChouetteAreaEnum.Quay) && 
							!child.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								STOP_AREA_3,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(STOP_AREA_3, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_3,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(STOP_AREA_3, errorItem);
				}
			}
			break;
			case Quay:
			case BoardingPosition:
			{
				prepareCheckPoint(STOP_AREA_4);
				// 2-NEPTUNE-StopArea-4 : if stoparea is quay or
				// boardingPosition : check if it refers only StopPoints
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", child.getAreaType().toString());
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_4,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(STOP_AREA_4, errorItem);

				}
			}
			break;
			case ITL:
			{
				// 2-NEPTUNE-ITL-1 : if stoparea is ITL : check if it
				// refers only non ITL stopAreas
				prepareCheckPoint(ITL_1);
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					if (child.getAreaType().equals(ChouetteAreaEnum.ITL))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								ITL_1,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(ITL_1, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!locations.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							ITL_1,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(ITL_1, errorItem);
				}
				// 2-NEPTUNE-ITL-2 : if stoparea is ITL : check if a ITLType
				// object refers it
				prepareCheckPoint(ITL_2);
				RoutingConstraint routingConstraint = factory.getRoutingConstraint(stopArea.getObjectId());
				if (routingConstraint == null)
				{
					// unused ITL Stop
					Detail errorItem = new Detail(
							ITL_2,
							new Location(sourceLocation,stopArea.getObjectId()));
					addValidationError(ITL_1, errorItem);
				}
			}


			}
			if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				prepareCheckPoint(STOP_AREA_5);
				prepareCheckPoint(STOP_AREA_6);
				String centroidId = stopareaAreacentroidMap.get(stopArea.getObjectId());
				if (centroidId == null)
				{
					// 2-NEPTUNE-StopArea-5 : if stoparea is not ITL : check if
					// it refers an existing areacentroid (replace test
					// fk_centroid_stoparea from XSD)
					// TODO 
					//					{
					//						Map<String, Object> map = new HashMap<String, Object>();
					//						map.put("centroidOfArea", "xxxxxx" );
					//						Detail errorItem = new Detail(
					//								STOP_AREA_5,
					//								new Location(sourceLocation,stopArea.getObjectId()), map);
					//						addValidationError(STOP_AREA_5, errorItem);
					//					} 
				}else
				{
					AreaCentroid centroid = factory.getAreaCentroid(centroidId);
					// 2-NEPTUNE-StopArea-6 : if stoparea is not ITL : check
					// if it refers an existing areacentroid which refers
					// the good stoparea.
					if (centroid.getContainedInId() != null)
					{
						if (!centroid.getContainedInId().equals(
								stopArea.getObjectId()))
						{
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("centroidOfArea", centroid);
							map.put("containedIn", centroid.getContainedInId());
							Detail errorItem = new Detail(
									STOP_AREA_6,
									new Location(sourceLocation,stopArea.getObjectId()), map);
							addValidationError(STOP_AREA_6, errorItem);
						}
					}
				}

			}
		}

	}

	private void validateITL()
	{
		Map<String, RoutingConstraint> routingConstraints = factory.getRoutingConstraints();
		Line line = referential.getLines().values().iterator().next();
		if (!routingConstraints.isEmpty())
		{

			// 2-NEPTUNE-ITL-3 : Check if ITL refers existing StopArea
			prepareCheckPoint(ITL_3);
			for (RoutingConstraint itl : routingConstraints.values())
			{
				FileLocation sourceLocation = locations.get(itl.getObjectId()+"_ITL");
				StopArea area = itl.getArea();
				if (area == null || area.getAreaType() == null)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("areaId", itl.getObjectId());
					map.put("name", itl.getName());
					Detail errorItem = new Detail(
							ITL_3,
							new Location(sourceLocation,itl.getObjectId()), map);
					addValidationError(ITL_3, errorItem);
				} else
				{
					// 2-NEPTUNE-ITL-4 : Check if ITL refers StopArea of ITL
					// type
					prepareCheckPoint(ITL_4);
					if (!area.getAreaType().equals(ChouetteAreaEnum.ITL))
					{
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("areaId", itl.getObjectId());
						map.put("type", area.getAreaType().toString());
						map.put("name", itl.getName());
						Detail errorItem = new Detail(
								ITL_4,
								new Location(sourceLocation,itl.getObjectId()), map);
						addValidationError(ITL_4, errorItem);

					}
				}

				// 2-NEPTUNE-ITL-5 : Check if ITL refers Line
				if (itl.getLine() != null)
				{
					prepareCheckPoint(ITL_5);
					if (itl.getLine().getObjectId() != line.getObjectId())
					{
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", itl.getName());
						map.put("lineId", line.getObjectId());
						map.put("lineIdShortCut", itl.getLine().getObjectId());
						Detail errorItem = new Detail(
								ITL_5,
								new Location(sourceLocation,itl.getObjectId()), map);
						addValidationError(ITL_5, errorItem);
					}

				}

			}
		}

	}


}
