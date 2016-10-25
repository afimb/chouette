package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.gtfs.validation.TestUtils;
import mobi.chouette.exchange.importer.updater.DatabaseTestUtils;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.checkpoint.AbstractValidation;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class GtfsImporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "stop_area" };

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, GtfsImportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			GtfsImportParameters parameters = JSONUtil.fromJSON(abstractParameterString, GtfsImportParameters.class);

			ValidationParameters validationParameters = JSONUtil.fromJSON(validationParametersString,
					ValidationParameters.class);

			return checkParameters(parameters, validationParameters);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof GtfsImportParameters)) {
			log.error("invalid parameters for gtfs import " + abstractParameter.getClass().getName());
			return false;
		}

		GtfsImportParameters parameters = (GtfsImportParameters) abstractParameter;
		return parameters.isValid(log, allowedTypes);
	}

	@Override
	public boolean checkFilename(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}

		if (!fileName.endsWith(".zip") && !fileName.endsWith(".txt")) {
			log.error("Zip archive or txt input data expected");
			return false;
		}

		return true;
	}
	
	@Override
	public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
		return checkFileExistenceInZip(fileName, filePath, "txt");
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GtfsImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GtfsImporterInputValidator.class.getName(), new DefaultFactory());
	}

	@Override
	public List<TestDescription> getTestList() {
		TestUtils testUtils = TestUtils.getInstance();
		DatabaseTestUtils dbTestUtils = DatabaseTestUtils.getInstance();
		List<TestDescription> lstTestWithDatabase = new ArrayList<TestDescription>();
		lstTestWithDatabase.addAll(testUtils.getTestUtilsList());
		lstTestWithDatabase.addAll(dbTestUtils.getTestUtilsList());
		lstTestWithDatabase.addAll(AbstractValidation.getTestLevel3FileList());
		
		return lstTestWithDatabase;
	}

}
