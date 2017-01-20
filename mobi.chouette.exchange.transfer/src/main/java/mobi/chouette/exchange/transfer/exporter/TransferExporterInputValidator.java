package mobi.chouette.exchange.transfer.exporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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
public class TransferExporterInputValidator extends AbstractInputValidator {


	@Override
	public AbstractParameter toActionParameter(String abstractParameter) {
		try {
			return JSONUtil.fromJSON(abstractParameter, TransferExportParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean checkParameters(String abstractParameterString, String validationParametersString) {

		try {
			TransferExportParameters parameters = JSONUtil.fromJSON(abstractParameterString, TransferExportParameters.class);

			return checkParameters(parameters, null);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}
	}

	
	
	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		
		TransferExportParameters ex = (TransferExportParameters) abstractParameter;
		if((ex.getStartDate() != null && ex.getEndDate() == null) || (ex.getStartDate() == null && ex.getEndDate() != null) ) {
			log.error("Either both startDate and endDate must be set or none of them");
			return false;
		}
		
		if (ex.getStartDate() != null && ex.getEndDate() != null) {
			if (ex.getStartDate().after(ex.getEndDate())) {
				log.error("startDate "+ex.getStartDate()+ " cannot be after endDate "+ex.getEndDate());
				return false;
			}
		}

		
		if(ex.getDestReferentialName() == null) {
			log.error("Destination referential must be set");
			return false;
		}
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
		return Collections.emptyList();
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new TransferExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(TransferExporterInputValidator.class.getName(), new DefaultFactory());
	}



}
