package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.hub.model.HubMissionOperation;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.model.VehicleJourney;

@Log4j
public class HubMissionOperationProducer extends AbstractProducer {

	public HubMissionOperationProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	private HubMissionOperation hubObject = new HubMissionOperation();

	public boolean save(Context context,VehicleJourney neptuneObject, int rank) {

		hubObject.clear();
		hubObject.setNumeroMission(Integer.valueOf(rank));
		hubObject.setCodeOperation(neptuneObject.getPublishedJourneyIdentifier());
		
		try {
			getExporter().getMissionOperationExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save mission operation",e);
			return false;
		}
		return true;
	}

}
