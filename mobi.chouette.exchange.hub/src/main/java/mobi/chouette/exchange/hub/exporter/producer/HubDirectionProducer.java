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
import mobi.chouette.exchange.hub.model.HubDirection;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.JourneyPattern;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubDirectionProducer extends AbstractProducer {
	public HubDirectionProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubDirection hubObject = new HubDirection();

	public boolean save(JourneyPattern neptuneObject, ActionReport report) {

		hubObject.clear();
		
		hubObject.setDirection(neptuneObject.getName());
		hubObject.setCodeLigne(toHubId(neptuneObject.getRoute().getLine()));
		hubObject.setSens(toSens(neptuneObject.getRoute().getWayBack()));
		hubObject.setCodeChemin(toHubId(neptuneObject));

		try {
			getExporter().getDirectionExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save direction",e);
			return false;
		}
		return true;
	}

}
