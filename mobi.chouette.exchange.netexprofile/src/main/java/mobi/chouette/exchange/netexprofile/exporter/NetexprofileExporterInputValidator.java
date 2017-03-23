package mobi.chouette.exchange.netexprofile.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
public class NetexprofileExporterInputValidator extends AbstractInputValidator {

    private static String[] allowedTypes = {"line", "network", "company", "group_of_line"};

    @Override
    public AbstractParameter toActionParameter(String abstractParameter) {
        try {
            return JSONUtil.fromJSON(abstractParameter, NetexprofileExportParameters.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean checkParameters(String abstractParameterString, String validationParametersString) {

        try {
            NetexprofileExportParameters parameters = JSONUtil.fromJSON(abstractParameterString, NetexprofileExportParameters.class);

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
        if (!(abstractParameter instanceof NetexprofileExportParameters)) {
            log.error("invalid parameters for Netexprofile export " + abstractParameter.getClass().getName());
            return false;
        }

        NetexprofileExportParameters parameters = (NetexprofileExportParameters) abstractParameter;
        if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
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

    @Override
    public boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter) {
        if (fileName != null) {
            log.error("input data not expected");
            return false;
        }
        return true;
    }

    public static class DefaultFactory extends InputValidatorFactory {

        @Override
        protected InputValidator create() throws IOException {
            InputValidator result = new NetexprofileExporterInputValidator();
            return result;
        }
    }

    static {
        InputValidatorFactory.factories.put(NetexprofileExporterInputValidator.class.getName(), new DefaultFactory());
    }

    @Override
    public List<TestDescription> getTestList() {
        List<TestDescription> emptyList = new ArrayList<TestDescription>();
        return emptyList;
    }

}
