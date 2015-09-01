/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubSchemaProducer extends AbstractProducer {
	
	private int compteur = 1;
	
	public HubSchemaProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubSchema hubObject = new HubSchema();

	public boolean save(Route neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCodeLigne(toHubId(neptuneObject.getLine()));
		
		
		hubObject.setSens(toSens(neptuneObject.getWayBack()));
		hubObject.setIdentifiant(Integer.valueOf(compteur++)); 
		
        for (StopPoint point : neptuneObject.getStopPoints()) 
        {
        	if (point == null) continue;
        	HubSchema.ArretSchema arret = hubObject.new ArretSchema();
        	hubObject.getArrets().add(arret);
        	arret.setCode(toHubId(point.getContainedInStopArea()));
        	arret.setIdentifiant(toInt(point.getContainedInStopArea().getRegistrationNumber()));
		}
		
		try {
			getExporter().getSchemaExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save schema",e);
			return false;
		}
		return true;
	}

}
