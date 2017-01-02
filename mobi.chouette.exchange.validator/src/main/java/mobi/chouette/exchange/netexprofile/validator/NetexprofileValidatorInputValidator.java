package mobi.chouette.exchange.netexprofile.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.neptune.validation.TestUtils;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.checkpoint.AbstractValidation;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NetexprofileValidatorInputValidator extends NetexprofileImporterInputValidator {

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, NetexprofileValidateParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof NetexprofileValidateParameters)) {
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
			InputValidator result = new NetexprofileValidatorInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NetexprofileValidatorInputValidator.class.getName(), new DefaultFactory());
	}
	
	@Override
	public List<TestDescription> getTestList() {
		List<TestDescription> lstResults = new ArrayList<TestDescription>();
		TestUtils testUtils = TestUtils.getInstance();
		
		lstResults.addAll(testUtils.getTestUtilsList());
		lstResults.addAll(AbstractValidation.getTestLevel3FileList());
		
		return lstResults;
	}

}
