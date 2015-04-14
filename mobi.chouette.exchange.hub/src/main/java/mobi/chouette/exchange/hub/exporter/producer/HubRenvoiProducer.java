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
import mobi.chouette.exchange.hub.model.HubRenvoi;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Footnote;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubRenvoiProducer extends AbstractProducer {

	private int compteur = 1;

	public HubRenvoiProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubRenvoi hubObject = new HubRenvoi();

	public boolean save(Footnote neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCode(neptuneObject.getCode());
		hubObject.setNom(neptuneObject.getLabel());
	    hubObject.setIdentifiant(compteur++);
	    neptuneObject.setKey(hubObject.getIdentifiant().toString());
		
		try {
			getExporter().getRenvoiExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save renvoi",e);
			return false;
		}
		return true;
	}

}
