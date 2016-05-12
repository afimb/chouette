package mobi.chouette.exchange.regtopp.importer.index.v13;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class StopPointByStopId extends IndexImpl<List<RegtoppStopPointSTP>> {

	public StopPointByStopId(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new StopPointByStopId(context, validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopPointByStopId.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppStopPointSTP stopPoint = (RegtoppStopPointSTP) obj;
			List<RegtoppStopPointSTP> listOfStopPoints = index.get(stopPoint.getStopId());
			if(listOfStopPoints == null) {
				listOfStopPoints = new ArrayList<>();
				index.put(stopPoint.getStopId(),listOfStopPoints);
			}
			// TODO check if list already have this stopPoint registered
			listOfStopPoints.add(stopPoint);
		}
	}

	@Override
	public boolean validate(List<RegtoppStopPointSTP> bean, RegtoppImporter dao) {
		// TODO Auto-generated method stub
		return false;
	}
}
