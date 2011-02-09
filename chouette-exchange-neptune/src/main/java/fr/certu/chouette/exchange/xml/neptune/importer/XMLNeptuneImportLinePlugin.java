/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.ValidationException;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReport;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class XMLNeptuneImportLinePlugin implements IImportPlugin<Line> 
{

	private static final Logger logger = Logger.getLogger(XMLNeptuneImportLinePlugin.class);
	private FormatDescription description;
	
	@Getter @Setter private NeptuneConverter converter;

	public XMLNeptuneImportLinePlugin()
	{
	    description = new FormatDescription() ;
		description.setName("XMLNeptuneLine");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		ParameterDescription param1 = new ParameterDescription("xmlFile",ParameterDescription.TYPE.FILEPATH,false,true);
		param1.setAllowedExtensions(Arrays.asList(new String[]{"xml"}));
		params.add(param1);
		ParameterDescription param2 = new ParameterDescription("validateXML",ParameterDescription.TYPE.BOOLEAN,false,"false");
		params.add(param2);
		description.setParameterDescriptions(params);		
	}

	@Override
	public FormatDescription getDescription() 
	{
		return description;
	}

	@Override
	public List<Line> doImport(List<ParameterValue> parameters,ReportHolder reportContainer)
	throws ChouetteException 
	{
		String filePath = null;
		boolean validate = false;
		for (ParameterValue value : parameters) 
		{
			if (value instanceof SimpleParameterValue)
			{
				SimpleParameterValue svalue = (SimpleParameterValue) value;
				if (svalue.getName().equals("xmlFile"))
				{
					filePath = svalue.getFilepathValue();
				}
				if (svalue.getName().equals("validateXML"))
				{
					validate = svalue.getBooleanValue().booleanValue();
				}
			}
		}
		if (filePath == null) 
		{
			throw new IllegalArgumentException("xmlFile required");
		}

		List<Line> lines = processImport(filePath,validate,reportContainer);

		return lines;
	}

	private List<Line> processImport(String filePath, boolean validate,ReportHolder reportContainer) throws ExchangeException 
	{
		Report report = new NeptuneReport(NeptuneReport.KEY.IMPORT);
		report.setStatus(Report.STATE.OK);
		reportContainer.setReport(report);
		NeptuneFileReader reader = new NeptuneFileReader();
		ChouettePTNetworkTypeType rootObject = null;
		try
		{
		   rootObject = reader.read(filePath);
		}
		catch (Exception e) 
		{
			ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.FILE_ERROR,filePath,e.getLocalizedMessage());
			item.setStatus(Report.STATE.ERROR);
			report.addItem(item);
			report.setStatus(Report.STATE.FATAL);
			return null;
		}
		if (validate)
		{
			try 
			{
				rootObject.validate();
			} 
			catch (ValidationException e) 
			{
				ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.VALIDATION_ERROR,filePath);
				item.setStatus(Report.STATE.ERROR);
				report.addItem(item);
				Throwable t = e;
				while (t != null)
				{
					ReportItem subItem = new NeptuneReportItem(NeptuneReportItem.KEY.VALIDATION_CAUSE,t.getLocalizedMessage());
					subItem.setStatus(Report.STATE.ERROR);
					item.addItem(subItem);
					t = t.getCause();
				}
				report.setStatus(Report.STATE.FATAL);
				return null;
			}
		}
		
		ModelAssembler modelAssembler = new ModelAssembler();

		List<Line> lines = converter.extractLines(rootObject);
		
		modelAssembler.setLines(lines);
		modelAssembler.setRoutes(converter.extractRoutes(rootObject));
		modelAssembler.setCompanies(converter.extractCompanies(rootObject));
		modelAssembler.setPtNetwork(converter.extractPTNetwork(rootObject));
		modelAssembler.setJourneyPatterns(converter.extractJourneyPatterns(rootObject));
		modelAssembler.setPtLinks(converter.extractPTLinks(rootObject));
		modelAssembler.setVehicleJourneys(converter.extractVehicleJourneys(rootObject));
		modelAssembler.setStopPoints(converter.extractStopPoints(rootObject));
		modelAssembler.setStopAreas(converter.extractStopAreas(rootObject));
		modelAssembler.setAreaCentroids(converter.extractAreaCentroids(rootObject));
		modelAssembler.setConnectionLinks(converter.extractConnectionLinks(rootObject));
		
		modelAssembler.connect();
		
		ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.OK_LINE,filePath,Integer.toString(lines.size()));
		item.setStatus(Report.STATE.OK);
		report.addItem(item);
		
		rootObject.toString();
		
		return lines;
	}


}
