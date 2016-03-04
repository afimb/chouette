package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

@Log4j
public class TripByIndexingKey extends IndexImpl<RegtoppTripIndexTIX>   {

	public TripByIndexingKey(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;

		log.warn("TripByIndexingKey validation not implemented");
		
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new TripByIndexingKey(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TripByIndexingKey.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) obj;
			_index.put(trip.getIndexingKey(),trip);
		}
	}
}
