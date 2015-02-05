package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTrip;

public class TripById extends TripIndex {

	public static final String KEY = FIELDS.trip_id.name();

	public TripById(String name) throws IOException {
		super(name, KEY, true);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@Override
		protected Index<GtfsTrip> create(String name) throws IOException {
			return new TripById(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TripById.class.getName(), factory);
	}
}
