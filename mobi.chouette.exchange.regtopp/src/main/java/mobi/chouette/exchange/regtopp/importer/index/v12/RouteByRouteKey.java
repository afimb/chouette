package mobi.chouette.exchange.regtopp.importer.index.v12;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class RouteByRouteKey extends RouteIndex {

	public RouteByRouteKey(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByRouteKey(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteByRouteKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppRouteTMS route = (RegtoppRouteTMS) obj;
			if (route == null){
				throw new IllegalArgumentException("Route");
			}
			RegtoppRouteTMS existing = index.put(route.getRouteKey(), route);
			if (existing != null) {
				continue; // we want to check > 0 occurences
			}
		}
	}
}
