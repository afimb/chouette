package mobi.chouette.exchange.dummy.importer;

import java.nio.file.Path;
import java.util.List;

import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public class DummyImporterInputValidator extends AbstractInputValidator {

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, DummyImportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean checkParameters(String abstractParameter, String validationParameters) {
		return true;
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		return true;
	}
	
	@Override
	public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
		return true;
	}

	@Override
	public List<TestDescription> getTestList() {
		return null;
	}

}
