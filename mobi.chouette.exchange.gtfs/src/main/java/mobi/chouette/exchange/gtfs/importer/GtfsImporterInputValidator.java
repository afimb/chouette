package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;

import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public class GtfsImporterInputValidator implements InputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		// TODO Auto-generated method stub
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
		InputValidatorFactory.factories.put(GtfsImporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
