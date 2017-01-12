package mobi.chouette.exchange.generic.exporter;

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
public class GenericExporterInputValidator extends AbstractInputValidator {


	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, GenericExportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			GenericExportParameters parameters = JSONUtil.fromJSON(abstractParameterString, GenericExportParameters.class);

			return checkParameters(parameters, null);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	
	
	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		// TODO Auto-generated method stub
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
			InputValidator result = new GenericExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GenericExporterInputValidator.class.getName(), new DefaultFactory());
	}



}
