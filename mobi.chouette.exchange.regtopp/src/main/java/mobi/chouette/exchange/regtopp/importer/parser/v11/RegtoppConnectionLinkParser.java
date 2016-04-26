package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppConnectionLinkParser extends LineSpecificParser {

	/*
	 * Validation rules of type I and II are checked during this step, and results are stored in reports.
	 */
	// TODO. Rename this function "parse(Context context)".
	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode.

		// Det som kan sjekkes her er at antall poster stemmer og at alle referanser til andre filer er gyldige

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();

		ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		validateGAVIndex(context, importer, validationReporter);
	}

	private void validateGAVIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasGAVImporter()) {
	//	TODO	validationReporter.reportSuccess(context, Constant.REGTOPP_FILE_GAV, AbstractRegtoppPathwayGAV.FILE_EXTENSION);

			Index<AbstractRegtoppPathwayGAV> index = importer.getPathwayByIndexingKey();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(AbstractRegtoppPathwayGAV.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), AbstractRegtoppPathwayGAV.FILE_EXTENSION);
			}

			for (AbstractRegtoppPathwayGAV bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex);
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, AbstractRegtoppPathwayGAV.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, AbstractRegtoppPathwayGAV.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), AbstractRegtoppPathwayGAV.FILE_EXTENSION);
				validationReporter.validate(context, AbstractRegtoppPathwayGAV.FILE_EXTENSION, bean.getOkTests());
			}
		}
	}

	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@SuppressWarnings("deprecation")
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);

		if (importer.hasGAVImporter()) {
			Index<AbstractRegtoppPathwayGAV> routeIndex = importer.getPathwayByIndexingKey();

			for (AbstractRegtoppPathwayGAV pathway : routeIndex) {

				String chouetteStartStopAreaObjectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY,
						pathway.getStopIdFrom());

				String chouetteEndStopAreaObjectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY,
						pathway.getStopIdTo());

				
				if(!referential.getSharedStopAreas().containsKey(chouetteStartStopAreaObjectId)) {
					log.error("StopArea (ConnectionLink start) "+chouetteStartStopAreaObjectId+" does not exist in shipment");
					// TODO report with validation reporter
				} else if(!referential.getSharedStopAreas().containsKey(chouetteEndStopAreaObjectId)) {
					// TODO report with validation reporter
					log.error("StopArea (ConnectionLink end) "+chouetteEndStopAreaObjectId+" does not exist in shipment");
				} else {
					StopArea startStopArea = ObjectFactory.getStopArea(referential, chouetteStartStopAreaObjectId);
					StopArea endStopArea = ObjectFactory.getStopArea(referential, chouetteEndStopAreaObjectId);

					String chouetteConnectionLinkId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.CONNECTIONLINK_KEY,
							pathway.getStopIdFrom() + "-" + pathway.getStopIdTo());
					ConnectionLink connectionLink = ObjectFactory.getConnectionLink(referential, chouetteConnectionLinkId);

					connectionLink.setDefaultDuration(new java.sql.Time(0, pathway.getDuration(), 0));
					connectionLink.setComment(pathway.getDescription());
					connectionLink.setStartOfLink(startStopArea);
					connectionLink.setEndOfLink(endStopArea);
				}
			}
		}

	}

	static {
		ParserFactory.register(RegtoppConnectionLinkParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppConnectionLinkParser();
			}
		});
	}

}
