package mobi.chouette.exchange.netex;

import java.io.IOException;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.NeptuneIdentifiedObject;

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

		Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+:(LOC|_)+");
		
		Pattern p2 = Pattern.compile("\\w+:\\w+:\\w+:([0-9A-Za-z]|_|-)+:\\w+");
		
		return (p.matcher(oid).matches() || p2.matcher(oid).matches());
	}
	
	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace,
			Class<? extends NeptuneIdentifiedObject> clazz) {
		ChouetteId chouetteId = null;
		
		// If object id is conform to netex format
		if (checkObjectId(objectId)) {
			String [] objectIdArray = objectId.split(":");
			String codespace = null;
			String technicalId = null;
			boolean shared = false;
			
			chouetteId = new ChouetteId();
			
			if (objectIdArray.length == 4) {
				codespace = objectIdArray[0];
				technicalId = objectIdArray[2];
				shared = !(objectIdArray[3].equalsIgnoreCase("LOC"));
	
			} else if (objectIdArray.length == 5) { // Object is a stop point
				codespace = objectIdArray[0] + ":" + objectIdArray[1];
				technicalId = objectIdArray[3];
				shared = !(objectIdArray[4].equalsIgnoreCase("LOC"));
			}
			chouetteId.setCodeSpace(codespace);
			chouetteId.setTechnicalId(technicalId);
			chouetteId.setShared(shared);
		}
		
		return chouetteId;
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
		ChouetteIdGeneratorFactory.factories.put("Netex", new DefaultFactory());
	}
}
