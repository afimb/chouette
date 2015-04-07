/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import mobi.chouette.exchange.hub.model.HubCommune;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.StopArea;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class HubCommuneProducer extends AbstractProducer {
	public HubCommuneProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubCommune hubObject = new HubCommune();

	public boolean save(StopArea neptuneObject, ActionReport report) {

		hubObject.clear();
		
		hubObject.setNom(neptuneObject.getCityName());
		if (neptuneObject.getCountryCode() != null) {
			hubObject.setCodeInsee(Integer.decode(neptuneObject.getCountryCode()));
		}
		hubObject.setNom(neptuneObject.getCityName());

		try {
			getExporter().getCommuneExporter().export(hubObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
