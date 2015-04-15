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
import mobi.chouette.exchange.hub.model.HubCorrespondance;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.ConnectionLink;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCorrespondanceProducer extends AbstractProducer {
	
	private int compteur = 1;
	public HubCorrespondanceProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubCorrespondance hubObject = new HubCorrespondance();

	public boolean save(ConnectionLink neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCodeArret1(toHubId(neptuneObject.getStartOfLink()));
		hubObject.setIdentifiantArret1(toInt(neptuneObject.getStartOfLink().getRegistrationNumber()));
		hubObject.setCodeArret2(toHubId(neptuneObject.getEndOfLink()));
		hubObject.setIdentifiantArret2(toInt(neptuneObject.getEndOfLink().getRegistrationNumber()));
		
		if (neptuneObject.getLinkDistance() != null)
		   hubObject.setDistance(neptuneObject.getLinkDistance().intValue());
		
		hubObject.setTempsParcours(toHubTime(neptuneObject.getDefaultDuration()));
		
		hubObject.setIdentifiant(Integer.valueOf(compteur++));

		try {
			getExporter().getCorrespondanceExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save correspondance",e);
			return false;
		}
		return true;
	}

}
