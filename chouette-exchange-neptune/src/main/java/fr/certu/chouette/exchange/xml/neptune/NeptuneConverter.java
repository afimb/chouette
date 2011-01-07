package fr.certu.chouette.exchange.xml.neptune;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.model.neptune.Line;

/**
 * note : repartir du fr.certu.chouette.service.validation.util.MainSchemaProducer 
 * 
 * @author michel
 *
 */
public class NeptuneConverter 
{

	public List<Line> extractLines(ChouettePTNetworkTypeType rootObject) 
	{
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.Line xmlLine = lineDescription.getLine();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		LineProducer producer = new LineProducer();
		Line line = producer.produceLine(xmlLine);
		
		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		return lines;
	}

	
}
