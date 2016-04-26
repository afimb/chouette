package mobi.chouette.exchange.regtopp.importer.index.v11;

import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

public class PathwayByIndexingKey extends IndexImpl<AbstractRegtoppPathwayGAV> {

	public PathwayByIndexingKey(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(AbstractRegtoppPathwayGAV bean, RegtoppImporter dao) {
		boolean result = true;

		if (bean.getDescription() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		} else {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Gangveitekst", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}

		if (bean.getDuration() != null) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_FIELD_VALUE);
		} else if (bean.getDuration() == null) {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Gangtid", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		} else if (bean.getDuration() <= 0) {
			bean.getErrors().add(new RegtoppException(new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getRecordLineNumber(),
					"Gangtid", null, RegtoppException.ERROR.INVALID_FIELD_VALUE, "")));
			result = false;
		}
		
		// Verify stops
		String from = bean.getStopIdFrom();
		String to = bean.getStopIdTo();
		
		
		Index<AbstractRegtoppStopHPL> stopById = dao.getStopById();
		
		if(stopById.containsKey(from)) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors()
					.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getRecordLineNumber(),
							"Holdeplassnr fra", from,
							RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
			
		}
		if(stopById.containsKey(to)) {
			bean.getOkTests().add(RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE);
		} else {
			bean.getErrors()
					.add(new RegtoppException(new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getRecordLineNumber(),
							"Holdeplassnr til", from,
							RegtoppException.ERROR.INVALID_MANDATORY_ID_REFERENCE, "Unreferenced id.")));
			result = false;
			
		}
		
		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new PathwayByIndexingKey(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(PathwayByIndexingKey.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			AbstractRegtoppPathwayGAV route = (AbstractRegtoppPathwayGAV) obj;
			AbstractRegtoppPathwayGAV existing = index.put(route.getIndexingKey(), route);
			if (existing != null) {
				continue; // we want to check > 0 occurences
			}
		}
	}
}
