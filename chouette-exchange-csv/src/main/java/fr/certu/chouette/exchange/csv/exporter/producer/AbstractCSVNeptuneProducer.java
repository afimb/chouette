package fr.certu.chouette.exchange.csv.exporter.producer;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractCSVNeptuneProducer<T extends NeptuneIdentifiedObject> implements ICSVNeptuneProducer<T>
{
	public static final int TITLE_COLUMN = 7;

	protected String[] createCSVLine(String title, String value) 
	{
		String[] csvLine = new String[TITLE_COLUMN+2];
		csvLine[TITLE_COLUMN] = title;
		csvLine[TITLE_COLUMN+1] = value;
		return csvLine;
	}

	protected String asString(Object object)
	{
		if(object != null)
		{
			return object.toString();
		}
		return null;
	}
}
