package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;

import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public class NeptuneExporterInputValidator implements InputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new NeptuneExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NeptuneExporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
