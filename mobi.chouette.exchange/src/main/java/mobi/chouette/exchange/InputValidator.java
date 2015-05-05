package mobi.chouette.exchange;

import mobi.chouette.common.JobData;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;


public interface InputValidator {
boolean check(AbstractParameter abstractParameter,ValidationParameters validationParameters, String fileName);
boolean initReport(JobData data);
}
