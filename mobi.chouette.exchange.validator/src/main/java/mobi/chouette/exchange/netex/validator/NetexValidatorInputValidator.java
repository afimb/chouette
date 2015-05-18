package mobi.chouette.exchange.netex.validator;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.netex.importer.NetexImporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NetexValidatorInputValidator extends NetexImporterInputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		if (!(abstractParameter instanceof NetexValidateParameters)) {
			log.error("invalid parameters for validator " + abstractParameter.getClass().getName());
			return false;
		}
		if (validationParameters == null) {
			log.error("no validation parameters for validation ");
			return false;
		}

		return super.check(abstractParameter, validationParameters, fileName);
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new NetexValidatorInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NetexValidatorInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
