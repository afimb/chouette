/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

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
public class HubCourseProducer extends AbstractProducer {
	public HubCourseProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubCourse hubObject = new HubCourse();

	public boolean save(VehicleJourney neptuneObject, ActionReport report, int rank) {

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
		if (isTrue(neptuneObject.getFlexibleService()) || (neptuneObject.getFlexibleService() == null && isTrue(neptuneObject.getRoute().getLine().getFlexibleService())))
		{
            hubObject.getIdentifiantsRenvoi().add(Integer.valueOf(1));
		}
		for (Footnote footnote : neptuneObject.getRoute().getLine().getFootnotes()) 
		{
			// TODO hubObject.getIdentifiantsRenvoi().add(footnote.get) ???
		}

		
		hubObject.setIdentifiant(toInt(toHubId(neptuneObject)));
		
		try {
			getExporter().getCourseExporter().export(hubObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// deuxieme enregistrement pour l'arrivée
		VehicleJourneyAtStop vjas2 = neptuneObject.getVehicleJourneyAtStops().get(neptuneObject.getVehicleJourneyAtStops().size()-1);
		hubObject.setCodeArret(toHubId(vjas2.getStopPoint().getContainedInStopArea()));
		hubObject.setHeure(toHubTime(vjas2.getArrivalTime()));
		hubObject.setIdentifiantArret(toInt(vjas2.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		try {
			getExporter().getCourseExporter().export(hubObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
