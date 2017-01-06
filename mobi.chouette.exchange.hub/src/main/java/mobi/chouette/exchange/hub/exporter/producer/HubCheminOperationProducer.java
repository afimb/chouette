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
import mobi.chouette.common.Context;
import mobi.chouette.exchange.hub.model.HubCheminOperation;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.model.JourneyPattern;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCheminOperationProducer extends AbstractProducer {
	public HubCheminOperationProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubCheminOperation hubObject = new HubCheminOperation();

	public boolean save(Context context, JourneyPattern neptuneObject) {

		if (!isEmpty(neptuneObject.getRegistrationNumber())) {
			hubObject.clear();
			hubObject.setCodeChemin(toHubId(neptuneObject));
			hubObject.setType(HubCheminOperation.TYPE_COM);
			// hubObject.setUn(Integer.valueOf(1));
			hubObject.setCodeGirouette(neptuneObject.getRegistrationNumber());

			try {
				getExporter().getCheminOperationExporter().export(hubObject);
			} catch (IOException e) {
				log.error("fail to save chemin_operation", e);
				return false;
			}
		}
		return true;
	}

}
