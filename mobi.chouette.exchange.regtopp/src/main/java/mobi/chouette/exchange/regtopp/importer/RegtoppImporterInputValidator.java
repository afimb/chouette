package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class RegtoppImporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "stop_area" };

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof RegtoppImportParameters)) {
			log.error("invalid parameters for regtopp import " + abstractParameter.getClass().getName());
			return false;
		}

		RegtoppImportParameters parameters = (RegtoppImportParameters) abstractParameter;
		return parameters.isValid(log, allowedTypes);
	}

	@Override
	public boolean checkFilename(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}

		if (!fileName.endsWith(".zip")) {
			log.error("Zip archive expected");
			return false;
		}

		return true;
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new RegtoppImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(RegtoppImporterInputValidator.class.getName(), new DefaultFactory());
	}

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		throw new RuntimeException("toActionParameter not implemented for abstractParameter "+abstractParameter);
	}

	@Override
	public boolean checkParameters(String abstractParameter, String validationParameters) {
		throw new RuntimeException("checkParameters not implemented for abstractParameter "+abstractParameter+ " and validationParameters "+validationParameters);
	}

}
