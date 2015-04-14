/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubModeTransport;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubModeTransportProducer extends AbstractProducer {

	private Map<HubModeTransport.MODE_TRANSPORT, HubModeTransport> modesTransport = new HashMap<>();

	public HubModeTransportProducer(HubExporterInterface exporter) {
		super(exporter);
	}

	public boolean addLine(Line line) {
		HubModeTransport.MODE_TRANSPORT mode = null;
		switch (line.getTransportModeName()) {
		case Air:
			mode = HubModeTransport.MODE_TRANSPORT.AVION;
			break;
		case Bicycle:
			mode = HubModeTransport.MODE_TRANSPORT.VELO;
			break;
		case Bus:
			mode = HubModeTransport.MODE_TRANSPORT.BUS;
			break;
		case Coach:
			mode = HubModeTransport.MODE_TRANSPORT.CAR;
			break;
		case Waterborne:
		case Ferry:
			mode = HubModeTransport.MODE_TRANSPORT.BATEAU;
			break;
		case LocalTrain:
		case Train:
		case LongDistanceTrain:
		case LongDistanceTrain_2:
			mode = HubModeTransport.MODE_TRANSPORT.TRAIN;
			break;
		case Metro:
			mode = HubModeTransport.MODE_TRANSPORT.METRO;
			break;
		case Tramway:
			mode = HubModeTransport.MODE_TRANSPORT.TRAM;
			break;
		case Trolleybus:
			mode = HubModeTransport.MODE_TRANSPORT.TROLLEY;
			break;

		default:
			return false; // not implemented
		}
		HubModeTransport hubObject = modesTransport.get(mode);
		if (hubObject == null) {
			hubObject = new HubModeTransport();
			hubObject.setCode(mode);
			modesTransport.put(mode, hubObject);
		}
		hubObject.getCodesLigne().add(toHubId(line));

		return true;
	}

	public boolean saveAll(ActionReport report) {

		try {
			List<HubModeTransport> listModes = new ArrayList<>(modesTransport.values());
			Collections.sort(listModes,new Sorter());
			
			for (HubModeTransport hubObject : listModes) {
				getExporter().getModeTransportExporter().export(hubObject);
			}
		}
		 catch (IOException e) {
			log.error("fail to save modes transport",e);
			return false;
		}
		
		return true;
	}

	
	public class Sorter implements Comparator<HubModeTransport> {
		@Override
		public int compare(HubModeTransport arg0, HubModeTransport arg1) {

			return arg0.getCode().name().compareTo(arg1.getCode().name());
		}
	}

}
