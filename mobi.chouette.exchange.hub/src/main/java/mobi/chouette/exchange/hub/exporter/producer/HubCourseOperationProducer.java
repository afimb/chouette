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
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.TransportModeConverter;
import mobi.chouette.exchange.hub.HubTransportModeConverter;
import mobi.chouette.exchange.hub.exporter.HubExportParameters;
import mobi.chouette.exchange.hub.model.HubCourseOperation;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.model.VehicleJourney;

  /**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCourseOperationProducer extends AbstractProducer implements Constant{

	public HubCourseOperationProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubCourseOperation hubObject = new HubCourseOperation();

	public boolean save(Context context,VehicleJourney neptuneObject,  int rank) {
		HubExportParameters parameters = (HubExportParameters) context.get(CONFIGURATION);
		HubTransportModeConverter htmc = HubTransportModeConverter.getInstance();
		TransportModeConverter tmc = (TransportModeConverter) context.get(TRANSPORT_MODE_CONVERTER);

		hubObject.clear();
		hubObject.setNumeroCourse(Integer.valueOf(rank));
		hubObject.setCodeOperation(neptuneObject.getPublishedJourneyIdentifier());

		if (isTrue(neptuneObject.getFlexibleService())) {
			hubObject.setModeTransport("TAD");
		} else if (neptuneObject.getTransportModeContainer() != null) {
			
			if (!parameters.getDefaultFormat().equalsIgnoreCase("Hub")) {
				TransportMode ptM = tmc.specificToGenericMode(neptuneObject.getTransportModeContainer());
				TransportMode tM = htmc.genericToSpecificMode(ptM);
				if (tM != null) {
					if ((tM.getMode().equalsIgnoreCase("CAR") || tM.getMode().equalsIgnoreCase("BUS")) && isTrue(neptuneObject.getMobilityRestrictedSuitability()))
						hubObject.setModeTransport(tM.getMode() + "_PMR");
					else
						hubObject.setModeTransport(tM.getMode());
				}
			} else
				hubObject.setModeTransport(neptuneObject.getTransportMode());

			//			switch (neptuneObject.getTransportMode()) {
			//			case Coach:
			//				if (isTrue(neptuneObject.getMobilityRestrictedSuitability())) {
			//					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.CAR_PMR);
			//				} else {
			//					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.CAR);
			//				}
			//				break;
			//			case Bus:
			//				if (isTrue(neptuneObject.getMobilityRestrictedSuitability())) {
			//					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BUS_PMR);
			//				} else {
			//					hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BUS);
			//				}
			//				break;
			//			case Air:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.AVION);
			//				break;
			//			case Ferry:
			//			case Waterborne:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.BATEAU);
			//				break;
			//			case Metro:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.METRO);
			//				break;
			//			case Taxi:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TAXIBUS);
			//				break;
			//			case LocalTrain:
			//			case LongDistanceTrain:
			//			case LongDistanceTrain_2:
			//			case Train:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TRAIN);
			//				break;
			//			case Tramway:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TRAM);
			//				break;
			//			case Trolleybus:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.TROLLEY);
			//				break;
			//			case Bicycle:
			//				hubObject.setModeTransport(HubCourseOperation.MODE_TRANSPORT.VELO);
			//				break;
			//
			//			default:
			//				break;
			//			}
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
