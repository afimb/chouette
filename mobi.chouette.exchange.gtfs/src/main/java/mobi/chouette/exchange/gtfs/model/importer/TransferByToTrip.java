package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTransfer;

public class TransferByToTrip extends TransferByFromStop {

	public static final String KEY = FIELDS.to_trip_id.name();

	public TransferByToTrip(String name) throws IOException {
		super(name, KEY, false);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@Override
		protected Index<GtfsTransfer> create(String name) throws IOException {
			return new TransferByToTrip(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TransferByToTrip.class.getName(), factory);
	}
}
