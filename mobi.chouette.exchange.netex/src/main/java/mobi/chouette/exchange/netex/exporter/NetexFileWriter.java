package mobi.chouette.exchange.netex.exporter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.VehicleJourney;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

@Log4j
public class NetexFileWriter implements Constant
{
	private static VelocityEngine velocityEngine;
	// Prepare the model for velocity
	private Map<String, Object> model = new HashMap<String, Object>();

	public NetexFileWriter()
	{
		if (velocityEngine == null)
		{
			velocityEngine = new VelocityEngine();
			velocityEngine.addProperty("resource.loader", "classpath");
			velocityEngine.addProperty("classpath.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		}
	}

	private void prepareModel(ExportableData collection) throws DatatypeConfigurationException
	{		
		model.put("date", new DateTool());
		model.put("esc", new EscapeTool());
		model.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss'Z'");
		model.put("shortDateFormat", "yyyy-MM-dd");
		model.put("dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss");
		model.put("durationFactory", DatatypeFactory.newInstance());

		model.put("modelTranslator", new ModelTranslator());

		Line line = collection.getLine();
		model.put("line", collection.getLine());
		model.put("network", collection.getNetwork());
		if (collection.getNetwork().getVersionDate() == null)
		{
			collection.getNetwork().setVersionDate(Calendar.getInstance().getTime());
		}
		model.put("company", collection.getLine().getCompany());
		model.put("connectionLinks", collection.getConnectionLinks());
		model.put("accessLinks", collection.getAccessLinks());

		// For ServiceFrame need to have for each tariff stop points associated
		model.put("tariffs", tariffs(collection));

		// For TimetableFrame need to have for trainNumbers
		model.put("vehicleNumbers", vehicleNumbers(collection));

		// Be careful line return attributes address
		List<StopArea> stopAreaWithoutQuays = new ArrayList<StopArea>();
		stopAreaWithoutQuays.addAll(collection.getStopPlaces());
		stopAreaWithoutQuays.addAll(collection.getCommercialStopPoints());
		model.put("stopPlaces", stopAreaWithoutQuays);

		// For ITL
		model.put("routingConstraints", line.getRoutingConstraints());

		// For TimetableFrame need to have vehicle journeys
		model.put("vehicleJourneys", collection.getVehicleJourneys());

		// For ServiceCalendarFrame need to have time tables
		model.put("timetables", collection.getTimetables());
	}

	public File writeXmlFile(ExportableData collection, File file) throws IOException,
	DatatypeConfigurationException
	{
		// Prepare the model for velocity
		prepareModel(collection);

		StringWriter output = new StringWriter();
		VelocityContext velocityContext = new VelocityContext(model);
		velocityContext.put("esc", new EscapeTool());

		velocityEngine.mergeTemplate("templates/line.vm", "UTF-8",
				velocityContext, output);

		FileUtils.write(file, output.toString(), "UTF-8");

		log.debug("File : " + file.getName() + "created");

		return file;
	}

	private List<Long> vehicleNumbers(ExportableData collection)
	{
		List<Long> result = new ArrayList<Long>();

		List<VehicleJourney> vehicles = collection.getVehicleJourneys();
		for (VehicleJourney vehicle : vehicles)
		{
			if (!result.contains(vehicle.getNumber()))
			{
				result.add(vehicle.getNumber());
			}
		}
		return result;
	}

	private List<Integer> tariffs(ExportableData collection)
	{
		List<Integer> tariffs = new ArrayList<Integer>();

		for (StopArea stopArea : collection.getStopAreas())
		{
			if (stopArea.getFareCode() != null
					&& !tariffs.contains(stopArea.getFareCode()))
				tariffs.add(stopArea.getFareCode());
		}
		return tariffs;
	}

}
