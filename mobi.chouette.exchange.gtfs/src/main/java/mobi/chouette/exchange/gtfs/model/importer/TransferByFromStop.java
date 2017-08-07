package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTransfer;

public class TransferByFromStop extends TransferIndex {

	public static final String KEY = FIELDS.from_stop_id.name();

	public TransferByFromStop(String name) throws IOException {
		super(name, KEY, false);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@Override
		protected Index<GtfsTransfer> create(String name) throws IOException {
			return new TransferByFromStop(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TransferByFromStop.class.getName(), factory);
	}
}
