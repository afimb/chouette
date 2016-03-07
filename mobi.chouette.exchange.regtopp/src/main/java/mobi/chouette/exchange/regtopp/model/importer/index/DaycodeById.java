package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

@Log4j
public class DaycodeById extends IndexImpl<RegtoppDayCodeDKO>   {

	@Getter
	private RegtoppDayCodeHeaderDKO header;
	
	public DaycodeById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppDayCodeDKO bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
		
		
		
		log.error("Validation code for RegtoppDestination not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new DaycodeById(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(DaycodeById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		
		// First object in list will be the header object, the rest the usual DayCodeDKO objects
		List<Object> rawContent = _parser.getRawContent();
		for(int i=0;i<rawContent.size(); i++) {
			if(i == 0) {
				header = (RegtoppDayCodeHeaderDKO) rawContent.get(i);
			} else {
				RegtoppDayCodeDKO dayCode = (RegtoppDayCodeDKO) rawContent.get(i);
				_index.put(dayCode.getDayCodeId(), dayCode);
				
			}
		}
	}
}
