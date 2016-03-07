package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class RouteById extends IndexImpl<RegtoppRouteTMS>   {

	public RouteById(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
	}

	@Override
	public boolean validate(RegtoppRouteTMS bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
		// Koordinater ulike
		// Sone 1 og 2 forskjellige
		// Fullstendig navn !§= kortnavn
		
		// Holdeplassnummer X antall siffer
		
		
		log.warn("Validation code for RegtoppRoute not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new RouteById(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppRouteTMS stop = (RegtoppRouteTMS) obj;
			_index.put(stop.getRouteId(), stop);
		}
	}
}
