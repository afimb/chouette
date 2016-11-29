package mobi.chouette.exchange.neptune;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.trident.schema.trident.PTNetworkType;

import javassist.expr.Instanceof;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.AbstractChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ChouetteId;
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
public class NeptuneChouetteIdGenerator extends AbstractChouetteIdGenerator implements NeptuneObjectIdTypes{
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

		Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+");
		return p.matcher(oid).matches();
	}
	
	@Override
	public ChouetteId toChouetteId(String objectId, String defaultCodespace) {
		ChouetteId chouetteId = null;
		
		// If object id is conform to neptune format
		if (checkObjectId(objectId)) {
			log.info("Object id : " + objectId + " conforme au format neptune");
			String [] objectIdArray = objectId.split(":");
			String codespace = objectIdArray[0];
			String technicalId = objectIdArray[2];
			
			chouetteId = new ChouetteId(codespace, technicalId, false);
		} else {
			log.info("Object id : " + objectId + " non conforme au format neptune");
		}
		
		return chouetteId;
	}

	@Override
	public String toSpecificFormatId(ChouetteId chouetteId, String defaultCodespace, NeptuneIdentifiedObject object) {
		String objectId = null;
		
		// Produire l'identifiant pour chaque type d'objet possible pour chaque format
		
			objectId += chouetteId.getCodeSpace();
			objectId += ":";
			
			if ( object instanceof AccessPoint)
				objectId += ACCESSPOINT_KEY;
			else if (object instanceof AccessLink)
				objectId += ACCESSLINK_KEY;
			else if (object instanceof Company)
				objectId += COMPANY_KEY;
			else if (object instanceof ConnectionLink)
				objectId += CONNECTIONLINK_KEY;
			else if (object instanceof GroupOfLine)
				objectId += GROUPOFLINE_KEY;
			else if (object instanceof JourneyPattern)
				objectId += JOURNEYPATTERN_KEY;
			else if (object instanceof Line)
				objectId += LINE_KEY;
			else if (object instanceof Network)
				objectId += PTNETWORK_KEY;
			else if (object instanceof RouteSection)
				objectId += ROUTE_SECTION_KEY;
			else if (object instanceof Route)
				objectId += ROUTE_KEY;
			else if (object instanceof StopArea)
				objectId += STOPAREA_KEY;
			else if (object instanceof StopPoint)
				objectId += STOPPOINT_KEY;
			else if (object instanceof TimeSlot)
				objectId += TIMESLOT_KEY;
			else if (object instanceof Timetable)
				objectId += TIMETABLE_KEY;
			else if (object instanceof Timeband)
				objectId += TIMEBAND_KEY;
			else if (object instanceof VehicleJourney)
				objectId += VEHICLEJOURNEY_KEY;
			
			else {
				log.error("Class " + object.getClass().getSimpleName() + " from type not found for neptune export id generation");
				objectId += object.getClass().getSimpleName();
			}
			
			objectId += ":";
			objectId += chouetteId.getTechnicalId();
		
			
		
		return objectId;
	}
	
	
	public static class DefaultFactory extends ChouetteIdGeneratorFactory {

		@Override
		protected ChouetteIdGenerator create() throws IOException {
			ChouetteIdGenerator result = new NeptuneChouetteIdGenerator();
			return result;
		}
	}

	static {
		ChouetteIdGeneratorFactory.factories.put("Neptune", new DefaultFactory());
	}
}
