package fr.certu.chouette.exchange.csv.exporter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Setter;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.csv.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.TimetableProducer;
import fr.certu.chouette.exchange.csv.exporter.report.CSVReport;
import fr.certu.chouette.exchange.csv.exporter.report.CSVReportItem;
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
		param1.setAllowedExtensions(Arrays.asList(new String[]{"csv","zip"}));
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

		if(beans == null || beans.size() ==0) 
		{
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

		if(beans.size() > 1)
		{
			if (fileName.toLowerCase().endsWith(".csv"))
			throw new IllegalArgumentException("cannot export multiple lines in simple csv file");
		}
		if (fileName.toLowerCase().endsWith(".zip"))
		{
			// Create the ZIP file
            try 
            {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileName));
                for (Line line : beans) 
                {
                	if (line.getRoutes().isEmpty()) continue;
                	if (line.getRoutes().size() > 2) 
                	{
                		CSVReportItem item = new CSVReportItem(CSVReportItem.KEY.TOO_MUCH_ROUTES,Report.STATE.WARNING,line.getName());
        				report.addItem(item);
                		logger.error("cannot export "+line.getName()+" too much routes (> 2)");
                		continue;
                	}
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    				try {
						exportLine(new OutputStreamWriter(stream, "UTF-8"), line);

	                    // Add ZIP entry to output stream.
	                    ZipEntry entry = new ZipEntry(line.getName()+"_"+line.getId()+".csv");
	                    out.putNextEntry(entry);
	
	                    out.write(stream.toByteArray());
                		CSVReportItem item = new CSVReportItem(CSVReportItem.KEY.OK_LINE,Report.STATE.OK,line.getName());
        				report.addItem(item);
	
	                    // Complete the entry
	                    out.closeEntry();
					} catch (IOException e) {
						logger.error("export failed ",e);
					}
				}
                out.close();
            } 
            catch (IOException e) 
            {
				logger.error("export failed ",e);
			}

		}
		else
		{
			Line line = beans.get(0);
			try 
			{
				exportLine(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"), line);
			} 
			catch (IOException e) 
			{
				logger.error("export failed ",e);
			}
		}
	}

	private void exportLine(Writer writer, Line line) {
		List<Timetable> timetables = getLineTimetables(line);		
		
		try {
			CSVWriter csvWriter = new CSVWriter(writer, ';',CSVWriter.NO_QUOTE_CHARACTER);
			for(Timetable timetable : timetables)
			{
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
			logger.error("export failed ",e);
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
