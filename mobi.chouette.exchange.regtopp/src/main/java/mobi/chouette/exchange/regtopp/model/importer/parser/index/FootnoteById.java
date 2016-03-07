package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;

@Log4j
public class FootnoteById extends IndexImpl<RegtoppFootnoteMRK>   {

	public FootnoteById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
	
		
		
		log.error("Validation code for RegtoppFootnote not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new FootnoteById(parser);
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
