package mobi.chouette.exchange.report;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.FileReport.FILE_STATE;
import mobi.chouette.exchange.validation.report.ValidationReporterImpl;

public interface ActionReporter {
	FileReport findFileReport(Context context, String name);
	FileReport findFileReport(Context context, String name, FILE_STATE state);
	void addFileReport(Context context, String fileInfoName, FILE_STATE state);
	void addFileReport(Context context, String fileInfoName, FILE_STATE state, FileError fileError);
	
	/**
	 * Factory for using action reporter instance
	 * @author gjamot
	 *
	 */
	public class Factory {
		private static ActionReporter actionReporter;
		
		public static synchronized ActionReporter getInstance() {
			if(actionReporter == null) {
				actionReporter = new ActionReporterImpl();
			}
			
			return actionReporter;
		}
	}
}
