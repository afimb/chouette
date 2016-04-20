package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class UniqueLinesByTripIndex extends TripIndex {

	public UniqueLinesByTripIndex(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new UniqueLinesByTripIndex(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(UniqueLinesByTripIndex.class.getName(), factory);
	}

	@Override
	public void index() throws IOException {
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppTripIndexTIX trip = (AbstractRegtoppTripIndexTIX) obj;
			index.put(trip.getLineId(), null);
		}
	}
}
