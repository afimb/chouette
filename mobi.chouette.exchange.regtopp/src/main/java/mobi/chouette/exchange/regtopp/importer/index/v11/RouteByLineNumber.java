package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
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

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class RouteByLineNumber extends IndexImpl<RegtoppRouteTDA> {

	public RouteByLineNumber(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new RouteByLineNumber(context, validationReporter, parser);
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
					getMessage("label.regtoppRouteTDA.stopId"), bean.getStopId(), RegtoppException.ERROR.TDA_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		return result;
	}
}
