package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubMission;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubMissionProducer extends AbstractProducer {
	
	public HubMissionProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubMission hubObject = new HubMission();

	public boolean save(VehicleJourney neptuneObject, int pmrRenvoiId, ActionReport report, int rank) {

		hubObject.clear();
		hubObject.setNumero(Integer.valueOf(rank));
		VehicleJourneyAtStop vjas1 = neptuneObject.getVehicleJourneyAtStops().get(0);
		hubObject.setCodeArretDepart(toHubId(vjas1.getStopPoint().getContainedInStopArea()));
		hubObject.setHeureDepart(toHubTime(vjas1.getDepartureTime()));
		VehicleJourneyAtStop vjas2 = neptuneObject.getVehicleJourneyAtStops().get(neptuneObject.getVehicleJourneyAtStops().size() - 1);
		hubObject.setCodeArretArrivee(toHubId(vjas2.getStopPoint().getContainedInStopArea()));
		hubObject.setHeureArrivee(toHubTime(vjas2.getArrivalTime()));
		
		hubObject.setIdentifiantArretDepart(toInt(vjas1.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		hubObject.setIdentifiantArretArrivee(toInt(vjas2.getStopPoint().getContainedInStopArea().getRegistrationNumber()));
		
		hubObject.setTempsPaye(toHubDelay(vjas1.getDepartureTime(), vjas2.getArrivalTime()));
		
		hubObject.setCodeLigne(toHubId(neptuneObject.getRoute().getLine()));
		hubObject.setCodeChemin(toHubId(neptuneObject.getJourneyPattern()));

		int dayTypes = 0;
		for (Timetable timetable : neptuneObject.getTimetables()) {
			dayTypes |= timetable.getIntDayTypes();
			hubObject.getCodesPeriode().add(toHubId(timetable));
		}
		dayTypes >>= 2;
		hubObject.setValidite(Integer.valueOf(dayTypes));


		hubObject.setIdentifiant((neptuneObject.getId()).intValue());

		try {
			getExporter().getMissionExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save course on departure",e);
			return false;
		}
		return true;
	}
}
