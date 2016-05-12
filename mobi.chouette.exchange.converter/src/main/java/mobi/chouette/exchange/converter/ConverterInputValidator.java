package mobi.chouette.exchange.converter;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import org.apache.commons.lang.StringUtils;

@Log4j
public class ConverterInputValidator extends AbstractInputValidator {

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, ConvertParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			ConvertParameters parameters = JSONUtil.fromJSON(abstractParameterString, ConvertParameters.class);

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
		if (!(abstractParameter instanceof ConvertParameters)) {
			log.error("invalid parameters for converter " + abstractParameter.getClass().getName());
			return false;
		}

		ConvertParameters parameters = (ConvertParameters) abstractParameter;
		if (parameters.getImportConfiguration() == null) {
			log.error("missing import parameters for converter ");
			return false;
		}
		if (parameters.getExportConfiguration() == null) {
			log.error("missing export parameters for converter ");
			return false;
		}

		InputValidator importValidator = null;
		InputValidator exportValidator = null;

		String importFormat = "unknown";
		if (parameters.getImportConfiguration() instanceof NeptuneImportParameters) {
			importFormat = "neptune";
		} else if (parameters.getImportConfiguration() instanceof GtfsImportParameters) {
			importFormat = "gtfs";
		} else if (parameters.getImportConfiguration() instanceof NetexImportParameters) {
			importFormat = "netex";
		} else {
			log.error("unknown import format for converter " + parameters.getImportConfiguration().getClass().getName());
			return false;
		}
		parameters.getImportConfiguration().setNoSave(true);

		String exportFormat = "unknown";
		if (parameters.getExportConfiguration() instanceof NeptuneExportParameters) {
			exportFormat = "neptune";
		} else if (parameters.getExportConfiguration() instanceof GtfsExportParameters) {
			exportFormat = "gtfs";
		} else if (parameters.getExportConfiguration() instanceof NetexExportParameters) {
			exportFormat = "netex";
		} else {
			log.error("unknown export format for converter " + parameters.getExportConfiguration().getClass().getName());
			return false;
		}

		if (exportFormat.equals(importFormat)) {
			log.error("import and export format are same for converter " + exportFormat);
			return false;
		}

		try {
			importValidator = InputValidatorFactory.create(getCommandInputValidatorName(importFormat, "importer"));
		} catch (ClassNotFoundException | IOException e) {
			log.error("missing import module for converter " + parameters.getImportConfiguration().getClass().getName());
			return false;
		}
		try {
			exportValidator = InputValidatorFactory.create(getCommandInputValidatorName(exportFormat, "exporter"));
		} catch (ClassNotFoundException | IOException e) {
			log.error("missing export module for converter " + parameters.getExportConfiguration().getClass().getName());
			return false;
		}

		if (!importValidator.checkParameters(parameters.getImportConfiguration(), validationParameters))
			return false;
		if (!exportValidator.checkParameters(parameters.getExportConfiguration(), validationParameters))
			return false;
		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		if (fileName == null) {
			log.error("input data expected");
			return false;
		}
		if (!fileName.endsWith(".zip")) {
			log.error("Zip archive input data expected");
			return false;
		}

		return true;
	}

	private String getCommandInputValidatorName(String type, String action) {
		return "mobi.chouette.exchange." + (type.isEmpty() ? "" : type + ".") + action + "."
				+ StringUtils.capitalize(type) + StringUtils.capitalize(action) + "InputValidator";
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new ConverterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(ConverterInputValidator.class.getName(), new DefaultFactory());
	}


}
