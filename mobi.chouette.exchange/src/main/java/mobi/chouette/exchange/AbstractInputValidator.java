package mobi.chouette.exchange;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.report.ActionReport;

public abstract class AbstractInputValidator implements InputValidator, Constant {
	public boolean initReport(JobData data) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, data);
		ProgressionCommand progression = new ProgressionCommand();
		progression.initialize(context, 1);
		return true;
	}
}
