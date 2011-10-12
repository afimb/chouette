package fr.certu.chouette.exchange.csv.exporter.producer;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractCSVNeptuneProducer<T extends NeptuneIdentifiedObject> implements ICSVNeptuneProducer<T>
{
	   public static final int TITLE_COLUMN = 7;

	   public abstract List<String[]> produce(T o);

	protected String[] createCSVLine(String title, String value) {
		String[] csvLine = new String[TITLE_COLUMN+2];
		csvLine[TITLE_COLUMN] = title;
		csvLine[TITLE_COLUMN+1] = value;
		return csvLine;
	}
}
