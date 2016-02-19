package mobi.chouette.exchange;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

public abstract class AbstractInputValidator implements InputValidator, Constant {
	public boolean initReport(JobData data) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, data);
		ProgressionCommand progression = new ProgressionCommand();
		progression.initialize(context, 1);
		return true;
	}

	/* (non-Javadoc)
	 * @see mobi.chouette.exchange.InputValidator#toValidation(java.lang.String)
	 */
	@Override
	public ValidationParameters toValidation(String validationParameters) {
		try {
			return JSONUtil.fromJSON(validationParameters,
					ValidationParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	

}
