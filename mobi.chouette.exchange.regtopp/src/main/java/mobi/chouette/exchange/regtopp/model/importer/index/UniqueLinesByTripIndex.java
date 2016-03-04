package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

@Log4j
public class UniqueLinesByTripIndex extends IndexImpl<RegtoppTripIndexTIX>   {

	public UniqueLinesByTripIndex(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppTripIndexTIX bean, RegtoppImporter dao) {
		boolean result = true;

		log.warn("TripByIndexingKey validation not implemented");
		
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new UniqueLinesByTripIndex(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(UniqueLinesByTripIndex.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) obj;
			_index.put(trip.getTripId(),trip);
		}
	}
}
