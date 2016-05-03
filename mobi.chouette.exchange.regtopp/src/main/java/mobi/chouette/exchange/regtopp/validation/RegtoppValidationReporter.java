package mobi.chouette.exchange.regtopp.validation;

import static mobi.chouette.common.Constant.*;
import static mobi.chouette.exchange.regtopp.validation.Constant.*;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileError.CODE;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import org.codehaus.jettison.json.JSONException;

@Log4j
public class RegtoppValidationReporter {

	@Getter
	private Set<RegtoppException> exceptions = new RegtoppExceptionsHashSet<RegtoppException>();

	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		String checkPointName = checkPointName(RegtoppException.ERROR.SYSTEM);

		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			report.addFileInfo(filenameInfo, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND,
					"A problem occured while reading the file \"" + filenameInfo + "\" (" + checkPointName + ") : " + ex.getMessage()));
			validationReport.addDetail(checkPointName, new Location(filenameInfo), ex.getMessage(), CheckPoint.RESULT.NOK);
			String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
			log.error(ex, ex);
			throw new Exception("A problem occured while reading the file \"" + filenameInfo + "\" : " + message);
		}
	}

	public void reportErrors(Context context, Set<RegtoppException> errors, String filename) throws Exception {
		for (RegtoppException error : errors) {
			reportError(context, error, filename);
		}
	}

	public void reportError(Context context, RegtoppException ex, String filenameInfo) throws Exception {
		if (!exceptions.add(ex))
			return;
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		switch (ex.getError()) {
			case SYSTEM:
				addError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.ERROR, "Error ");
				break;
//			case INVALID_FIELD_VALUE:
//				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
//				break;
			case MULTIPLE_ADMIN_CODES:
				addMultipleAdminCodesError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case MISSING_MANDATORY_FILES:
				addMissingMandatoryFilesError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case FILE_WITH_NO_ENTRY:
				addFileWithNoEntryError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case TIX_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TIX_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TIX_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TIX_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case HPL_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case HPL_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case HPL_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case HPL_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case DKO_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DKO_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DKO_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DKO_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case GAV_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case GAV_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case GAV_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case GAV_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case DST_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DST_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DST_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case DST_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case MRK_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case MRK_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case MRK_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case MRK_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case LIN_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case LIN_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case LIN_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case LIN_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case TDA_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TDA_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TDA_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case TDA_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case STP_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case STP_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case STP_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;
			case STP_DUPLICATE_KEY:
				addDuplicateKeyError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			case VLP_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(actionReport, validationReport, filenameInfo, ex, FileError.CODE.INVALID_FORMAT, FILE_STATE.IGNORED);
				break;

			default:
				break;
		}
	}

	private void addFileWithNoEntryError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex, CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Empty file");
	}

	private void addMissingMandatoryFilesError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex, CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Duplicate key value");
	}

	private void addMultipleAdminCodesError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex, CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Duplicate key value");
	}

	private void addDuplicateKeyError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex, CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Duplicate key value");
	}

	private void addInvalidFieldValueError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex,
			CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Invalid field value");
	}

	private void addInvalidMandatoryReferenceError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex,
			CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Invalid mandatory reference");
	}

	private void addInvalidOptionalReferenceError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex,
			CODE invalidFormat, FILE_STATE ignored) {
		addError(actionReport, validationReport, filenameInfo, ex, invalidFormat, ignored, "Invalid optional reference");
	}

	private void addError(ActionReport actionReport, ValidationReport validationReport, String filenameInfo, RegtoppException ex, FileError.CODE fileErrorCode,
			FILE_STATE fileState, String messagePrefix) {
		String checkPointName = checkPointName(ex.getError());

		String message = createMessage(messagePrefix, ex, checkPointName);
		addError(actionReport, filenameInfo, new FileError(fileErrorCode, messagePrefix + " field='" + ex.getField() + "' value='"+ex.getValue()+"' (rule " + checkPointName + ")"));
		actionReport.addFileInfo(filenameInfo, fileState);
		validationReport.addDetail(checkPointName, new Location(filenameInfo, ex.getField(), ex.getLineNumber(), null), ex.getValue(), ex.getField(), ex.getErrorMessage(),
				CheckPoint.RESULT.UNCHECK);

	}

	private String createMessage(String messagePrefix, RegtoppException ex, String checkPointName) {
		if (ex.getField() == null) {
			return messagePrefix + "' (rule " + checkPointName + ")";
		} else {
			String value = (ex.getValue() == null) ? "" : ex.getValue();
			return messagePrefix + " field='" + ex.getField() + "' value='" + value + "' (rule " + checkPointName + ")";
		}
	}

	/* Add FileError detail manually as ActionReport does not work properly */
	private void addError(ActionReport actionReport, String filename, FileError error) {
		List<FileInfo> files = actionReport.getFiles();
		for (FileInfo f : files) {
			if (f.getName().equals(filename)) {
				f.addError(error);
				break;
			}
		}
	}

	private String checkPointName(mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorName) {
		switch (errorName) {
			case SYSTEM:
				return REGTOPP_FILE;
//			case INVALID_FIELD_VALUE:
//				return REGTOPP_INVALID_FIELD_VALUE;

			case MULTIPLE_ADMIN_CODES:
				return REGTOPP_MULTIPLE_ADMIN_CODES;
			case MISSING_MANDATORY_FILES:
				return REGTOPP_MISSING_MANDATORY_FILES;
			case FILE_WITH_NO_ENTRY:
				return REGTOPP_FILE_WITH_NO_ENTRY;


			case TIX_INVALID_FIELD_VALUE:
				return REGTOPP_TIX_INVALID_FIELD_VALUE;
			case TIX_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_TIX_INVALID_MANDATORY_ID_REFERENCE;
			case TIX_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_TIX_INVALID_OPTIONAL_ID_REFERENCE;
			case TIX_DUPLICATE_KEY:
				return REGTOPP_TIX_DUPLICATE_KEY;

			case HPL_INVALID_FIELD_VALUE:
				return REGTOPP_HPL_INVALID_FIELD_VALUE;
			case HPL_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_HPL_INVALID_MANDATORY_ID_REFERENCE;
			case HPL_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_HPL_INVALID_OPTIONAL_ID_REFERENCE;
			case HPL_DUPLICATE_KEY:
				return REGTOPP_HPL_DUPLICATE_KEY;

			case DKO_INVALID_FIELD_VALUE:
				return REGTOPP_DKO_INVALID_FIELD_VALUE;
			case DKO_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_DKO_INVALID_MANDATORY_ID_REFERENCE;
			case DKO_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_DKO_INVALID_OPTIONAL_ID_REFERENCE;
			case DKO_DUPLICATE_KEY:
				return REGTOPP_DKO_DUPLICATE_KEY;

			case GAV_INVALID_FIELD_VALUE:
				return REGTOPP_GAV_INVALID_FIELD_VALUE;
			case GAV_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_GAV_INVALID_MANDATORY_ID_REFERENCE;
			case GAV_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_GAV_INVALID_OPTIONAL_ID_REFERENCE;
			case GAV_DUPLICATE_KEY:
				return REGTOPP_GAV_DUPLICATE_KEY;

			case DST_INVALID_FIELD_VALUE:
				return REGTOPP_DST_INVALID_FIELD_VALUE;
			case DST_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_DST_INVALID_MANDATORY_ID_REFERENCE;
			case DST_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_DST_INVALID_OPTIONAL_ID_REFERENCE;
			case DST_DUPLICATE_KEY:
				return REGTOPP_DST_DUPLICATE_KEY;

			case MRK_INVALID_FIELD_VALUE:
				return REGTOPP_MRK_INVALID_FIELD_VALUE;
			case MRK_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_MRK_INVALID_MANDATORY_ID_REFERENCE;
			case MRK_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_MRK_INVALID_OPTIONAL_ID_REFERENCE;
			case MRK_DUPLICATE_KEY:
				return REGTOPP_MRK_DUPLICATE_KEY;

			case LIN_INVALID_FIELD_VALUE:
				return REGTOPP_LIN_INVALID_FIELD_VALUE;
			case LIN_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_LIN_INVALID_MANDATORY_ID_REFERENCE;
			case LIN_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_LIN_INVALID_OPTIONAL_ID_REFERENCE;
			case LIN_DUPLICATE_KEY:
				return REGTOPP_LIN_DUPLICATE_KEY;

			case VLP_INVALID_FIELD_VALUE:
				return REGTOPP_VLP_INVALID_FIELD_VALUE;

			default:
				return null;
		}
	}

	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		report.addFileInfo(filenameInfo, FILE_STATE.OK);
		if (validationReport.findCheckPointByName(checkpointName).getState() == CheckPoint.RESULT.UNCHECK)
			validationReport.findCheckPointByName(checkpointName).setState(CheckPoint.RESULT.OK);
	}

	public void validate(Context context, Set<mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR> errorCodes) {
		if (errorCodes != null)
			for (mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorCode : errorCodes) {
				String checkPointName = checkPointName(errorCode);
				ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

				CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointName);

				if (checkPoint != null)
                    if (checkPoint.getState() == CheckPoint.RESULT.UNCHECK)
                        checkPoint.setState(CheckPoint.RESULT.OK);

			}
	}

}
