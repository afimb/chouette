package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;

import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public class HubExporterInputValidator implements InputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new HubExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(HubExporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
