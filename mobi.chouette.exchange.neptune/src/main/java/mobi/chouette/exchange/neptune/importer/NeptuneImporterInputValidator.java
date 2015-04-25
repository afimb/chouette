package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NeptuneImporterInputValidator implements InputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		if (!(abstractParameter instanceof NeptuneImportParameters)) {
			log.error("invalid parameters for Neptune import " + abstractParameter.getClass().getName());
			return false;
		}
		if (fileName  == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}
		
		if (!fileName.endsWith(".zip") && !fileName.endsWith(".xml")) {
			log.error("xml or Zip archive input data expected");
			return false;
		}

		return true;

	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new NeptuneImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NeptuneImporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
