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
import mobi.chouette.exchange.hub.model.HubCourse;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCourseProducer extends AbstractProducer {

	public HubCourseProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubCourse hubObject = new HubCourse();

	public boolean save(VehicleJourney neptuneObject, int pmrRenvoiId, ActionReport report, int rank) {

		hubObject.clear();
		hubObject.setNumero(Integer.valueOf(rank));
		VehicleJourneyAtStop vjas1 = neptuneObject.getVehicleJourneyAtStops().get(0);
		hubObject.setCodeArret(toHubId(vjas1.getStopPoint().getContainedInStopArea()));
		hubObject.setHeure(toHubTime(vjas1.getDepartureTime()));
		hubObject.setIdentifiantArret(toInt(vjas1.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		hubObject.setCodeLigne(toHubId(neptuneObject.getRoute().getLine()));
		hubObject.setCodeChemin(toHubId(neptuneObject.getJourneyPattern()));
		hubObject.setType(HubCourse.TYPE_DEPART);
		hubObject.setSens(toSens(neptuneObject.getRoute().getWayBack()));

		int dayTypes = 0;
		for (Timetable timetable : neptuneObject.getTimetables()) {
			dayTypes |= timetable.getIntDayTypes();
			hubObject.getCodesPeriode().add(toHubId(timetable));
		}
		dayTypes >>= 2;
		hubObject.setValidite(Integer.valueOf(dayTypes));

		// renvois
		if (pmrRenvoiId > 0) {
			if (isTrue(neptuneObject.getMobilityRestrictedSuitability())
					|| (isTrue(neptuneObject.getRoute().getLine().getMobilityRestrictedSuitable()) && !isFalse(neptuneObject
							.getMobilityRestrictedSuitability()))) {
				hubObject.getIdentifiantsRenvoi().add(pmrRenvoiId);
			}
		}
		for (Footnote footnote : neptuneObject.getFootnotes()) {
			hubObject.getIdentifiantsRenvoi().add(Integer.decode(footnote.getKey()));
		}

		//hubObject.setIdentifiant(toInt(toHubId(neptuneObject)));
		hubObject.setIdentifiant((vjas1.getId()).intValue());

		try {
			getExporter().getCourseExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save course on departure",e);
			return false;
		}
		// deuxieme enregistrement pour l'arrivée
		VehicleJourneyAtStop vjas2 = neptuneObject.getVehicleJourneyAtStops().get(
				neptuneObject.getVehicleJourneyAtStops().size() - 1);
		hubObject.setCodeArret(toHubId(vjas2.getStopPoint().getContainedInStopArea()));
		hubObject.setHeure(toHubTime(vjas2.getArrivalTime()));
		hubObject.setType(HubCourse.TYPE_ARRIVEE);
		hubObject.setIdentifiantArret(toInt(vjas2.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		hubObject.setIdentifiant((vjas2.getId()).intValue());
		try {
			getExporter().getCourseExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save course on arrival",e);
			return false;
		}
		return true;
	}

}
