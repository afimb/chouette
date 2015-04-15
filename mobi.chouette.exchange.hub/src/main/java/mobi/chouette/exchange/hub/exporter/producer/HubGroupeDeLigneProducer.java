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
import mobi.chouette.exchange.hub.model.HubGroupeDeLigne;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.GroupOfLine;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubGroupeDeLigneProducer extends AbstractProducer {
	public HubGroupeDeLigneProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubGroupeDeLigne hubObject = new HubGroupeDeLigne();

	public boolean save(GroupOfLine neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCode(toHubId(neptuneObject));

		String name = neptuneObject.getName();
		if (name.trim().isEmpty()) {
			log.error("no name for " + neptuneObject.getObjectId());
			// HubReportItem item = new HubReportItem(
			// HubReportItem.KEY.MISSING_DATA, STATE.ERROR, "Nom",
			// neptuneObject.getObjectId(), "Name");
			// report.addItem(item);
			return false;
		}

		hubObject.setNom(name);

		hubObject.setIdentifiant(toInt(neptuneObject.getRegistrationNumber()));

		try {
			getExporter().getGroupeDeLigneExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save groupe de ligne",e);
			return false;
		}
		return true;
	}

}
