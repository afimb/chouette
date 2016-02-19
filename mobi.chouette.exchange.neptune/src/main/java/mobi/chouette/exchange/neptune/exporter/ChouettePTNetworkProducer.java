package mobi.chouette.exchange.neptune.exporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.neptune.exporter.producer.AbstractJaxbNeptuneProducer;
import mobi.chouette.exchange.neptune.exporter.producer.AccessLinkProducer;
import mobi.chouette.exchange.neptune.exporter.producer.AccessPointProducer;
import mobi.chouette.exchange.neptune.exporter.producer.AreaCentroidProducer;
import mobi.chouette.exchange.neptune.exporter.producer.CompanyProducer;
import mobi.chouette.exchange.neptune.exporter.producer.ConnectionLinkProducer;
import mobi.chouette.exchange.neptune.exporter.producer.GroupOfLineProducer;
import mobi.chouette.exchange.neptune.exporter.producer.JourneyPatternProducer;
import mobi.chouette.exchange.neptune.exporter.producer.LineProducer;
import mobi.chouette.exchange.neptune.exporter.producer.PTLinkProducer;
import mobi.chouette.exchange.neptune.exporter.producer.PTNetworkProducer;
import mobi.chouette.exchange.neptune.exporter.producer.RouteProducer;
import mobi.chouette.exchange.neptune.exporter.producer.RoutingConstraintProducer;
import mobi.chouette.exchange.neptune.exporter.producer.StopAreaProducer;
import mobi.chouette.exchange.neptune.exporter.producer.StopPointProducer;
import mobi.chouette.exchange.neptune.exporter.producer.TimeSlotProducer;
import mobi.chouette.exchange.neptune.exporter.producer.TimetableProducer;
import mobi.chouette.exchange.neptune.exporter.producer.VehicleJourneyProducer;
import mobi.chouette.exchange.neptune.exporter.util.NeptuneObjectUtil;
import mobi.chouette.exchange.neptune.jaxb.JaxbNeptuneFileConverter;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ChouetteAreaEnum;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.CompanyType;
import org.trident.schema.trident.GroupOfLineType;
import org.trident.schema.trident.ITLType;
import org.trident.schema.trident.JourneyPatternType;
import org.trident.schema.trident.PTLinkType;
import org.trident.schema.trident.TimeSlotType;
import org.trident.schema.trident.TimetableType;
import org.trident.schema.trident.VehicleJourneyType;


public class ChouettePTNetworkProducer implements Constant {

	private static LineProducer lineProducer = new LineProducer();
	private static PTNetworkProducer networkProducer = new PTNetworkProducer();
	private static RouteProducer routeProducer = new RouteProducer();
	private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
	private static VehicleJourneyProducer vehicleJourneyProducer = new VehicleJourneyProducer();
	private static StopPointProducer stopPointProducer = new StopPointProducer();
	private static PTLinkProducer ptLinkProducer = new PTLinkProducer();
	private static CompanyProducer companyProducer = new CompanyProducer();
	private static StopAreaProducer stopAreaProducer = new StopAreaProducer();
	private static AreaCentroidProducer areaCentroidProducer = new AreaCentroidProducer();
	private static ConnectionLinkProducer connectionLinkProducer = new ConnectionLinkProducer();
	private static TimetableProducer timetableProducer = new TimetableProducer();
	private static RoutingConstraintProducer routingConstraintProducer = new RoutingConstraintProducer();
	private static GroupOfLineProducer groupOfLineProducer = new GroupOfLineProducer();
	private static AccessPointProducer accessPointProducer = new AccessPointProducer();
	private static AccessLinkProducer accessLinkProducer = new AccessLinkProducer();
	//	private static FacilityProducer facilityProducer = new FacilityProducer();
	private static TimeSlotProducer timeSlotProducer = new TimeSlotProducer();

	public void produce(Context context) throws Exception
	{
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();

		NeptuneExportParameters parameters = (NeptuneExportParameters) context.get(CONFIGURATION);
		boolean addExtension = parameters.isAddExtension();
		String projectionType = parameters.getProjectionType();
		if (projectionType != null && !projectionType.isEmpty())
		{
			if (!projectionType.toUpperCase().startsWith("EPSG:"))
				projectionType = "EPSG:"+projectionType;
		}
		Metadata metadata = (Metadata) context.get(METADATA); 

		ChouettePTNetworkType rootObject = AbstractJaxbNeptuneProducer.tridentFactory.createChouettePTNetworkType();
        if (collection.getLine().getNetwork() != null)
		    rootObject.setPTNetwork(networkProducer.produce(collection.getLine().getNetwork(),addExtension));
		for (GroupOfLine group : collection.getGroupOfLines())
		{
			GroupOfLineType jaxbObj = groupOfLineProducer.produce(group,addExtension);
			jaxbObj.getLineId().add(collection.getLine().getObjectId());
			rootObject.getGroupOfLine().add(jaxbObj);
		}

		for (Company company : collection.getCompanies())
		{
			CompanyType jaxbObj = companyProducer.produce(company,addExtension);
			rootObject.getCompany().add(jaxbObj);
		}

		ChouetteArea chouetteArea = new ChouetteArea();
		for (StopArea stopArea : collection.getStopAreas())
		{
			stopArea.toProjection(projectionType);
			ChouetteArea.StopArea jaxbStopArea = stopAreaProducer.produce(stopArea,addExtension);
			// add children reference only for exported ones
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				for (StopArea child : stopArea.getRoutingConstraintAreas())
				{
					if (collection.getStopAreas().contains(child))
					{
						jaxbStopArea.getContains().add(child.getObjectId());
					}
				}
			} else
			{
				for (StopArea child : stopArea.getContainedStopAreas())
				{
					if (collection.getStopAreas().contains(child))
					{
						jaxbStopArea.getContains().add(child.getObjectId());
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints())
				{
					if (collection.getStopPoints().contains(child))
					{
						jaxbStopArea.getContains().add(child.getObjectId());
					}
				}
			}
			if (metadata != null && stopArea.hasCoordinates())
				metadata.getSpatialCoverage().update(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());
			if (stopArea.hasAddress() || stopArea.hasCoordinates() || stopArea.hasProjection())
			{
				ChouetteArea.AreaCentroid centroid = areaCentroidProducer.produce(stopArea,addExtension);
				chouetteArea.getAreaCentroid().add(centroid);
				jaxbStopArea.setCentroidOfArea(centroid.getObjectId());
			}
			chouetteArea.getStopArea().add(jaxbStopArea);
		}
		rootObject.setChouetteArea(chouetteArea); 

		for (ConnectionLink connectionLink : collection.getConnectionLinks())
		{
			rootObject.getConnectionLink().add(connectionLinkProducer.produce(connectionLink,addExtension));
		}

		for (Timetable timetable : collection.getTimetables())
		{
			timetable.computeLimitOfPeriods();

			TimetableType jaxbObj = timetableProducer.produce(timetable,addExtension);
			rootObject.getTimetable().add(jaxbObj);
			// add vehiclejourney only for exported ones
			for (VehicleJourney vehicleJourney : collection.getVehicleJourneys()) {
				if (vehicleJourney.getTimetables().contains(timetable))
				{
					jaxbObj.getVehicleJourneyId().add(vehicleJourney.getObjectId());					
				}
			}
			if (metadata != null)
				metadata.getTemporalCoverage().update(timetable.getStartOfPeriod(), timetable.getEndOfPeriod());
		}

		ChouetteLineDescription chouetteLineDescription = new ChouetteLineDescription();
		ChouetteLineDescription.Line jaxbLine = lineProducer.produce(collection.getLine(),collection.getRoutes(),addExtension);
		chouetteLineDescription.setLine(jaxbLine);
		rootObject.setChouetteLineDescription(chouetteLineDescription);

		if (collection.getLine().getRoutingConstraints() != null)
		{
			for (StopArea routingConstraint : collection.getLine().getRoutingConstraints())
			{
				ITLType jaxbITL = routingConstraintProducer.produceITL(collection.getLine(), routingConstraint,addExtension);
				chouetteLineDescription.getITL().add(jaxbITL);
			}
		}

		for (Route route : collection.getRoutes())
		{
			ChouetteRoute jaxbObj = routeProducer.produce(route,collection.getRoutes(),addExtension);
			// reduce journeyPatternId at exported ones
			jaxbObj.getJourneyPatternId().clear();
			for (JourneyPattern jp : route.getJourneyPatterns())
			{
				if (collection.getJourneyPatterns().contains(jp))
				{
					jaxbObj.getJourneyPatternId().add(jp.getObjectId());
				}
			}
			// add ptLinks 
			List<PTLink> ptLinks = NeptuneObjectUtil.getPtLinks(route);
			jaxbObj.getPtLinkId().addAll(NeptuneObjectUtil.extractObjectIds(ptLinks));
			for (PTLink ptLink : ptLinks) 
			{
				PTLinkType jaxbLink = ptLinkProducer.produce(ptLink, addExtension);
				chouetteLineDescription.getPtLink().add(jaxbLink);
			}
			chouetteLineDescription.getChouetteRoute().add(jaxbObj);
		}
		for (JourneyPattern journeyPattern : collection.getJourneyPatterns())
		{
			JourneyPatternType jaxbObj = journeyPatternProducer.produce(journeyPattern,addExtension);
			chouetteLineDescription.getJourneyPattern().add(jaxbObj);
		}
		for (StopPoint stopPoint : collection.getStopPoints())
		{
			org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint jaxbObj = stopPointProducer.produce(stopPoint,addExtension);
			chouetteLineDescription.getStopPoint().add(jaxbObj);
		}
		for (VehicleJourney vehicleJourney : collection.getVehicleJourneys())
		{
			
			List<JourneyFrequency> journeyFrequencies = vehicleJourney.getJourneyFrequencies();
			if (journeyFrequencies != null && !journeyFrequencies.isEmpty()) {
				int count = 0;
				for (JourneyFrequency journeyFrequency : journeyFrequencies) {
					Timeband timeband = journeyFrequency.getTimeband();
					TimeSlot timeSlot = new TimeSlot();
					if (timeband != null) {
						timeSlot.setBeginningSlotTime(timeband.getStartTime());
						timeSlot.setEndSlotTime(timeband.getEndTime());
						timeSlot.setObjectVersion(timeband.getObjectVersion());
						timeSlot.setCreationTime(timeband.getCreationTime());
						timeSlot.setCreatorId(timeband.getCreatorId());
					}
					else {
						timeSlot.setBeginningSlotTime(journeyFrequency.getFirstDepartureTime());
						timeSlot.setEndSlotTime(journeyFrequency.getLastDepartureTime());
					}
					timeSlot.setFirstDepartureTimeInSlot(journeyFrequency.getFirstDepartureTime());
					timeSlot.setLastDepartureTimeInSlot(journeyFrequency.getLastDepartureTime());
					
						VehicleJourneyType jaxbObj = vehicleJourneyProducer.produce(vehicleJourney, addExtension, count);
						timeSlot.setObjectId(jaxbObj.getObjectId().replaceAll("VehicleJourney", "TimeSlot"));
						jaxbObj.setTimeSlotId(timeSlot.getObjectId());
						chouetteLineDescription.getVehicleJourney().add(jaxbObj);
					
					TimeSlotType jaxbTSObj = timeSlotProducer.produce(timeSlot, addExtension);
					rootObject.getTimeSlot().add(jaxbTSObj);
					count++;
				}
			} else {
				VehicleJourneyType jaxbObj = vehicleJourneyProducer.produce(vehicleJourney, addExtension);
				chouetteLineDescription.getVehicleJourney().add(jaxbObj);
			}
		}

		for (AccessLink accessLink : collection.getAccessLinks())
		{
			if (collection.getStopAreas().contains(accessLink.getStopArea()) && 
					collection.getAccessPoints().contains(accessLink.getAccessPoint())	)
			{
				rootObject.getAccessLink().add(accessLinkProducer.produce(accessLink,addExtension));
			}
		}

		for (AccessPoint accessPoint : collection.getAccessPoints())
		{
			rootObject.getAccessPoint().add(accessPointProducer.produce(accessPoint,addExtension));
		}

		// sauvegarde
		JaxbNeptuneFileConverter writer = JaxbNeptuneFileConverter.getInstance();
		Path dir = Paths.get(rootDirectory,OUTPUT);
		String fileName = collection.getLine().getObjectId().replaceAll(":", "-")+".xml";
		File file = new File(dir.toFile(),fileName);
		writer.write(AbstractJaxbNeptuneProducer.tridentFactory.createChouettePTNetwork(rootObject), file );

		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

		if (metadata != null)
			metadata.getResources().add(metadata.new Resource(fileName, 
					NeptuneObjectPresenter.getName(collection.getLine().getNetwork()), 
					NeptuneObjectPresenter.getName(collection.getLine())));
	}


}
