package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NetexprofileImporterInputValidator extends AbstractInputValidator {

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, NetexprofileImportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			NetexprofileImportParameters parameters = JSONUtil.fromJSON(abstractParameterString, NetexprofileImportParameters.class);

			ValidationParameters validationParameters = JSONUtil.fromJSON(validationParametersString, ValidationParameters.class);

			return checkParameters(parameters, validationParameters);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof NetexprofileImportParameters)) {
			log.error("invalid parameters for Netex import " + abstractParameter.getClass().getName());
			return false;
		}

		NetexprofileImportParameters parameters = (NetexprofileImportParameters) abstractParameter;

		// Validate profile parameters
		if (StringUtils.trimToNull(parameters.getValidCodespaces()) != null) {
			String[] validCodespacesTuples = StringUtils.split(parameters.getValidCodespaces(), ",");
			if (validCodespacesTuples.length > 0) {
				// Check for odd numbers of commas
				if (validCodespacesTuples.length % 2 != 0) {
					log.error("Unable to decode valid codepsaces "+parameters.getValidCodespaces()+" String must be comma separated with a pattern like PREFIX,URL,PREFIX,URL, ...");
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			log.error("input data expected");
			return false;
		}

		if (!fileName.endsWith(".zip") && !fileName.endsWith(".xml")) {
			log.error("xml or Zip archive input data expected");
			return false;
		}

		return true;
	}

	@Override
	public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
		// TODO
		return true;
	}

	@Override
	public List<TestDescription> getTestList() {
		throw new UnsupportedOperationException("TODO");
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new NetexprofileImporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NetexprofileImporterInputValidator.class.getName(), new DefaultFactory());
	}

}
