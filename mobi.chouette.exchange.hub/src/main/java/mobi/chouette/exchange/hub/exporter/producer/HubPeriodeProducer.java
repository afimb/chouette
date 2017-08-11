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
import mobi.chouette.exchange.hub.model.HubPeriode;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.model.Timetable;

import org.joda.time.LocalDate;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubPeriodeProducer extends AbstractProducer {
	// private static final int ONE_DAY = 86400000;
	
	private int compteur = 1;
	
	public HubPeriodeProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubPeriode hubObject = new HubPeriode();

	public boolean save(Context context,Timetable neptuneObject) {

		hubObject.clear();
		hubObject.setCode(toHubId(neptuneObject));
		hubObject.setNom(neptuneObject.getComment());
        hubObject.setDateDebut(neptuneObject.getStartOfPeriod());
        hubObject.setDateFin(neptuneObject.getEndOfPeriod());

		LocalDate d = neptuneObject.getStartOfPeriod();
		LocalDate f = neptuneObject.getEndOfPeriod();
        while (d.isBefore(f) || d.equals(f)) {
			hubObject.getCalendrier().add(Boolean.valueOf(neptuneObject.isActiveOn(d)));
			d = d.plusDays(1);
		}
		
		hubObject.setIdentifiant(compteur++);

		try {
			getExporter().getPeriodeExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save periode",e);
			return false;
		}
		return true;
	}

}
