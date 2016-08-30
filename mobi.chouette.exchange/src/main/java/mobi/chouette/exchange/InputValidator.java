package mobi.chouette.exchange;

import java.nio.file.Path;
import java.util.List;

import mobi.chouette.common.JobData;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public interface InputValidator {
	AbstractParameter toActionParameter(String abstractParameter);

	ValidationParameters toValidation(String validationParameters);

	boolean checkParameters(String abstractParameter, String validationParameters);

	boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters);

	boolean checkFilename(String fileName);
	
	boolean checkFile(String fileName, Path filePath, AbstractParameter abstractParameter);
	
	boolean initReport(JobData data);
	
	List<Test> getTestList();
}
