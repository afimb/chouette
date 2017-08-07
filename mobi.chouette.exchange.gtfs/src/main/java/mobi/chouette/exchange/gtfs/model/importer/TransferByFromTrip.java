package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTransfer;

public class TransferByFromTrip extends TransferIndex {

	public static final String KEY = FIELDS.from_trip_id.name();

	public TransferByFromTrip(String name) throws IOException {
		super(name, KEY, false,true);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@Override
		protected Index<GtfsTransfer> create(String name) throws IOException {
			return new TransferByFromTrip(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TransferByFromTrip.class.getName(), factory);
	}
}
