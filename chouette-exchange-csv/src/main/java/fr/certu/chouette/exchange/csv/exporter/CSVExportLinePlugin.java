package fr.certu.chouette.exchange.csv.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import lombok.Setter;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.csv.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.TimetableProducer;
import fr.certu.chouette.exchange.csv.exporter.report.CSVReport;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

public class CSVExportLinePlugin implements IExportPlugin<Line> {

	private static final Logger logger = Logger.getLogger(CSVExportLinePlugin.class);
	@Setter TimetableProducer timetableProducer;
	@Setter PTNetworkProducer ptNetworkProducer;
	@Setter CompanyProducer companyProducer;
	@Setter LineProducer lineProducer;
	
	private FormatDescription description;


	public CSVExportLinePlugin() {
		description = new FormatDescription(this.getClass().getName());
		description.setName("CSV");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		ParameterDescription param1 = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false, true);
		param1.setAllowedExtensions(Arrays.asList(new String[]{"csv"}));
		params.add(param1);
		description.setParameterDescriptions(params);
	}

	@Override
	public FormatDescription getDescription() {
		return description;
	}

	@Override
	public void doExport(List<Line> beans, List<ParameterValue> parameters,
			ReportHolder reportContainer) throws ChouetteException {
		String fileName = null;
      CSVReport report = new CSVReport(CSVReport.KEY.EXPORT);
      report.setStatus(Report.STATE.OK);
      reportContainer.setReport(report);

		if(beans == null){
			throw new IllegalArgumentException("no beans to export");
		}

		for (ParameterValue value : parameters) 
		{
			if (value instanceof SimpleParameterValue)
			{
				SimpleParameterValue svalue = (SimpleParameterValue) value;
				if (svalue.getName().equals("outputFile"))
				{
					fileName = svalue.getFilepathValue();
				}

			}
		}
		if (fileName == null) 
		{
			throw new IllegalArgumentException("outputFile required");
		}

		if(beans.size() > 1){
			throw new IllegalArgumentException("cannot export multiple lines");
		}
		
		Line line = beans.get(0);
		
		List<Timetable> timetables = getLineTimetables(line);		
		
		try {
			CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"), ';',CSVWriter.NO_QUOTE_CHARACTER);
			for(Timetable timetable : timetables){
				csvWriter.writeAll(timetableProducer.produce(timetable));
				csvWriter.writeNext(new String[0]);
			}
			
			csvWriter.writeAll(ptNetworkProducer.produce(line.getPtNetwork()));
			csvWriter.writeNext(new String[0]);
			
			csvWriter.writeAll(companyProducer.produce(line.getCompany()));
			csvWriter.writeNext(new String[0]);
			
			csvWriter.writeAll(lineProducer.produce(line));
			csvWriter.writeNext(new String[0]);
			
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Timetable> getLineTimetables(Line line) {
		List<Route> routes = line.getRoutes();
		
		List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
		for(Route route : routes){
			journeyPatterns.addAll(route.getJourneyPatterns());
		}
		
		List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
		for(JourneyPattern journeyPattern : journeyPatterns){
			vehicleJourneys.addAll(journeyPattern.getVehicleJourneys());
		}
		
		HashSet<Timetable> timetablesHashSet = new HashSet<Timetable>();
		for(VehicleJourney vehicleJourney : vehicleJourneys){
			timetablesHashSet.addAll(vehicleJourney.getTimetables());
		}
		return Arrays.asList(timetablesHashSet.toArray(new Timetable[timetablesHashSet.size()]));
	}
}
