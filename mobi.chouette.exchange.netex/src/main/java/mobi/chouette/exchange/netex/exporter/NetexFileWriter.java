package mobi.chouette.exchange.netex.exporter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.netex.exporter.writer.DeliveryWriter;

import org.apache.commons.io.output.FileWriterWithEncoding;

@Log4j
public class NetexFileWriter implements Constant {
//	private static VelocityEngine velocityEngine = null;
	// Prepare the model for velocity
//	private Map<String, Object> model = new HashMap<String, Object>();

	public NetexFileWriter() {
//		if (velocityEngine == null) {
//			velocityEngine = new VelocityEngine();
//			velocityEngine.setProperty("resource.loader", "classpath");
//			velocityEngine.setProperty("classpath.resource.loader.class",
//					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//			velocityEngine.setProperty("classpath.resource.loader.cache",true);
//			velocityEngine.init();
//		}
	}

//	private void prepareModel(ExportableData collection) throws DatatypeConfigurationException {
//		Monitor monitor = MonitorFactory.start("NetexFileWriter.prepareModel");
//		try
//		{
//		model.put("date", new DateTool());
//		model.put("esc", new EscapeTool());
//		model.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss'Z'");
//		model.put("shortDateFormat", "yyyy-MM-dd");
//		model.put("dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss");
//		model.put("durationFactory", DatatypeFactory.newInstance());
//
//		model.put("modelTranslator", new ModelTranslator());
//
//		Line line = collection.getLine();
//		model.put("line", collection.getLine());
//		model.put("network", collection.getNetwork());
//		if (collection.getNetwork().getVersionDate() == null) {
//			collection.getNetwork().setVersionDate(Calendar.getInstance().getTime());
//		}
//		model.put("company", collection.getLine().getCompany());
//		model.put("connectionLinks", collection.getConnectionLinks());
//		model.put("accessLinks", collection.getAccessLinks());
//
//		// For ServiceFrame need to have for each tariff stop points associated
//		model.put("tariffs", tariffs(collection));
//
//		// For TimetableFrame need to have for trainNumbers
//		model.put("vehicleNumbers", vehicleNumbers(collection));
//
//		// Be careful line return attributes address
//		List<StopArea> stopAreaWithoutQuays = new ArrayList<StopArea>();
//		stopAreaWithoutQuays.addAll(collection.getStopPlaces());
//		stopAreaWithoutQuays.addAll(collection.getCommercialStopPoints());
//		model.put("stopPlaces", stopAreaWithoutQuays);
//
//		// For ITL
//		model.put("routingConstraints", line.getRoutingConstraints());
//
//		// For TimetableFrame need to have vehicle journeys
//		model.put("vehicleJourneys", collection.getVehicleJourneys());
//
//		// For ServiceCalendarFrame need to have time tables
//		model.put("timetables", collection.getTimetables());
//		} finally {
//			log.info(Color.CYAN + monitor.stop() + Color.NORMAL);
//		}
//	}

	public File writeXmlFile(ExportableData collection, File file) throws IOException, DatatypeConfigurationException {
		// Prepare the model for velocity
//		 prepareModel(collection);
//		Monitor monitor = MonitorFactory.start("NetexFileWriter.writeXmlFile");
//		try{
//		// StringWriter output = new StringWriter();
//		VelocityContext velocityContext = new VelocityContext(model);
//		velocityContext.put("esc", new EscapeTool());
//		
		Writer output = new FileWriterWithEncoding(file, "UTF-8");
		DeliveryWriter.write(output, collection);
		output.close();
//
//		velocityEngine.mergeTemplate("templates/line.vm", "UTF-8", velocityContext, output);
//
//		// FileUtils.write(file, output.toString(), "UTF-8");

		log.info("File : " + file.getName() + "created");

//		} finally {
//			log.info(Color.CYAN + monitor.stop() + Color.NORMAL);
//		}
		return file;
	}

//	private List<Long> vehicleNumbers(ExportableData collection) {
//		List<Long> result = new ArrayList<Long>();
//
//		List<VehicleJourney> vehicles = collection.getVehicleJourneys();
//		for (VehicleJourney vehicle : vehicles) {
//			if (!result.contains(vehicle.getNumber())) {
//				result.add(vehicle.getNumber());
//			}
//		}
//		return result;
//	}
//
//	private List<Integer> tariffs(ExportableData collection) {
//		List<Integer> tariffs = new ArrayList<Integer>();
//
//		for (StopArea stopArea : collection.getStopAreas()) {
//			if (stopArea.getFareCode() != null && !tariffs.contains(stopArea.getFareCode()))
//				tariffs.add(stopArea.getFareCode());
//		}
//		return tariffs;
//	}

}
