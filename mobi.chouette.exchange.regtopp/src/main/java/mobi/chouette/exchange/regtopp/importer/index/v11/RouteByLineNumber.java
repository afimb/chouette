package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import org.apache.commons.lang.StringUtils;

@Log4j
public class RouteByLineNumber extends IndexImpl<RegtoppRouteTDA> {

	public RouteByLineNumber(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByLineNumber(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteByLineNumber.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppRouteTDA route = (RegtoppRouteTDA) obj;
			route.setRouteId(route.getRecordLineNumber());
			index.put(route.getRouteId(), route); // File linenumber
		}
	}

	@Override
	public boolean validate(RegtoppRouteTDA bean, RegtoppImporter dao) {
		boolean result = true;

		if (StringUtils.trimToNull(bean.getStopId()) != null) {
			bean.getOkTests().add(RegtoppException.ERROR.TDA_INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(RegtoppRouteTDA.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Holdeplassnr", null, RegtoppException.ERROR.TDA_INVALID_FIELD_VALUE, "")));
			result = false;
		}

		return result;
	}
}
