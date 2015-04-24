package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;

import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public class GtfsExporterInputValidator implements InputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GtfsExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GtfsExporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
