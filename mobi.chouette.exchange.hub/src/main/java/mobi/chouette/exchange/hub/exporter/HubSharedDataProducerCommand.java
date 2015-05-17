package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.hub.Constant;
import mobi.chouette.exchange.hub.exporter.producer.HubArretProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubCommuneProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubCorrespondanceProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubGroupeDeLigneProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubModeTransportProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubPeriodeProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubReseauProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubTransporteurProducer;
import mobi.chouette.exchange.hub.model.HubException;
import mobi.chouette.exchange.hub.model.exporter.HubExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class HubSharedDataProducerCommand implements Command, Constant {
	public static final String COMMAND = "HubSharedDataProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);
		try {

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				return ERROR;
			}

			saveData(context);
			DataStats globalStats = report.getStats();
			globalStats.setConnectionLinkCount(collection.getConnectionLinks().size());
			globalStats.setStopAreaCount(collection.getStopAreas().size());
			globalStats.setTimeTableCount(collection.getTimetables().size());
			result = SUCCESS;
		} catch (HubException e) {
			log.error(e);
			report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA,"unable to export data : "+e));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private void saveData(Context context) throws Exception {

		// save commercial stops
		saveCommercialStops(context);

		// save physical stops AFTER commercial ones
		savePhysicalStops(context);

		// save cities AFTER stops
		saveCities(context);

		// save connection links
		saveConnectionLinks(context);
		
		// save networks
		saveNetworks(context);
		
		// save companies
		saveCompanies(context);
		
		// save groupOfLines
		saveGroupOfLines(context);
		
		// save transport modes
		saveTransportModes(context);
		
		// save timetables
		saveTimetables(context);
		

	}

	private void saveCities(Context context) {
		ActionReport report = (ActionReport) context.get(REPORT);
		HubCommuneProducer producer = (HubCommuneProducer) context.get(HUB_COMMUNE_PRODUCER);
		if (producer != null) producer.saveAll(report);
		
	}

	private void saveNetworks(Context context) {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getNetworks().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubReseauProducer producer =  new HubReseauProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);

		List<Network> neptuneObjects = new ArrayList<>(collection.getNetworks());
		Collections.sort(neptuneObjects,new ObjectIdSorter());
		
		for (Network neptuneObject : neptuneObjects) {
			producer.save(neptuneObject, report);
		}
		
	}

	private void saveCompanies(Context context) {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getCompanies().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubTransporteurProducer producer =  new HubTransporteurProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);

		List<Company> neptuneObjects = new ArrayList<>(collection.getCompanies());
		Collections.sort(neptuneObjects,new ObjectIdSorter());
		for (Company neptuneObject : neptuneObjects) {
			producer.save(neptuneObject, report);
		}
		
	}

	private void saveGroupOfLines(Context context) {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getGroupOfLines().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubGroupeDeLigneProducer producer =  new HubGroupeDeLigneProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);

		List<GroupOfLine> neptuneObjects = new ArrayList<>(collection.getGroupOfLines());
		Collections.sort(neptuneObjects,new ObjectIdSorter());
		for (GroupOfLine neptuneObject : neptuneObjects) {
			producer.save(neptuneObject, report);
		}
		
	}

	private void saveTransportModes(Context context) {
		ActionReport report = (ActionReport) context.get(REPORT);
		HubModeTransportProducer producer = (HubModeTransportProducer) context.get(HUB_MODETRANSPORT_PRODUCER);
		if (producer != null) producer.saveAll(report);
		
	}

	private void saveTimetables(Context context) {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getTimetables().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubPeriodeProducer producer =  new HubPeriodeProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);
		Metadata metadata = (Metadata) context.get(METADATA);

		List<Timetable> neptuneObjects = new ArrayList<>(collection.getTimetables());
		Collections.sort(neptuneObjects,new ObjectIdSorter());
		for (Timetable neptuneObject : neptuneObjects) {
			producer.save(neptuneObject, report);
			metadata.getTemporalCoverage().update(neptuneObject.getStartOfPeriod(), neptuneObject.getEndOfPeriod());
		}
		
	}

	private void savePhysicalStops(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getPhysicalStops().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubArretProducer producer = (HubArretProducer) context.get(HUB_ARRET_PRODUCER);
		if (producer == null) {
			producer = new HubArretProducer(exporter);
			context.put(HUB_ARRET_PRODUCER, producer);	
		}
//		HubCommuneProducer communeProducer = (HubCommuneProducer) context.get(HUB_COMMUNE_PRODUCER);
//		if (communeProducer == null) {
//			communeProducer = new HubCommuneProducer(exporter);
//			context.put(HUB_COMMUNE_PRODUCER, communeProducer);	
//		}
		ActionReport report = (ActionReport) context.get(REPORT);

		List<StopArea> stops = new ArrayList<>(collection.getPhysicalStops());
		Collections.sort(stops,new ObjectIdSorter());
		for (StopArea stop : stops) {
			producer.save(stop, report);
//			communeProducer.addCity(stop);
		}

	}

	private void saveCommercialStops(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getCommercialStops().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubArretProducer producer = (HubArretProducer) context.get(HUB_ARRET_PRODUCER);
		if (producer == null) {
			producer = new HubArretProducer(exporter);
			context.put(HUB_ARRET_PRODUCER, producer);
		}
		HubCommuneProducer communeProducer = (HubCommuneProducer) context.get(HUB_COMMUNE_PRODUCER);
		if (communeProducer == null) {
			communeProducer = new HubCommuneProducer(exporter);
			context.put(HUB_COMMUNE_PRODUCER, communeProducer);	
		}
		ActionReport report = (ActionReport) context.get(REPORT);

		List<StopArea> stops = new ArrayList<>(collection.getCommercialStops());
		Collections.sort(stops,new ObjectIdSorter());
		for (StopArea stop : stops) {
			producer.save(stop, report);
			communeProducer.addCity(stop);
		}

	}


	private void saveConnectionLinks(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getConnectionLinks().isEmpty())
			return;
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubCorrespondanceProducer producer =  new HubCorrespondanceProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);

		List<ConnectionLink> links = new ArrayList<>(collection.getConnectionLinks());
		Collections.sort(links,new ObjectIdSorter());
		for (ConnectionLink link : links) {
			producer.save(link, report);
		}

	}

	public class ObjectIdSorter implements Comparator<NeptuneIdentifiedObject> {
		@Override
		public int compare(NeptuneIdentifiedObject arg0, NeptuneIdentifiedObject arg1) {

			return arg0.objectIdSuffix().compareTo(arg1.objectIdSuffix());
		}
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new HubSharedDataProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubSharedDataProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
