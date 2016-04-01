package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class StopsOnRouteByRoute extends IndexImpl<List<RegtoppRouteTMS>>   {

	public StopsOnRouteByRoute(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
	}

	@Override
	public boolean validate(List<RegtoppRouteTMS> bean, RegtoppImporter dao) {
		boolean result = true;
		

		//TODO
		
		
		log.warn("Validation code for RegtoppRouteTMS not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new StopsOnRouteByRoute(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopsOnRouteByRoute.class.getName(), factory);
	}


	@Override
	public void index() throws Exception {
		for(Object obj : _parser.getRawContent()) {
			
			RegtoppRouteTMS stopPointOnRoute = (RegtoppRouteTMS) obj;
			String key = stopPointOnRoute.getLineId()+stopPointOnRoute.getDirection()+stopPointOnRoute.getRouteId();
			
			List<RegtoppRouteTMS> stopPoints = _index.get(stopPointOnRoute);
			if(stopPoints == null) {
				stopPoints = new ArrayList<RegtoppRouteTMS>();
				_index.put(key,stopPoints);
			}
			
			stopPoints.add(stopPointOnRoute);
		}
	}
}
