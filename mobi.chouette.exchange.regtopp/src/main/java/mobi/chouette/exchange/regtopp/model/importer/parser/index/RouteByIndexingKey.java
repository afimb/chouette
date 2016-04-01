package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class RouteByIndexingKey extends IndexImpl<RegtoppRouteTMS>   {

	public RouteByIndexingKey(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
	}

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;

		log.warn("RouteByIndexingKey validation not implemented");
		
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new RouteByIndexingKey(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteByIndexingKey.class.getName(), factory);
	}


	@Override
	public void index() throws Exception {
		for(Object obj : _parser.getRawContent()) {
			RegtoppRouteTMS route = (RegtoppRouteTMS) obj;
			RegtoppRouteTMS existing = _index.put(route.getIndexingKey(),route);
			if(existing != null) {
				// TODO fix exception/validation reporting
				_validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError()), null);
			}
		}
	}
}
