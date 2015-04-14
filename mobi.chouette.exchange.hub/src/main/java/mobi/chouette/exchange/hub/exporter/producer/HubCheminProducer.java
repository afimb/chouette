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
import mobi.chouette.exchange.hub.model.HubChemin;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCheminProducer extends AbstractProducer {
	public HubCheminProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubChemin hubObject = new HubChemin();

	public boolean save(JourneyPattern neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCodeLigne(toHubId(neptuneObject.getRoute().getLine()));
		hubObject.setCodeChemin(toHubId(neptuneObject));
		
		hubObject.setIdentifiant(toInt(neptuneObject.getRegistrationNumber()));
		hubObject.setNom(neptuneObject.getName());
		hubObject.setSens(toSens(neptuneObject.getRoute().getWayBack()));
		hubObject.setType(HubChemin.TYPE_COM);

		
        for (StopPoint point : neptuneObject.getStopPoints()) 
        {
        	HubChemin.ArretChemin arret = hubObject.new ArretChemin();
        	hubObject.getArrets().add(arret);
        	arret.setCode(toHubId(point.getContainedInStopArea()));
        	arret.setIdentifiant(toInt(point.getContainedInStopArea().getRegistrationNumber()));
		}
		
		try {
			getExporter().getCheminExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save chemin",e);
			return false;
		}
		return true;
	}

}
