package mobi.chouette.exchange.netex.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.netex.importer.NetexImporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.checkpoint.AbstractValidation;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NetexValidatorInputValidator extends NetexImporterInputValidator {

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, NetexValidateParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof NetexValidateParameters)) {
			log.error("invalid parameters for validator " + abstractParameter.getClass().getName());
			return false;
		}
//		if (validationParameters == null) {
//			log.error("no validation parameters for validation ");
//			return false;
//		}

		return super.checkParameters(abstractParameter, validationParameters);
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
	
	@Override
	public List<TestDescription> getTestList() {
		List<TestDescription> lstResults = new ArrayList<TestDescription>();
		lstResults.addAll(AbstractValidation.getTestLevel3FileList());
		
		return lstResults;
	}
	

}
