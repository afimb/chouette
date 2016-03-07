package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.util.List;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class DaycodeById extends IndexImpl<RegtoppDayCodeDKO>   {

	@Getter
	private RegtoppDayCodeHeaderDKO header;
	
	public DaycodeById(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
	}

	@Override
	public boolean validate(RegtoppDayCodeDKO bean, RegtoppImporter dao) {
		boolean result = false;
		

		// Mulige valideringssteg
		
		
		
		
		log.error("Validation code for RegtoppDestination not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new DaycodeById(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(DaycodeById.class.getName(), factory);
	}


	@Override
	public void index() throws Exception {
		
		// First object in list will be the header object, the rest the usual DayCodeDKO objects
		List<Object> rawContent = _parser.getRawContent();
		for(int i=0;i<rawContent.size(); i++) {
			if(i == 0) {
				header = (RegtoppDayCodeHeaderDKO) rawContent.get(i);
			} else {
				RegtoppDayCodeDKO dayCode = (RegtoppDayCodeDKO) rawContent.get(i);
				RegtoppDayCodeDKO existing = _index.put(dayCode.getDayCodeId(), dayCode);
				if(existing != null) {
					// TODO fix exception/validation reporting
					_validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError()), null);
				}
				
			}
		}
	}

}
