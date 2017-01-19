package mobi.chouette.exchange.generic.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class GenericImporterInputValidator extends AbstractInputValidator {


	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, GenericImportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		return true;
	}

	
	
	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		
		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<TestDescription> getTestList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GenericImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GenericImporterInputValidator.class.getName(), new DefaultFactory());
	}



}
