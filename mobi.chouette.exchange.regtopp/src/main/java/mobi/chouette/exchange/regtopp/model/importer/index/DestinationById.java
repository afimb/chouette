package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

@Log4j
public class DestinationById extends IndexImpl<RegtoppDestinationDST>   {

	public DestinationById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppDestinationDST bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
		
		
		
		log.error("Validation code for RegtoppDestination not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new DestinationById(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(DestinationById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppDestinationDST destination = (RegtoppDestinationDST) obj;
			_index.put(destination.getDestinationId(), destination);
		}
	}
}
