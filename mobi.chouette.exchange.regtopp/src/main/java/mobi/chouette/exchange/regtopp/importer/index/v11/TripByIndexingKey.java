package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class TripByIndexingKey extends TripIndex {

	public TripByIndexingKey(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new TripByIndexingKey(context, validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TripByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppTripIndexTIX newRecord = (AbstractRegtoppTripIndexTIX) obj;
			AbstractRegtoppTripIndexTIX existingRecord = index.put(newRecord.getIndexingKey(), newRecord);
			if (existingRecord != null) {
				if(!existingRecord.equals(newRecord)) {
					// Since not equal, try to find a new tripId and reinsert record
					
					// First make sure first entry in tix file remains untouched, it is the newRecord we want to update with a new tripId
					index.remove(newRecord.getIndexingKey());
					index.put(existingRecord.getIndexingKey(),existingRecord);
					
					
					int counter = 1;
					boolean foundSlot = false;
					String originalTripId = newRecord.getTripId();
					
					// Iterate over possible tripIds with the counter value appended
					while(!foundSlot) {
						newRecord.setTripId(originalTripId+counter);
						foundSlot = !index.containsKey(newRecord.getIndexingKey());
						if(foundSlot) {
							index.put(newRecord.getIndexingKey(),newRecord);
							log.warn("Duplicate key in TIX file. Existing: "+existingRecord+" New updated trip: "+newRecord);
						}
						counter++;
					}
				} else {
					log.warn("Duplicate key in TIX file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				}
				validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
						newRecord.getRecordLineNumber(), getMessage("label.regtoppTripIndexTIX.lineId") + "/" + getMessage("label.regtoppTripIndexTIX.tripId"), newRecord.getIndexingKey(), ERROR.TIX_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
			}
		}
	}
}
