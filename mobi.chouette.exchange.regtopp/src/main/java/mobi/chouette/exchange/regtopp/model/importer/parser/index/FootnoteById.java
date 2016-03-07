package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class FootnoteById extends IndexImpl<RegtoppFootnoteMRK>   {

	public FootnoteById(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		super(validationReporter,fileParser);
	}

	@Override
	public boolean validate(RegtoppFootnoteMRK bean, RegtoppImporter dao) {
		boolean result = true;
		
		if(StringUtils.trimToNull(bean.getDescription()) == null) {
		//	_validationReporter.reportError(new Context(), ex, filenameInfo);
			
			
			//	TODO add entry to _validationReporter
			result = false;
		}
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter,FileContentParser parser) throws Exception {
			return new FootnoteById(validationReporter,parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(FootnoteById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppFootnoteMRK footnote = (RegtoppFootnoteMRK) obj;
			_index.put(footnote.getFootnoteId(), footnote);
		}
	}
}
