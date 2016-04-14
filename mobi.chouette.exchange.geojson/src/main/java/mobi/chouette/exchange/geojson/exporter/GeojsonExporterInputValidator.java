package mobi.chouette.exchange.geojson.exporter;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class GeojsonExporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "network", "company",
			"group_of_line" };

	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter,
					GeojsonExportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean checkParameters(String abstractParameterString,
			String validationParametersString) {

		try {
			GeojsonExportParameters parameters = JSONUtil.fromJSON(
					abstractParameterString, GeojsonExportParameters.class);

			ValidationParameters validationParameters = JSONUtil.fromJSON(
					validationParametersString, ValidationParameters.class);

			return checkParameters(parameters, validationParameters);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean checkParameters(AbstractParameter abstractParameter,
			ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof GeojsonExportParameters)) {
			log.error("invalid parameters for GeoJson export "
					+ abstractParameter.getClass().getName());
			return false;
		}

		GeojsonExportParameters parameters = (GeojsonExportParameters) abstractParameter;
		if (parameters.getStartDate() != null
				&& parameters.getEndDate() != null) {
			if (parameters.getStartDate().after(parameters.getEndDate())) {
				log.error("end date before start date ");
				return false;
			}
		}

		String type = parameters.getReferencesType();
		if (type != null && !type.isEmpty()) {
			if (!Arrays.asList(allowedTypes).contains(type.toLowerCase())) {
				log.error("invalid type " + type);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		if (fileName != null) {
			log.error("input data not expected");
			return false;
		}
		return true;
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GeojsonExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(
				GeojsonExporterInputValidator.class.getName(),
				new DefaultFactory());
	}

}
