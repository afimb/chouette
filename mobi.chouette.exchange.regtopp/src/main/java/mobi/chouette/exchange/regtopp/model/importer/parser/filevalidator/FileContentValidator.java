package mobi.chouette.exchange.regtopp.model.importer.parser.filevalidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;

@Log4j
public abstract class FileContentValidator {

	public abstract void validate(Context _context, FileContentParser parser);

	protected void validateUniqueKeys(Context _context, FileContentParser parser) {
		List<Object> rawContent = parser.getRawContent();
		
		
		// Make sure unique
		Set<String> uniqueKeys = new HashSet<String>();
		Set<String> duplicatedKeys = new HashSet<String>();
		
		
		for(Object o : rawContent) {
			RegtoppObject i = (RegtoppObject) o;
			
			String key = i.getIndexingKey();
			if(uniqueKeys.contains(key)) {
				duplicatedKeys.add(key);
			} else {
				uniqueKeys.add(key);
			}
			
			// Validate objects (no relations)
			
			
			
			
		}
		
		// Make sure only unique keys
		log.error("Unique keys not implemented");
		
	}
}
