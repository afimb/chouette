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
import mobi.chouette.exchange.hub.model.HubCourseOperation;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.VehicleJourney;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCourseOperationProducer extends AbstractProducer {

	public HubCourseOperationProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubCourseOperation hubObject = new HubCourseOperation();

	public boolean save(VehicleJourney neptuneObject, ActionReport report, int rank) {

		hubObject.clear();
		hubObject.setNumeroCourse(Integer.valueOf(rank));
		hubObject.setCodeOperation(neptuneObject.getPublishedJourneyIdentifier());

		if (isTrue(neptuneObject.getFlexibleService())) {
			hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TAD);
		} else if (neptuneObject.getTransportMode() != null) {
			switch (neptuneObject.getTransportMode()) {
			case Coach:
				if (isTrue(neptuneObject.getMobilityRestrictedSuitability())) {
					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.CAR_PMR);
				} else {
					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.CAR);
				}
				break;
			case Bus:
				if (isTrue(neptuneObject.getMobilityRestrictedSuitability())) {
					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BUS_PMR);
				} else {
					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BUS);
				}
				break;
			case Air:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.AVION);
				break;
			case Ferry:
			case Waterborne:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BATEAU);
				break;
			case Metro:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.METRO);
				break;
			case Taxi:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TAXIBUS);
				break;
			case LocalTrain:
			case LongDistanceTrain:
			case LongDistanceTrain_2:
			case Train:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TRAIN);
				break;
			case Tramway:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TRAM);
				break;
			case Trolleybus:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TROLLEY);
				break;
			case Bicycle:
				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.VELO);
				break;

			default:
				break;
			}
		}

		try {
			getExporter().getCourseOperationExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save course operation",e);
			return false;
		}
		return true;
	}

}
