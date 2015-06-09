package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class GtfsImporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "stop_area" };

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof GtfsImportParameters)) {
			log.error("invalid parameters for gtfs import " + abstractParameter.getClass().getName());
			return false;
		}

		GtfsImportParameters parameters = (GtfsImportParameters) abstractParameter;
		String prefix = parameters.getObjectIdPrefix();
		if (prefix == null || prefix.isEmpty()) {
			log.error("missing object_id_prefix");
			return false;
		}

		String type = parameters.getReferencesType();
		if (type != null && !type.isEmpty()) {
			if (!Arrays.asList(allowedTypes).contains(type.toLowerCase())) {
				log.error("invalid type " + type);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}

		if (!fileName.endsWith(".zip") && !fileName.endsWith(".txt")) {
			log.error("Zip archive or txt input data expected");
			return false;
		}

		return true;
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GtfsImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GtfsImporterInputValidator.class.getName(), new DefaultFactory());
	}

}
