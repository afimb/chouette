package mobi.chouette.exchange.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

public class ActionReporterImpl implements ActionReporter, Constant{

	@Override
	public void addFileReport(Context context, String fileInfoName, FILE_STATE state) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		FileReport fileReport = actionReport.findFileReport(fileInfoName);
		
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
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		FileReport fileReport = actionReport.findFileReport(fileInfoName);
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
		
	}

	@Override
	public void setActionError(Context context, ERROR_CODE code, String description) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		ActionError2 actionError = new ActionError2(code, description);
		
		if(actionReport != null)
			actionReport.setFailure(actionError);
		
	}

	@Override
	public void addObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		if(actionReport != null)
			actionReport.addObjectReport(new ObjectReport(objectId, type, description, status, ioType));
	}

	@Override
	public void addObjectReportToSpecificCollection(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		if(actionReport != null)
			actionReport.addObjectReportToSpecificCollection(new ObjectReport(objectId, type, description, status, ioType));
	}

	@Override
	public void addErrorToObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType, ERROR_CODE code, String descriptionError) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		if(actionReport != null) {
			ObjectReport object = new ObjectReport(objectId, type, description, status, ioType);
			object.addError(new ObjectError2(code, descriptionError));
		}
	}

	@Override
	public void addStatToObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType, String statType) {
		ActionReport2 actionReport = (ActionReport2) context.get(REPORT);
		if(actionReport != null) {
			ObjectReport object = new ObjectReport(objectId, type, description, status, ioType);
			object.addStatTypeToObject(statType);
		}	
	}
}
