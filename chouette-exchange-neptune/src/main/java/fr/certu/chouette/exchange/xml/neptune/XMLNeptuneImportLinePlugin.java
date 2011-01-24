/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune;

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
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

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
		NeptuneFileReader reader = new NeptuneFileReader();
		ChouettePTNetworkTypeType rootObject = reader.read(filePath);
		if (validate)
		{
			try 
			{
				rootObject.validate();
			} 
			catch (ValidationException e) 
			{
				throw new ExchangeException(ExchangeExceptionCode.INVALID_XML_FILE , filePath);
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
		
		modelAssembler.connect();
		
		return lines;
	}


}
