package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

@Log4j
public class PathwayByIndexingKey extends IndexImpl<AbstractRegtoppPathwayGAV> {

	public PathwayByIndexingKey(Context context, RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(context, validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppPathwayGAV bean, RegtoppImporter dao) throws Exception {
		boolean result = true;

		if (bean.getDescription() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.GAV_INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
					getMessage("label.regtoppPathwayGAV.descriptionHack"), null, RegtoppException.ERROR.GAV_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}

		if (bean.getDuration() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.GAV_INVALID_FIELD_VALUE);
		} else if (bean.getDuration() == null) {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
					getMessage("label.regtoppPathwayGAV.duration"), null, RegtoppException.ERROR.GAV_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		} else if (bean.getDuration() <= 0) {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
					getMessage("label.regtoppPathwayGAV.duration"), null, RegtoppException.ERROR.GAV_INVALID_FIELD_VALUE, getMessage("label.validation.invalidFieldValue"))));
			result = false;
		}
		
		// Verify stops
		String from = bean.getStopIdFrom();
		String to = bean.getStopIdTo();
		
		
		Index<AbstractRegtoppStopHPL> stopById = dao.getStopById();
		
		if(stopById.containsKey(from)) {
			bean.getOkTests().add(RegtoppException.ERROR.GAV_INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors()
					.add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
							getMessage("label.regtoppPathwayGAV.stopIdFrom"), from,
							RegtoppException.ERROR.GAV_INVALID_MANDATORY_ID_REFERENCE, getMessage("label.validation.invalidMandatoryReference"))));
			result = false;
			
		}
		if(stopById.containsKey(to)) {
			bean.getOkTests().add(RegtoppException.ERROR.GAV_INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors()
					.add(new RegtoppException(new FileParserValidationError(getUnderlyingFilename(), bean.getRecordLineNumber(),
							getMessage("label.regtoppPathwayGAV.stopIdTo"), from,
							RegtoppException.ERROR.GAV_INVALID_MANDATORY_ID_REFERENCE, getMessage("label.validation.invalidMandatoryReference"))));
			result = false;
			
		}
		
		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new PathwayByIndexingKey(context, validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(PathwayByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppPathwayGAV newRecord = (AbstractRegtoppPathwayGAV) obj;
			AbstractRegtoppPathwayGAV existingRecord = index.put(newRecord.getIndexingKey(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in GAV file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(context, new RegtoppException(new FileParserValidationError(getUnderlyingFilename(),
						newRecord.getRecordLineNumber(), getMessage("label.regtoppPathwayGAV.stopIdFrom")+"/"+getMessage("label.regtoppPathwayGAV.stopIdTo"), newRecord.getIndexingKey(), ERROR.GAV_DUPLICATE_KEY, getMessage("label.validation.duplicateKeyError"))), getUnderlyingFilename());
			}
		}
	}
}
