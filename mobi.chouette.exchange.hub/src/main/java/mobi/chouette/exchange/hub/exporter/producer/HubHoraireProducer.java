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
import mobi.chouette.exchange.hub.model.HubHoraire;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.VehicleJourneyAtStop;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubHoraireProducer extends AbstractProducer {

	private int compteur = 1;
	public HubHoraireProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubHoraire hubObject = new HubHoraire();

	public boolean save(VehicleJourneyAtStop neptuneObject, boolean first, boolean last, ActionReport report, int rank) {

		hubObject.clear();
		hubObject.setCodeArret(toHubId(neptuneObject.getStopPoint().getContainedInStopArea()));
		hubObject.setNumeroCourse(Integer.valueOf(rank));
		//hubObject.setNumeroMission(toInt(neptuneObject.getVehicleJourney().getJourneyPattern().getRegistrationNumber()));
		hubObject.setNumeroMission(Integer.valueOf(rank));
		hubObject.setIdentifiantArret(toInt(neptuneObject.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		if (!first)
		{
			// save arrival
			hubObject.setHeure(toHubTime(neptuneObject.getArrivalTime()));
			hubObject.setType(HubHoraire.TYPE_ARRIVEE);
			hubObject.setIdentifiant(compteur++);
			try {
				getExporter().getHoraireExporter().export(hubObject);
			} catch (IOException e) {
				log.error("fail to save horaire",e);
				return false;
			}
		}
		if (!last)
		{
			// save departure
			hubObject.setHeure(toHubTime(neptuneObject.getDepartureTime()));
			hubObject.setType(HubHoraire.TYPE_DEPART);
			hubObject.setIdentifiant(compteur++);

			try {
				getExporter().getHoraireExporter().export(hubObject);
			} catch (IOException e) {
				log.error("fail to save horaire",e);
				return false;
			}
		}
		return true;
	}

}
