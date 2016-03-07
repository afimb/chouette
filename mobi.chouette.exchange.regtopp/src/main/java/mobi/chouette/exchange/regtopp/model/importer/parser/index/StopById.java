package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;

@Log4j
public class StopById extends IndexImpl<RegtoppStopHPL>   {

	public StopById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
		// Koordinater ulike
		// Sone 1 og 2 forskjellige
		// Fullstendig navn !§= kortnavn
		
		// Holdeplassnummer X antall siffer
		
		
		log.error("Validation code for RegtoppStopp not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new StopById(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppStopHPL stop = (RegtoppStopHPL) obj;
			_index.put(stop.getStopId(), stop);
		}
	}
}
