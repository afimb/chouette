package mobi.chouette.exchange.validator;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class ValidatorInputValidator implements InputValidator {

	private static String[] allowedTypes = { "all", "line", "network", "company", "groupofline" };
	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		if (!(abstractParameter instanceof ValidateParameters)) {
			log.error("invalid parameters for validator " + abstractParameter.getClass().getName());
			return false;
		}

		ValidateParameters parameters = (ValidateParameters) abstractParameter;
		String type = parameters.getReferencesType();
		if (type == null || type.isEmpty()) {
			log.error("missing type");
			return false;
		}
		if (!Arrays.asList(allowedTypes).contains(type.toLowerCase())) {
			log.error("invalid type " + type);
			return false;
		}
		
		if (validationParameters == null) {
			log.error("validation parameters expected");
			return false;
		}
		if (fileName != null) {
			log.error("input data not expected");
			return false;
		}

		return true;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new ValidatorInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(ValidatorInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
