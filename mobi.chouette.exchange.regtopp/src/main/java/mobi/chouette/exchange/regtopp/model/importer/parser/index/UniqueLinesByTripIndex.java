package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class UniqueLinesByTripIndex extends IndexImpl<RegtoppTripIndexTIX>   {

	public UniqueLinesByTripIndex(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
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
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new UniqueLinesByTripIndex(validationReporter,parser);
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
			_index.put(trip.getLineId(),null);
			log.info("Found line "+trip.getLineId());
		}
	}
}
