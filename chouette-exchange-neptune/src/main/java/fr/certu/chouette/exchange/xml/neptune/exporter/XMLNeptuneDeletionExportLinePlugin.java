package fr.certu.chouette.exchange.xml.neptune.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Setter;
import chouette.schema.ChouetteRemoveLine;
import chouette.schema.ChouetteRemoveLineTypeType;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.LineProducer;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

public class XMLNeptuneDeletionExportLinePlugin implements IExportPlugin<Line> {


	private FormatDescription description;
	@Setter private LineProducer lineProducer;

	
	public XMLNeptuneDeletionExportLinePlugin() {
		description = new FormatDescription(this.getClass().getName());
		description.setName("NEPTUNE");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		ParameterDescription param1 = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILENAME, false, true);
		param1.setAllowedExtensions(Arrays.asList(new String[]{"xml","zip"}));
		params.add(param1);
		description.setParameterDescriptions(params);
	}
	
	@Override
	public FormatDescription getDescription() {
		return description;
	}

	@Override
	public void doExport(List<Line> beans, List<ParameterValue> parameters,
			ReportHolder report) throws ChouetteException {
		String fileName = null;
		
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
					fileName = svalue.getFilenameValue();
				}

			}
		}
		if (fileName == null) 
		{
			throw new IllegalArgumentException("outputFile required");
		}
		
		String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
		
		if(beans.size() > 1 && fileExtension.equals("xml")){
			throw new IllegalArgumentException("cannot export multiple lines in one XML file");
		}
		
		File outputFile = new File(fileName);
		
		if(fileExtension.equals("xml")){
			ChouetteRemoveLineTypeType rootObject = exportLine(beans.get(0));
			NeptuneFileWriter neptuneFileWriter = new NeptuneFileWriter();
			neptuneFileWriter.write(rootObject, outputFile);
		}
		else
		{
			//TODO : implement solution for multiple beans
		}
	}

	private ChouetteRemoveLineTypeType exportLine(Line line) {
		ChouetteRemoveLine rootObject = new ChouetteRemoveLine();
		
		if(line != null)
		{
			chouette.schema.Line castorLine = lineProducer.produce(line);
			rootObject.setLine(castorLine);
		}
		
		return rootObject;
	}

}
