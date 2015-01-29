package mobi.chouette.exchange.importer.updater;

import java.util.Comparator;

import mobi.chouette.model.NeptuneIdentifiedObject;

public class NeptuneIdentifiedObjectComparator implements
		Comparator<NeptuneIdentifiedObject> {
	public static final Comparator<NeptuneIdentifiedObject> INSTANCE = new NeptuneIdentifiedObjectComparator();

	@Override
	public int compare(NeptuneIdentifiedObject left,
			NeptuneIdentifiedObject right) {
		return left.getObjectId().compareTo(right.getObjectId());
	}
}
