package mobi.chouette.exchange.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.FileReport.FILE_STATE;

public class ActionReporterImpl implements ActionReporter, Constant{
	
	@Override
	public FileReport findFileReport(Context context, String name) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		for (FileReport fileReport : actionReport.getFiles()) {
			if (fileReport.getName().equals(name))
				return fileReport;
		}
		return null;
	}
	
	@Override
	public FileReport findFileReport(Context context, String name, FILE_STATE state) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		for (FileReport fileReport : actionReport.getFiles()) {
			if (fileReport.getName().equals(name) &&
					(fileReport.getStatus().name().equals(state.name()) ||
							FILE_STATE.OK.equals(fileReport.getStatus().name()) ||
							FILE_STATE.OK.equals(state.name()))) {
				if (FILE_STATE.OK.equals(fileReport.getStatus().name()))
					fileReport.setStatus(state);
				return fileReport;
			}
		}
		return null;
	}
	
	@Override
	public void addFileReport(Context context, String fileInfoName, FILE_STATE state) {
		FileReport fileReport = findFileReport(context, fileInfoName);
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		if (fileReport == null)
		{
			actionReport.addFileReport(new FileReport(fileInfoName, state));
		}
		else
		{
			switch (fileReport.getStatus()) {
			case IGNORED:
				fileReport.setStatus(state);
				break;
			case OK: 
				if (state.equals(FILE_STATE.ERROR))
					fileReport.setStatus(state);
				break;
			case ERROR:
			default:
				break;
			}
		}
	}
	
	@Override
	public void addFileReport(Context context, String fileInfoName, FILE_STATE state, FileError fileError) {
		addFileReport(context, fileInfoName, state);
		FileReport fileReport = findFileReport(context, fileInfoName);
		switch (fileReport.getStatus()) {
		case IGNORED:
		case OK:
			fileReport.setStatus(state);
			break;
		case ERROR:
		default:
			break;
		}
		if (fileReport.getErrors().size() <= 0)
			fileReport.addError(new FileError(FileError.CODE.READ_ERROR, "Il y a des erreurs dans ce fichier."));
		//findFileReport(fileInfoName, state).addError(fileError);
	}

}
