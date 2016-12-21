package mobi.chouette.exchange.netex;

import java.io.IOException;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Log4j
public class NetexChouetteIdGenerator extends AbstractChouetteIdGenerator implements NetexObjectIdTypes{
	/**
	 * check if an objectId is conform to Trident
	 * 
	 * @param oid
	 *            objectId to check
	 * @return true if valid, false othewise
	 */
	public boolean checkObjectId(String oid) {
		if (oid == null)
			return false;

//		Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+:(LOC|_)+");
//		Pattern p2 = Pattern.compile("\\w+:\\w+:\\w+:([0-9A-Za-z]|_|-)+:\\w+");
//		return (p.matcher(oid).matches() || p2.matcher(oid).matches());
		Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+");
		return p.matcher(oid).matches();
		
	}
	
	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace,
			Class<? extends NeptuneIdentifiedObject> clazz) {
		ChouetteId chouetteId = null;
		
		if (checkObjectId(objectId)) {
			// log.info("Object id : " + objectId + " conforme au format neptune");
			String[] objectIdArray = objectId.split(":");
			String codespace = objectIdArray[0];
			String technicalId = objectIdArray[2];
			String objectType = getObjectType(clazz);
			// si le type d'objet ,n'est pas standard, on l'injecte dans le TechnicalId
			if (!objectIdArray[1].equals(objectType)) {
				technicalId = objectIdArray[1] + ":" + objectIdArray[2];
			}
			chouetteId = new ChouetteId(codespace, technicalId, false);
		} else {
			log.info("Object id : " + objectId + " non conforme au format neptune");
		}
		
//		// If object id is conform to netex format
//		if (checkObjectId(objectId)) {
//			String [] objectIdArray = objectId.split(":");
//			String codespace = null;
//			String technicalId = null;
//			boolean shared = false;
//			
//			chouetteId = new ChouetteId();
//			
//			if (objectIdArray.length == 4) {
//				codespace = objectIdArray[0];
//				technicalId = objectIdArray[2];
//				shared = !(objectIdArray[3].equalsIgnoreCase("LOC"));
//	
//			} else if (objectIdArray.length == 5) { // Object is a stop point
//				codespace = objectIdArray[0] + ":" + objectIdArray[1];
//				technicalId = objectIdArray[3];
//				shared = !(objectIdArray[4].equalsIgnoreCase("LOC"));
//			}
//			chouetteId.setCodeSpace(codespace);
//			chouetteId.setTechnicalId(technicalId);
//			chouetteId.setShared(shared);
//		}
		
		return chouetteId;
	}

	private String getObjectType(Class<? extends NeptuneIdentifiedObject> clazz) {
		try {
			NeptuneIdentifiedObject object = clazz.newInstance();
			if (object instanceof AccessPoint)
				return ACCESSPOINT_KEY;
			if (object instanceof AccessLink)
				return ACCESSLINK_KEY;
			if (object instanceof Company)
				return COMPANY_KEY;
			if (object instanceof ConnectionLink)
				return CONNECTIONLINK_KEY;
			if (object instanceof GroupOfLine)
				return GROUPOFLINE_KEY;
			if (object instanceof JourneyPattern)
				return JOURNEYPATTERN_KEY;
			if (object instanceof Line)
				return LINE_KEY;
			if (object instanceof Network)
				return PTNETWORK_KEY;
			if (object instanceof RouteSection)
				return ROUTE_SECTION_KEY;
			if (object instanceof Route)
				return ROUTE_KEY;
			if (object instanceof StopArea)
				return STOPAREA_KEY;
			if (object instanceof StopPoint)
				return STOPPOINT_KEY;
			if (object instanceof Timetable)
				return TIMETABLE_KEY;
			if (object instanceof Timeband)
				return TIMEBAND_KEY;
			if (object instanceof VehicleJourney)
				return VEHICLEJOURNEY_KEY;

		} catch (InstantiationException | IllegalAccessException e) {

		}
		return clazz.getSimpleName();

	}

	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		String objectId = null;
		// Produire l'identifiant pour chaque type d'objet possible pour chaque format
				try {
					objectId += chouetteId.getCodeSpace();
					objectId += ":";
					
					if (Class.forName(ACCESSPOINT_KEY).isInstance(object))
						objectId += ACCESSPOINT_KEY;
					else if (Class.forName(ACCESSLINK_KEY).isInstance(object))
						objectId += ACCESSLINK_KEY;
					else if (Class.forName(AREACENTROID_KEY).isInstance(object))
						objectId += AREACENTROID_KEY;
					else if (Class.forName(COMPANY_KEY).isInstance(object))
						objectId += COMPANY_KEY;
					else if (Class.forName(CONNECTIONLINK_KEY).isInstance(object))
						objectId += CONNECTIONLINK_KEY;
					else if (Class.forName(FACILITY_KEY).isInstance(object))
						objectId += FACILITY_KEY;
					else if (Class.forName(GROUPOFLINE_KEY).isInstance(object))
						objectId += GROUPOFLINE_KEY;
					else if (Class.forName(JOURNEYPATTERN_KEY).isInstance(object))
						objectId += JOURNEYPATTERN_KEY;
					else if (Class.forName(LINE_KEY).isInstance(object))
						objectId += LINE_KEY;
					else if (Class.forName(PTLINK_KEY).isInstance(object))
						objectId += PTLINK_KEY;
					else if (Class.forName(PTNETWORK_KEY).isInstance(object))
						objectId += PTNETWORK_KEY;
					else if (Class.forName(ROUTE_SECTION_KEY).isInstance(object))
						objectId += ROUTE_SECTION_KEY;
					else if (Class.forName(ROUTE_KEY).isInstance(object))
						objectId += ROUTE_KEY;
					else if (Class.forName(STOPAREA_KEY).isInstance(object))
						objectId += STOPAREA_KEY;
					else if (Class.forName(STOPPOINT_KEY).isInstance(object))
						objectId += STOPPOINT_KEY;
					else if (Class.forName(STOPPOINT_KEY).isInstance(object))
						objectId += STOPPOINT_KEY;
					else if (Class.forName(TIMESLOT_KEY).isInstance(object))
						objectId += TIMESLOT_KEY;
					else if (Class.forName(TIMETABLE_KEY).isInstance(object))
						objectId += TIMETABLE_KEY;
					else if (Class.forName(TIMEBAND_KEY).isInstance(object))
						objectId += TIMEBAND_KEY;
					else if (Class.forName(VEHICLEJOURNEY_KEY).isInstance(object))
						objectId += VEHICLEJOURNEY_KEY;
					
					objectId += ":";
					objectId += chouetteId.getTechnicalId();
				} catch (ClassNotFoundException e) {
					log.error("Class from type not found for netex export id generation");
				}
		return objectId;
	}
	
	public static class DefaultFactory extends ChouetteIdGeneratorFactory {

		@Override
		protected ChouetteIdGenerator create() throws IOException {
			ChouetteIdGenerator result = new NetexChouetteIdGenerator();
			return result;
		}
	}

	static {
		ChouetteIdGeneratorFactory.factories.put("netex", new DefaultFactory());
	}
}
