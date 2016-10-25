package mobi.chouette.exchange.regtopp.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.importer.updater.DatabaseTestUtils;
import mobi.chouette.exchange.neptune.validation.TestUtils;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.checkpoint.AbstractValidation;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class RegtoppImporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "stop_area" };

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, RegtoppImportParameters.class);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			RegtoppImportParameters parameters = JSONUtil.fromJSON(abstractParameterString, RegtoppImportParameters.class);

			ValidationParameters validationParameters = JSONUtil.fromJSON(validationParametersString,
					ValidationParameters.class);

			return checkParameters(parameters, validationParameters);
		} catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof RegtoppImportParameters)) {
			log.error("invalid parameters for regtopp import " + abstractParameter.getClass().getName());
			return false;
		}

		RegtoppImportParameters parameters = (RegtoppImportParameters) abstractParameter;
		return parameters.isValid(log, allowedTypes);
	}

	@Override
	public boolean checkFilename(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}

		if (!fileName.endsWith(".zip")) {
			log.error("Zip archive expected");
			return false;
		}

		return true;
	}

	@Override
	public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
		return checkFileExistenceInZipWithRegex(fileName, filePath, "(?i).+\\.tix|(?i).+\\.hpl|(?i).+\\.dko");
	}

	protected boolean checkFileExistenceInZipWithRegex(String fileName, Path filePath, String regex) {
		List<String> fileNames = new ArrayList<>();
		if (fileName.endsWith(".zip")) {
			ZipFile zipFile = null;
			File file = null;
			try {
				file = new File(filePath.toString());
				zipFile = new ZipFile(file);
				for (Enumeration<? extends ZipEntry> e = zipFile.entries();
					 e.hasMoreElements();) {
					ZipEntry ze = e.nextElement();
					fileNames.add(ze.getName());
				}
			} catch (IOException e) {
				log.error("Trouble reading zip " + fileName);
			}
		}

		for (String name : fileNames) {
			if (name.matches(regex)){
				return true;
			}
		}

		return false;
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

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new RegtoppImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(RegtoppImporterInputValidator.class.getName(), new DefaultFactory());
	}

}
