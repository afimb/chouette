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

import org.apache.log4j.Logger;
import org.exolab.castor.xml.ValidationException;

import chouette.schema.ChouettePTNetworkTypeType;

import fr.certu.chouette.common.report.ReportHolder;
import fr.certu.chouette.exchange.ExchangeException;
import fr.certu.chouette.exchange.FormatDescription;
import fr.certu.chouette.exchange.IImportPlugin;
import fr.certu.chouette.exchange.ParameterDescription;
import fr.certu.chouette.exchange.ParameterValue;
import fr.certu.chouette.exchange.SimpleParameterValue;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;

public class XMLNeptuneImportLinePlugin implements IImportPlugin<Line> 
{

	private static final Logger logger = Logger.getLogger(XMLNeptuneImportLinePlugin.class);
	private FormatDescription description;

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
	throws ExchangeException 
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
			throw new ExchangeException("missing xmlFile arg");
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

				fr.certu.chouette.service.validation.commun.ValidationException ex = new fr.certu.chouette.service.validation.commun.ValidationException();
				ex.add(TypeInvalidite.INVALID_XML_FILE, filePath);
				throw ex;
			}
		}
		NeptuneConverter converter = new NeptuneConverter();
		List<Line> line = converter.extractLines(rootObject);

		return line;
	}


}
