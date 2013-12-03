package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.ChouetteAreaType;
import org.trident.schema.trident.ChouetteFacilityType;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ChouettePTNetworkType.AccessLink;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.AreaCentroid;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.StopArea;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint;
import org.trident.schema.trident.ChouettePTNetworkType.ConnectionLink;
import org.trident.schema.trident.CompanyType;
import org.trident.schema.trident.GroupOfLineType;
import org.trident.schema.trident.ITLType;
import org.trident.schema.trident.JourneyPatternType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.PTAccessPointType;
import org.trident.schema.trident.PTLinkType;
import org.trident.schema.trident.PTNetworkType;
import org.trident.schema.trident.TimeSlotType;
import org.trident.schema.trident.TimetableType;
import org.trident.schema.trident.TridentObjectType;
import org.trident.schema.trident.VehicleJourneyType;
import org.xml.sax.Locator;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class Level2Validator 
{
	private static final String NETWORK_1 = "2-NEPTUNE-Network-1";
	private static final String GROUP_OF_LINE_1 = "2-NEPTUNE-GroupOfLine-1";
	private static final String STOP_AREA_1 = "2-NEPTUNE-StopArea-1";
	private static final String STOP_AREA_2 = "2-NEPTUNE-StopArea-2";
	private static final String STOP_AREA_3 = "2-NEPTUNE-StopArea-3";
	private static final String STOP_AREA_4 = "2-NEPTUNE-StopArea-4";
	private static final String STOP_AREA_5 = "2-NEPTUNE-StopArea-5";
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";
	private static final String ITL_3 = "2-NEPTUNE-ITL-3";
	private static final String AREA_CENTROID_1 = "2-NEPTUNE-AreaCentroid-1";
	private static final String CONNECTION_LINK_1 = "2-NEPTUNE-ConnectionLink-1";
	private static final String ACCESS_POINT_1 = "2-NEPTUNE-AccessPoint-1";
	private static final String ACCESS_POINT_2 = "2-NEPTUNE-AccessPoint-2";
	private static final String ACCESS_POINT_3 = "2-NEPTUNE-AccessPoint-3";
	private static final String ACCESS_POINT_4 = "2-NEPTUNE-AccessPoint-4";
	private static final String ACCESS_POINT_5 = "2-NEPTUNE-AccessPoint-5";
	private static final String ACCESS_POINT_6 = "2-NEPTUNE-AccessPoint-6";
	private static final String ACCESS_LINK_1 = "2-NEPTUNE-AccessLink-1";
	private static final String LINE_1 = "2-NEPTUNE-Line-1";
	private static final String LINE_2 = "2-NEPTUNE-Line-2";
	private static final String ROUTE_1 = "2-NEPTUNE-Route-1";
	private static final String ROUTE_2 = "2-NEPTUNE-Route-2";
	private static final String ROUTE_3 = "2-NEPTUNE-Route-3";
	private static final String ROUTE_4 = "2-NEPTUNE-Route-4";
	private static final String ROUTE_5 = "2-NEPTUNE-Route-5";
	private static final String ROUTE_6 = "2-NEPTUNE-Route-6";
	private static final String ROUTE_7 = "2-NEPTUNE-Route-7";
	private static final String ROUTE_8 = "2-NEPTUNE-Route-8";
	private static final String ROUTE_9 = "2-NEPTUNE-Route-9";
	private static final String STOP_POINT_1 = "2-NEPTUNE-StopPoint-1";
	private static final String TIMETABLE_1 = "2-NEPTUNE-Timetable-1";
	private static final String TIMETABLE_2 = "2-NEPTUNE-Timetable-2";
	private static final String VEHICLE_JOURNEY_1 = "2-NEPTUNE-VehicleJourney-1";
	private static final String VEHICLE_JOURNEY_2 = "2-NEPTUNE-VehicleJourney-2";
	private static final String VEHICLE_JOURNEY_3 = "2-NEPTUNE-VehicleJourney-3";
	private static final String VEHICLE_JOURNEY_AT_STOP_1 = "2-NEPTUNE-VehicleJourneyAtStop-1";
	private static final String FACILITY_1 = "2-NEPTUNE-Facility-1";

	@Setter private  PTNetworkType ptNetwork;

	@Setter private ChouettePTNetworkType.ChouetteLineDescription.Line line;

	private Map<String,TridentObjectType> tridentObjects = new HashMap<String, TridentObjectType>();

	private Map<String,ChouettePTNetworkType.AccessLink> accessLinks = new HashMap<String,ChouettePTNetworkType.AccessLink>();
	public void addAccessLinks(List<ChouettePTNetworkType.AccessLink> accessLinkList) 
	{
		for (ChouettePTNetworkType.AccessLink accessLink : accessLinkList) 
		{
			accessLinks.put(accessLink.getObjectId(), accessLink);
			tridentObjects.put(accessLink.getObjectId(), accessLink);
		}
	}

	private Map<String,PTAccessPointType> accessPoints = new HashMap<String,PTAccessPointType>();
	public void addAccessPoints(List<PTAccessPointType> accessPointList) 
	{
		for (PTAccessPointType accessPoint : accessPointList) 
		{
			accessPoints.put(accessPoint.getObjectId(), accessPoint);
			tridentObjects.put(accessPoint.getObjectId(), accessPoint);
		}
	}

	private Map<String,ChouettePTNetworkType.ChouetteArea.AreaCentroid> areaCentroids = new HashMap<String,ChouettePTNetworkType.ChouetteArea.AreaCentroid>();
	public void addAreaCentroids(List<ChouettePTNetworkType.ChouetteArea.AreaCentroid> areaCentroidList) 
	{
		for (ChouettePTNetworkType.ChouetteArea.AreaCentroid areaCentroid : areaCentroidList) 
		{
			areaCentroids.put(areaCentroid.getObjectId(), areaCentroid);
			tridentObjects.put(areaCentroid.getObjectId(), areaCentroid);
		}
	}

	private Map<String,CompanyType> companies = new HashMap<String,CompanyType>();
	public void addCompanies(List<CompanyType> companyList) 
	{
		for (CompanyType company : companyList) {
			companies.put(company.getObjectId(), company);			
			tridentObjects.put(company.getObjectId(), company);
		}
	}

	private Map<String,ChouettePTNetworkType.ConnectionLink> connectionLinks = new HashMap<String,ChouettePTNetworkType.ConnectionLink>();
	public void addConnectionLinks(List<ChouettePTNetworkType.ConnectionLink> connectionLinkList) 
	{
		for (ChouettePTNetworkType.ConnectionLink connectionLink : connectionLinkList) {
			connectionLinks.put(connectionLink.getObjectId(), connectionLink);
			tridentObjects.put(connectionLink.getObjectId(), connectionLink);
		}
	}

	private Map<String,ChouetteFacilityType> facilities = new HashMap<String,ChouetteFacilityType>();
	public void addFacilities(List<ChouetteFacilityType> facilityList) 
	{
		for (ChouetteFacilityType facility : facilityList) {
			facilities.put(facility.getObjectId(), facility);
			tridentObjects.put(facility.getObjectId(), facility);
		}
	}

	private Map<String,GroupOfLineType> groupOfLines = new HashMap<String,GroupOfLineType>();
	public void addGroupOfLines(List<GroupOfLineType> groupOfLineList) 
	{
		for (GroupOfLineType groupOfLine : groupOfLineList) {
			groupOfLines.put(groupOfLine.getObjectId(), groupOfLine);
			tridentObjects.put(groupOfLine.getObjectId(), groupOfLine);
		}
	}

	private Map<String,JourneyPatternType> journeyPatterns = new HashMap<String,JourneyPatternType>();
	public void addJourneyPatterns(List<JourneyPatternType> journeyPatternList) 
	{
		for (JourneyPatternType journeyPattern : journeyPatternList) {
			journeyPatterns.put(journeyPattern.getObjectId(), journeyPattern);
			tridentObjects.put(journeyPattern.getObjectId(), journeyPattern);
		}
	}

	private Map<String,PTLinkType> ptLinks = new HashMap<String,PTLinkType>();
	private Map<String,List<PTLinkType>> mapPTLinksByStartId = new HashMap<String,List<PTLinkType>>();
	private Map<String,List<PTLinkType>> mapPTLinksByEndId = new HashMap<String,List<PTLinkType>>();

	public void addPTLinks(List<PTLinkType> ptLinkList) 
	{
		for (PTLinkType ptLink : ptLinkList) 
		{
			ptLinks.put(ptLink.getObjectId(), ptLink);
			tridentObjects.put(ptLink.getObjectId(), ptLink);
			{
				List<PTLinkType> list = mapPTLinksByStartId.get(ptLink.getStartOfLink());
				if (list == null)
				{
					list = new ArrayList<PTLinkType>();
					mapPTLinksByStartId.put(ptLink.getStartOfLink(),list);
				}
				list.add(ptLink);
			}
			{
				List<PTLinkType> list = mapPTLinksByEndId.get(ptLink.getEndOfLink());
				if (list == null)
				{
					list = new ArrayList<PTLinkType>();
					mapPTLinksByEndId.put(ptLink.getEndOfLink(),list);
				}
				list.add(ptLink);
			}
		}
	}

	private Map<String,ChouetteRoute> routes = new HashMap<String,ChouetteRoute>();
	public void addRoutes(List<ChouetteRoute> routeList) 
	{
		for (ChouetteRoute route : routeList) {
			routes.put(route.getObjectId(), route);
			tridentObjects.put(route.getObjectId(), route);
		}
	}

	private Map<String,ITLType> routingConstraints = new HashMap<String,ITLType>();
	public void addRoutingConstraints(List<ITLType> routingConstraintList) 
	{
		for (ITLType routingConstraint : routingConstraintList) {
			routingConstraints.put(routingConstraint.getAreaId(), routingConstraint);
		}
	}

	private Map<String,ChouettePTNetworkType.ChouetteArea.StopArea> stopAreas = new HashMap<String,ChouettePTNetworkType.ChouetteArea.StopArea>();
	private Map<String,String> mapStopAreaParent = new HashMap<String,String>();
	public void addStopAreas(List<ChouettePTNetworkType.ChouetteArea.StopArea> stopAreaList) 
	{
		for (ChouettePTNetworkType.ChouetteArea.StopArea stopArea : stopAreaList) {
			stopAreas.put(stopArea.getObjectId(), stopArea);
			tridentObjects.put(stopArea.getObjectId(), stopArea);
			if (!stopArea.getStopAreaExtension().getAreaType().equals(ChouetteAreaType.ITL))
			{
				for (String childId : stopArea.getContains()) 
				{
					mapStopAreaParent.put(childId,stopArea.getObjectId());
				}
			}
		}
	}

	private Map<String,ChouettePTNetworkType.ChouetteLineDescription.StopPoint> stopPoints = new HashMap<String,ChouettePTNetworkType.ChouetteLineDescription.StopPoint>();
	public void addStopPoints(List<ChouettePTNetworkType.ChouetteLineDescription.StopPoint> stopPointList) 
	{
		for (ChouettePTNetworkType.ChouetteLineDescription.StopPoint stopPoint : stopPointList) {
			stopPoints.put(stopPoint.getObjectId(), stopPoint);
			tridentObjects.put(stopPoint.getObjectId(), stopPoint);
		}
	}

	private Map<String,TimeSlotType> timeSlots = new HashMap<String,TimeSlotType>();
	public void addTimeSlots(List<TimeSlotType> timeSlotList) 
	{
		for (TimeSlotType timeSlot : timeSlotList) {
			timeSlots.put(timeSlot.getObjectId(), timeSlot);
			tridentObjects.put(timeSlot.getObjectId(), timeSlot);
		}
	}

	private Map<String,TimetableType> timetables = new HashMap<String,TimetableType>();
	public void addTimetables(List<TimetableType> timetableList) 
	{
		for (TimetableType timetable : timetableList) {
			timetables.put(timetable.getObjectId(), timetable);
			tridentObjects.put(timetable.getObjectId(), timetable);
		}
	}

	private Map<String,VehicleJourneyType> vehicleJourneys = new HashMap<String,VehicleJourneyType>();

	public void addVehicleJourneys(List<VehicleJourneyType> vehicleJourneyList) 
	{
		for (VehicleJourneyType vehicleJourney : vehicleJourneyList) {
			vehicleJourneys.put(vehicleJourney.getObjectId(), vehicleJourney);
			tridentObjects.put(vehicleJourney.getObjectId(), vehicleJourney);
		}
	}

	private String sourceFile;
	private PhaseReportItem validationReport;

	public Level2Validator(String sourceFile,PhaseReportItem validationReport)
	{
		this.sourceFile = sourceFile;
		this.validationReport = validationReport;
	}


	public void validate()
	{
		validateNetwork();
		validateGroupOfLines();
		validateStopAreas();
		validateAreaCentroids();
		validateConnectionLinks();
		validateAccessPoints();
		validateAccessLink();
		validateLine();
		validateRoutes();
		validateStopPoints();
		validateTimetables();
		validateVehicleJourneys();
		validateFacilities();
	}

	private void validateNetwork()
	{
		// 2-NEPTUNE-PtNetwork-1 : check if lineId of line is present in list
		if (!isListEmpty(ptNetwork.getLineId()))
		{
			prepareCheckPoint(NETWORK_1);
			String lineId = line.getObjectId();
			if (!ptNetwork.getLineId().contains(lineId))
			{
				Locator srcLoc = ptNetwork.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, srcLoc.getLineNumber(), srcLoc.getColumnNumber(), "lineId", lineId);
				DetailReportItem detail = new DetailReportItem(ptNetwork.getObjectId(), Report.STATE.WARNING, location );
				addValidationError(NETWORK_1, detail);
			}
		}
	}

	private void validateGroupOfLines()
	{
		// 2-NEPTUNE-GroupOfLine-1 : check if lineId of line is present in list
		for (GroupOfLineType groupOfLine : groupOfLines.values()) 
		{
			if (!isListEmpty(groupOfLine.getLineId()))
			{
				prepareCheckPoint(GROUP_OF_LINE_1);
				String lineId = line.getObjectId();
				if (!groupOfLine.getLineId().contains(lineId))
				{
					Locator srcLoc = groupOfLine.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, srcLoc.getLineNumber(), srcLoc.getColumnNumber(), "lineId", lineId);
					DetailReportItem detail = new DetailReportItem(groupOfLine.getObjectId(), Report.STATE.WARNING, location );
					addValidationError(GROUP_OF_LINE_1, detail);
				}
			}
		}
	}

	private void validateStopAreas()
	{
		if (!stopAreas.isEmpty()) prepareCheckPoint(STOP_AREA_1);
		for (StopArea stopArea : stopAreas.values()) 
		{
			// 2-NEPTUNE-StopArea-1 : check if StopArea refers in field contains only stopareas or stoppoints
			for (String childId : stopArea.getContains()) 
			{
				TridentObjectType trdObject = tridentObjects.get(childId);
				if (trdObject == null) continue; // external object, cannot check
				if (!(trdObject instanceof StopArea) && !(trdObject instanceof StopPoint))
				{
					// wrong reference type
					Locator trdLocation = stopArea.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"contains",childId);
					DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(STOP_AREA_1, errorItem);		
				}
				else
				{
					switch(stopArea.getStopAreaExtension().getAreaType())
					{
					case STOP_PLACE: 
						prepareCheckPoint(STOP_AREA_2);
						// 2-NEPTUNE-StopArea-2 : if stoparea is StopPlace : check if it refers only stopareas of type stopplace or commercialstoppoints
						if (trdObject instanceof StopPoint || 
								(!typeOfStopArea(trdObject).equals(ChouetteAreaType.STOP_PLACE) 
										&& !typeOfStopArea(trdObject).equals(ChouetteAreaType.COMMERCIAL_STOP_POINT)))
						{
							// wrong reference type
							Locator trdLocation = stopArea.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"contains",childId);
							DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(STOP_AREA_2, errorItem);		
						}
						break;
					case COMMERCIAL_STOP_POINT: 
						prepareCheckPoint(STOP_AREA_3);
						// 2-NEPTUNE-StopArea-3 : if stoparea is commercialStopPoint : check if it refers only stopareas of type quay or boardingPosition
						if (trdObject instanceof StopPoint || 
								(!typeOfStopArea(trdObject).equals(ChouetteAreaType.QUAY) 
										&& !typeOfStopArea(trdObject).equals(ChouetteAreaType.BOARDING_POSITION)))
						{
							// wrong reference type
							Locator trdLocation = stopArea.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"contains",childId);
							DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(STOP_AREA_3, errorItem);		
						}
						break;
					case QUAY :
					case BOARDING_POSITION:
						prepareCheckPoint(STOP_AREA_4);
						// 2-NEPTUNE-StopArea-4 : if stoparea is quay or boardingPosition : check if it refers only StopPoints
						if (trdObject instanceof StopArea)
						{
							// wrong reference type
							Locator trdLocation = stopArea.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"contains",childId);
							DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(STOP_AREA_4, errorItem);		
						}
						break;
					case ITL :
						prepareCheckPoint(ITL_1);
						// 2-NEPTUNE-ITL-1 : if stoparea is ITL : check if it refers only non ITL stopAreas
						if (trdObject instanceof StopPoint || typeOfStopArea(trdObject).equals(ChouetteAreaType.ITL) )
						{
							// wrong reference type
							Locator trdLocation = stopArea.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"contains",childId);
							DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(ITL_1, errorItem);		
						}
						break;
					}
				}
			}	

			switch(stopArea.getStopAreaExtension().getAreaType())
			{
			case ITL: 
				// 2-NEPTUNE-ITL-2 : if stoparea is ITL : check if a ITLType object refers it
			{
				prepareCheckPoint(ITL_2);
				boolean found = false;
				for (ITLType routingConstraint : routingConstraints.values()) 
				{
					if (routingConstraint.getAreaId().equals(stopArea.getObjectId())) 
					{
						found = true;
						break;
					}
					if (!found)
					{
						// unused ITL Stop
						Locator trdLocation = stopArea.sourceLocation();
						ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
						DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
						addValidationError(ITL_2, errorItem);		
					}

				}
			}
			break;
			case STOP_PLACE:
			case COMMERCIAL_STOP_POINT:
			case QUAY:
			case BOARDING_POSITION:
				prepareCheckPoint(STOP_AREA_5);
				// 2-NEPTUNE-StopArea-5 : if stoparea is not ITL : check if it refers an existing araacentroid which refers the good stoparea.
				AreaCentroid centroid = areaCentroids.get(stopArea.getCentroidOfArea());
				if (!centroid.getContainedIn().equals(stopArea.getObjectId()))
				{
					Locator trdLocation = stopArea.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"centroidOfArea",stopArea.getCentroidOfArea());
					DetailReportItem errorItem = new DetailReportItem(stopArea.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(STOP_AREA_5, errorItem);		                	
				}
				break;
			}


		}

		// 2-NEPTUNE-ITL-3 : Check if ITL refers StopArea of ITL type
		if (!routingConstraints.isEmpty()) prepareCheckPoint(ITL_3);
		for (ITLType itl : routingConstraints.values()) 
		{
			StopArea area = stopAreas.get(itl.getAreaId());
			if (area == null || !typeOfStopArea(area).equals(ChouetteAreaType.ITL))
			{
				Locator trdLocation = itl.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"areaId",itl.getAreaId());
				DetailReportItem errorItem = new DetailReportItem(itl.getAreaId(), Report.STATE.ERROR, location );
				addValidationError(ITL_3, errorItem);		                	

			}
		}

	}

	private void validateAreaCentroids()
	{
		if (areaCentroids.isEmpty()) return;
		// 2-NEPTUNE-AreaCentroid-1 : check centroid projection type as WSG84
		prepareCheckPoint(AREA_CENTROID_1);
		for (AreaCentroid centroid : areaCentroids.values())
		{
			if (centroid.getLongLatType().equals(LongLatTypeType.WGS_84)) continue;
			Locator trdLocation = centroid.sourceLocation();
			ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"longLatType",centroid.getLongLatType().toString());
			DetailReportItem errorItem = new DetailReportItem(centroid.getObjectId(), Report.STATE.ERROR, location );
			addValidationError(AREA_CENTROID_1, errorItem);		                	
		}

	}

	private void validateConnectionLinks()
	{
		if (connectionLinks.isEmpty()) return;
		// 2-NEPTUNE-ConnectionLink-1 : check presence of start or end of link
		prepareCheckPoint(CONNECTION_LINK_1);
		for (ConnectionLink connectionLink : connectionLinks.values())
		{
			if (stopAreas.get(connectionLink.getStartOfLink())!= null || stopAreas.get(connectionLink.getEndOfLink()) != null) continue; 
			Locator trdLocation = connectionLink.sourceLocation();
			ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
			DetailReportItem errorItem = new DetailReportItem(connectionLink.getObjectId(), Report.STATE.ERROR, location );
			addValidationError(CONNECTION_LINK_1, errorItem);		                	
		}

	}
	private void validateAccessPoints()
	{
		if (accessPoints.isEmpty()) return;

		// build a map on link connected ids 
		Map<String,List<AccessLink>> mapAccessLinkByAccessPointId = new HashMap<String,List<AccessLink>>();
		for (AccessLink link : accessLinks.values()) 
		{
			{
				String id = link.getStartOfLink();
				List<AccessLink> list = mapAccessLinkByAccessPointId.get(id);
				if (list == null)
				{
					list = new ArrayList<AccessLink>();
					mapAccessLinkByAccessPointId.put(id, list);
				}
				list.add(link);
			}
			{
				String id = link.getEndOfLink();
				List<AccessLink> list = mapAccessLinkByAccessPointId.get(id);
				if (list == null)
				{
					list = new ArrayList<AccessLink>();
					mapAccessLinkByAccessPointId.put(id, list);
				}
				list.add(link);
			}
		}

		prepareCheckPoint(ACCESS_POINT_1);
		prepareCheckPoint(ACCESS_POINT_2); 
		prepareCheckPoint(ACCESS_POINT_6);

		for (PTAccessPointType accessPoint : accessPoints.values())
		{
			// 2-NEPTUNE-AccessPoint-1 : check type of containedIn stopArea
			StopArea parent = stopAreas.get(accessPoint.getContainedIn());
			if (parent == null || typeOfStopArea(parent).equals(ChouetteAreaType.ITL))
			{
				Locator trdLocation = accessPoint.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"containedIn",accessPoint.getContainedIn());
				DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
				addValidationError(ACCESS_POINT_1, errorItem);
			}

			// 2-NEPTUNE-AccessPoint-2 : check presence of access links
			List<AccessLink> links = mapAccessLinkByAccessPointId.get(accessPoint.getObjectId());
			if (links == null)
			{
				Locator trdLocation = accessPoint.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
				DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
				addValidationError(ACCESS_POINT_2, errorItem);
			}

			boolean startFound = false;
			boolean endFound = false;
			String objectId = accessPoint.getObjectId();
			for (AccessLink link : links) 
			{
				if (link.getStartOfLink().equals(objectId)) startFound = true;
				if (link.getEndOfLink().equals(objectId)) endFound = true;
			}

			if (accessPoint.getType().equalsIgnoreCase("in"))
			{
				// 2-NEPTUNE-AccessPoint-3 : if type in : check only accesslinks on start
				prepareCheckPoint(ACCESS_POINT_3);
				if (endFound)
				{
					Locator trdLocation = accessPoint.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
					DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ACCESS_POINT_3, errorItem);
				}
			}
			else if (accessPoint.getType().equalsIgnoreCase("out"))
			{
				// 2-NEPTUNE-AccessPoint-4 : if type out : check only accesslinks on end
				prepareCheckPoint(ACCESS_POINT_4);
				if (startFound)
				{
					Locator trdLocation = accessPoint.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
					DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ACCESS_POINT_4, errorItem);
				}

			}
			else // inout
			{
				// 2-NEPTUNE-AccessPoint-5 : if type out : check minimum one accessLink in each direction
				prepareCheckPoint(ACCESS_POINT_5);
				if (!startFound || !endFound)
				{
					Locator trdLocation = accessPoint.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
					DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ACCESS_POINT_5, errorItem);
				}
			}

			// 2-NEPTUNE-AccessPoint-7 : check centroid projection type as WSG84
			if (accessPoint.getLongLatType().equals(LongLatTypeType.WGS_84)) continue;
			Locator trdLocation = accessPoint.sourceLocation();
			ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(), "longLatType",accessPoint.getLongLatType().toString());
			DetailReportItem errorItem = new DetailReportItem(accessPoint.getObjectId(), Report.STATE.ERROR, location );
			addValidationError(ACCESS_POINT_6, errorItem);		                	
		}

	}

	private void validateAccessLink()
	{
		if (accessLinks.isEmpty()) return;
		// 2-NEPTUNE-AccessLink-1 : check one target as accesspoint and other as stoparea 
		prepareCheckPoint(ACCESS_LINK_1);
		for (AccessLink link : accessLinks.values())
		{
			TridentObjectType startObject = tridentObjects.get(link.getStartOfLink());
			TridentObjectType endObject = tridentObjects.get(link.getEndOfLink());

			if (startObject instanceof StopArea && endObject instanceof PTAccessPointType) continue;
			if (startObject instanceof PTAccessPointType && endObject instanceof StopArea) continue;
			Locator trdLocation = link.sourceLocation();
			ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
			DetailReportItem errorItem = new DetailReportItem(link.getObjectId(), Report.STATE.ERROR, location );
			addValidationError(ACCESS_LINK_1, errorItem);		                	

		}


	}

	private void validateLine()
	{
		if (!line.getLineEnd().isEmpty())
		{
			// 2-NEPTUNE-Line-1 : check ends of line  
			prepareCheckPoint(LINE_1);
			for (String endId : line.getLineEnd()) 
			{
				// endId must be referenced by one and only one ptLink
				List<PTLinkType> startLinks = mapPTLinksByStartId.get(endId);
				List<PTLinkType> endLinks = mapPTLinksByEndId.get(endId);
				boolean oneRef = true;
				if (startLinks == null && endLinks == null) 
				{
					oneRef = false;
				}
				else if (startLinks == null && endLinks.size() != 1)
				{
					oneRef = false;
				}
				else if (endLinks == null && startLinks.size() != 1)
				{
					oneRef = false;
				}
				if (!oneRef)
				{
					Locator trdLocation = line.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"lineEnd",endId);
					DetailReportItem errorItem = new DetailReportItem(line.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(LINE_1, errorItem);		                	

				}

			}

		}
		// 2-NEPTUNE-Line-2 : check routes references  
		prepareCheckPoint(LINE_2);
		for (String routeId : routes.keySet()) 
		{
			if (!line.getRouteId().contains(routeId))
			{
				Locator trdLocation = line.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"routeId",routeId);
				DetailReportItem errorItem = new DetailReportItem(line.getObjectId(), Report.STATE.ERROR, location );
				addValidationError(LINE_2, errorItem);		                	
			}
		}

	}

	private void validateRoutes()
	{
		boolean route1ok = true;
		boolean route2ok = true;
		Map<String,String> ptLinkInRoute = new HashMap<String,String>();
		prepareCheckPoint(ROUTE_1);
		for (ChouetteRoute route : routes.values())
		{
			// 2-NEPTUNE-Route-1 : check if ptlink is contained in only one route
			for (String ptLinkId : route.getPtLinkId()) 
			{
				if (ptLinkInRoute.get(ptLinkId) != null)
				{
					route1ok = false;
					// ptlink is referenced by more than one route
					Locator trdLocation = route.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"ptLinkId",ptLinkId);
					DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ROUTE_1, errorItem);		                	
				}
				else
				{
					ptLinkInRoute.put(ptLinkId,route.getObjectId());
				}
			}
		}

		// 2-NEPTUNE-Route-2 : check stoppoint in no more than one start of ptlink and one end
		prepareCheckPoint(ROUTE_2);
		for (String stopPointId : mapPTLinksByStartId.keySet()) 
		{
			List<PTLinkType> ptLinkOfStop = mapPTLinksByStartId.get(stopPointId);
			if (ptLinkOfStop == null || ptLinkOfStop.size() == 1) continue;
			for (PTLinkType ptLinkType : ptLinkOfStop) 
			{
				route2ok = false;
				Locator trdLocation = ptLinkType.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"startOfLink",stopPointId);
				DetailReportItem errorItem = new DetailReportItem(ptLinkType.getObjectId(), Report.STATE.ERROR, location );
				addValidationError(ROUTE_2, errorItem);		                	
			}		
		}
		for (String stopPointId : mapPTLinksByEndId.keySet()) 
		{
			List<PTLinkType> ptLinkOfStop = mapPTLinksByEndId.get(stopPointId);
			if (ptLinkOfStop == null || ptLinkOfStop.size() == 1) continue;
			for (PTLinkType ptLinkType : ptLinkOfStop) 
			{
				Locator trdLocation = ptLinkType.sourceLocation();
				ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"endOfLink",stopPointId);
				DetailReportItem errorItem = new DetailReportItem(ptLinkType.getObjectId(), Report.STATE.ERROR, location );
				addValidationError(ROUTE_2, errorItem);		                	
			}		
		}

		if (!journeyPatterns.isEmpty())
		{
			prepareCheckPoint(ROUTE_4);
			for (JourneyPatternType journeyPattern : journeyPatterns.values()) 
			{
				ChouetteRoute route = routes.get(journeyPattern.getRouteId());
				// 2-NEPTUNE-Route-4 : check cross reference between journeypattern and route
				if (!route.getJourneyPatternId().contains(journeyPattern.getObjectId()))
				{
					Locator trdLocation = route.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"journeyPatternId",journeyPattern.getObjectId());
					DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ROUTE_4, errorItem);		                	
				}

			}
		}

		if (route1ok && route2ok)
		{

			Map<String,List<String>> sequenceOfRoutes = new HashMap<String,List<String>>();
			prepareCheckPoint(ROUTE_3);

			for (ChouetteRoute route : routes.values()) 
			{
				// 2-NEPTUNE-Route-3 : check if stoppoints build a linear route
				List<String> pointIds = new ArrayList<String>();
				// find first stop : does not appears as end of link
				for (String linkId : route.getPtLinkId()) 
				{
					PTLinkType link = ptLinks.get(linkId);
					if (!mapPTLinksByEndId.containsKey(link.getStartOfLink()))
					{
						if (pointIds.isEmpty())
						{
							pointIds.add(link.getStartOfLink());
						}
						else
						{
							// broken route !
							Locator trdLocation = route.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"ptLinkId",link.getObjectId());
							DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(ROUTE_3, errorItem);		                	

						}
					}
				}
				if (pointIds.isEmpty())
				{
					// no first id : circle route 
					Locator trdLocation = route.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
					DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ROUTE_3, errorItem);	
					continue;
				}
				String start = pointIds.get(0); 
				while (pointIds.size() < route.getPtLinkId().size() + 2)
				{
					List<PTLinkType> links = mapPTLinksByStartId.get(start);
					if (links == null) break; // normal loop exit
					start = links.get(0).getEndOfLink();
					pointIds.add(start);
				}

				if (pointIds.size() != route.getPtLinkId().size() + 1)
				{
					// ptlinks does not build complete stop sequence
					Locator trdLocation = route.sourceLocation();
					ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
					DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
					addValidationError(ROUTE_3, errorItem);	
					continue;
				}

				sequenceOfRoutes.put(route.getObjectId(), pointIds);

				if (!journeyPatterns.isEmpty())
				{
					prepareCheckPoint(ROUTE_5);
					prepareCheckPoint(ROUTE_6);	

					List<String> unusedPointIds = new ArrayList<String>(pointIds);

					for (String jpId : route.getJourneyPatternId()) 
					{
						JourneyPatternType jp = journeyPatterns.get(jpId);
						// 2-NEPTUNE-Route-5 : check journey pattern stoppoints included in route stoppoints
						if (!pointIds.containsAll(jp.getStopPointList()))
						{
							Locator trdLocation = jp.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber());
							DetailReportItem errorItem = new DetailReportItem(jp.getObjectId(), Report.STATE.ERROR, location );
							addValidationError(ROUTE_5, errorItem);	
						}
						unusedPointIds.removeAll(jp.getStopPointList());
					}

					// 2-NEPTUNE-Route-6 : check usage of stoppoint in a journeypattern of route (W)
					if (!unusedPointIds.isEmpty())
					{
						for (String stopPointId : unusedPointIds) 
						{
							Locator trdLocation = route.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"stopPointId",stopPointId);
							DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.WARNING, location );
							addValidationError(ROUTE_6, errorItem);	

						}
					}
				}
			}



			for (ChouetteRoute route : routes.values())
			{
				if (route.getWayBackRouteId() != null)
				{
					prepareCheckPoint(ROUTE_7);	
					ChouetteRoute wayBackRoute = routes.get(route.getWayBackRouteId());
					// 2-NEPTUNE-Route-7 : check cross reference of wayback routes 
					if (wayBackRoute.getWayBackRouteId() == null || !wayBackRoute.getWayBackRouteId().equals(route.getObjectId()))
					{
						Locator trdLocation = route.sourceLocation();
						ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"waybackRouteId",route.getWayBackRouteId());
						DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.ERROR, location );
						addValidationError(ROUTE_7, errorItem);						
					}

					// 2-NEPTUNE-Route-8 : check orientation of wayback routes (W)
					if (route.getRouteExtension()!= null && wayBackRoute.getRouteExtension() != null)
					{
						prepareCheckPoint(ROUTE_8);	
						String wk1 = route.getRouteExtension().getWayBack().toLowerCase().substring(0,1);
						String wk2 = wayBackRoute.getRouteExtension().getWayBack().toLowerCase().substring(0,1);
						if (wk1.equals(wk2))
						{
							Locator trdLocation = route.sourceLocation();
							ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"waybackRouteId",route.getWayBackRouteId());
							DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.WARNING, location );
							addValidationError(ROUTE_8, errorItem);						

						}
					}
					// 2-NEPTUNE-Route-9 : check terminus of wayback routes (W)
					List<String> pointIds = sequenceOfRoutes.get(route.getObjectId());
					List<String> wbPointIds = sequenceOfRoutes.get(wayBackRoute.getObjectId());
					if (pointIds != null && wbPointIds != null)
					{
						prepareCheckPoint(ROUTE_9);	
						// check start of route (end will be tested on wayback check
						StopPoint start = stopPoints.get(pointIds.get(0));
						StopPoint end = stopPoints.get(wbPointIds.get(wbPointIds.size()-1));
						if (end.getContainedIn().equals(start.getContainedIn())) continue;
                        String startParentCommercialId = mapStopAreaParent.get(start.getContainedIn());
                        String endParentCommercialId = mapStopAreaParent.get(end.getContainedIn());
						if (startParentCommercialId == null || endParentCommercialId == null) continue;
						if (startParentCommercialId.equals(endParentCommercialId)) continue;
						// warning 
						Locator trdLocation = route.sourceLocation();
						ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"stopPointId",pointIds.get(0));
						DetailReportItem errorItem = new DetailReportItem(route.getObjectId(), Report.STATE.WARNING, location );
						addValidationError(ROUTE_9, errorItem);						
						
					}
				}

			}
		}

	}

	private void validateStopPoints()
	{
		if (stopPoints.isEmpty()) return;
		// 2-NEPTUNE-StopPoint-1 : check stopPoint projection type as WSG84
		prepareCheckPoint(STOP_POINT_1);
		for (StopPoint point : stopPoints.values())
		{
			if (point.getLongLatType().equals(LongLatTypeType.WGS_84)) continue;
			Locator trdLocation = point.sourceLocation();
			ReportLocation location = new ReportLocation(sourceFile, trdLocation.getLineNumber(), trdLocation.getColumnNumber(),"longLatType",point.getLongLatType().toString());
			DetailReportItem errorItem = new DetailReportItem(point.getObjectId(), Report.STATE.ERROR, location );
			addValidationError(STOP_POINT_1, errorItem);		                	
		}

	}

	private void validateTimetables()
	{
		// 2-NEPTUNE-Timetable-1 : check if timetable refers at least one existing vehiclejourney
		// 2-NEPTUNE-Timetable-2 : check if vehiclejourney is referred by at least one timetable

	}

	private void validateVehicleJourneys()
	{
		// 2-NEPTUNE-VehicleJourney-1 : check if route and JourneyPattern are coherent
		// 2-NEPTUNE-VehicleJourney-2 : check if journeypatterns have at least one vehiclejourney
		// 2-NEPTUNE-VehicleJourneyAtStop-1 : check if stoppoints are coherent with journeyPattern

	}

	private void validateFacilities()
	{
		// 2-NEPTUNE-Facility-1"; WGS84

	}


	private ChouetteAreaType typeOfStopArea(TridentObjectType object)
	{
		if (object instanceof StopArea)
		{
			StopArea area = (StopArea) object;
			return area.getStopAreaExtension().getAreaType();
		}
		return null;
	}


	private boolean isListEmpty(List<?> list)
	{
		return list == null || list.isEmpty();
	}

	protected void addValidationError(String checkPointKey,DetailReportItem item)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		checkPoint.addItem(item);

	}

	protected void prepareCheckPoint(String checkPointKey)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		if (!checkPoint.hasItems()) checkPoint.setStatus(Report.STATE.OK);
	}


}
