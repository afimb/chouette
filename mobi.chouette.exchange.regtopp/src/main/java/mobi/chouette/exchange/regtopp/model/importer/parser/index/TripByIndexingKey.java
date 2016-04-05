package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class TripByIndexingKey extends IndexImpl<RegtoppTripIndexTIX>   {

	public TripByIndexingKey(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
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
			return new TripByIndexingKey(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TripByIndexingKey.class.getName(), factory);
	}


	@Override
	public void index() throws Exception {
		for(Object obj : _parser.getRawContent()) {
			RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) obj;
			RegtoppTripIndexTIX existing = _index.put(trip.getIndexingKey(),trip);
			if(existing != null) {
				// TODO fix exception/validation reporting
				_validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError()), null);
			}
		}
	}
}
