package mobi.chouette.exchange.regtopp.validation;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

import java.util.Set;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;
import static mobi.chouette.exchange.regtopp.validation.Constant.*;

@Log4j
public class RegtoppValidationReporter {

	@Getter
	private Set<RegtoppException> exceptions = new RegtoppExceptionsHashSet<>();

	public RegtoppValidationReporter(Context context) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		validationReporter.addItemToValidationReport(context, "1-REGTOPP-FILE-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-FIELD-ADMINCODES-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-FILE-NOENTRY-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-FILE-MANDATORY-1", "W");

		validationReporter.addItemToValidationReport(context, "1-REGTOPP-TIX-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-HPL-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-DKO-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-GAV-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-TMS-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-DST-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-MRK-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-LIN-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-TDA-1", "W");
		validationReporter.addItemToValidationReport(context, "1-REGTOPP-STP-1", "W");

		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "TIX", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "HPL", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "DKO", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "GAV", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "TMS", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "DST", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "MRK", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "LIN", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "TDA", 4, "W","W","W","W");
		validationReporter.addItemToValidationReport(context, "2-REGTOPP-", "STP", 4, "W","W","W","W");

		validationReporter.addItemToValidationReport(context, "2-REGTOPP-VLP-1", "W");
	}


	public void dispose() {
		exceptions.clear();
		exceptions = null;
	}

	public void throwUnknownError(Context context, Exception ex, String filenameInfo) throws Exception {
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		String checkPointName = checkPointName(RegtoppException.ERROR.SYSTEM);
		if (filenameInfo != null && filenameInfo.indexOf('.') > 0) {
			String errorMessage = getMessage("label.validation.fileError");
			actionReporter.addFileErrorInReport(context, filenameInfo, ActionReporter.FILE_ERROR_CODE.FILE_NOT_FOUND,
					errorMessage + "\"" + filenameInfo + "\" (" + checkPointName + ") : " + ex.getMessage());
			validationReporter.addCheckPointReportError(context, checkPointName, new DataLocation(filenameInfo), ex.getMessage());
			String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
			log.error(ex, ex);
			throw new Exception(errorMessage+ "\"" + filenameInfo + "\" : " + message);
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
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		switch (ex.getError()) {
			case SYSTEM:
				addError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.error"), ActionReporter.FILE_STATE.ERROR);
				break;
			case MULTIPLE_ADMIN_CODES:
				addMultipleAdminCodesError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case MISSING_MANDATORY_FILES:
				addMissingMandatoryFilesError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case FILE_WITH_NO_ENTRY:
				addFileWithNoEntryError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case TIX_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TIX_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TIX_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TIX_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case HPL_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case HPL_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case HPL_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case HPL_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case DKO_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DKO_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DKO_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DKO_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case GAV_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case GAV_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case GAV_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case GAV_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case DST_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DST_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DST_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case DST_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case MRK_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case MRK_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case MRK_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case MRK_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case LIN_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case LIN_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case LIN_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case LIN_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case TDA_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TDA_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TDA_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case TDA_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case STP_INVALID_MANDATORY_ID_REFERENCE:
				addInvalidMandatoryReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case STP_INVALID_OPTIONAL_ID_REFERENCE:
				addInvalidOptionalReferenceError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case STP_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;
			case STP_DUPLICATE_KEY:
				addDuplicateKeyError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			case VLP_INVALID_FIELD_VALUE:
				addInvalidFieldValueError(context, actionReporter, validationReporter, filenameInfo, ex);
				break;

			default:
				break;
		}
	}

	private void addFileWithNoEntryError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.emptyFile"));
	}

	private void addMissingMandatoryFilesError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.missingMandatoryFile"));
	}

	private void addMultipleAdminCodesError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.multipleAdminCodes"));
	}

	private void addDuplicateKeyError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.duplicateKeyError"));
	}

	private void addInvalidFieldValueError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.invalidFieldValue"));
	}

	private void addInvalidMandatoryReferenceError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.invalidMandatoryReference"));
	}

	private void addInvalidOptionalReferenceError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex) {
		addIgnoredError(context, actionReporter, validationReporter, filenameInfo, ex, getMessage("label.validation.invalidOptionalReference"));
	}

	private void addError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex, String messagePrefix, ActionReporter.FILE_STATE fileState) {
		String checkPointName = checkPointName(ex.getError());
		String message = createMessage(messagePrefix, ex, checkPointName);
		actionReporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
		actionReporter.setFileState(context, filenameInfo, IO_TYPE.INPUT, fileState);
		validationReporter.addCheckPointReportError(context, checkPointName, message, new DataLocation(filenameInfo, ex.getField(), ex.getLineNumber()));
	}

	private void addIgnoredError(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, String filenameInfo, RegtoppException ex, String messagePrefix) {
		addError(context, actionReporter, validationReporter, filenameInfo, ex, messagePrefix, ActionReporter.FILE_STATE.IGNORED);
	}

	private String createMessage(String messagePrefix, RegtoppException ex, String checkPointName) {
		String ruleLabel = getMessage("label.validation.rule");
		String fieldLabel = getMessage("label.validation.field");
		String valueLabel = getMessage("label.validation.value");

		if (ex.getField() == null) {
			return messagePrefix + "' (" + ruleLabel + " " + checkPointName + ")";
		} else {
			String value = (ex.getValue() == null) ? "" : ex.getValue();
			return messagePrefix + " " + fieldLabel + "='" + ex.getField() + "' " + valueLabel + "='" + value + "' (" + ruleLabel + " " + checkPointName + ")";
		}
	}

	private String checkPointName(mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorName) {
		switch (errorName) {
			case SYSTEM:
				return REGTOPP_FILE;
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

			case TMS_INVALID_FIELD_VALUE:
				return REGTOPP_TMS_INVALID_FIELD_VALUE;
			case TMS_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_TMS_INVALID_MANDATORY_ID_REFERENCE;
			case TMS_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_TMS_INVALID_OPTIONAL_ID_REFERENCE;
			case TMS_DUPLICATE_KEY:
				return REGTOPP_TMS_DUPLICATE_KEY;

			case TDA_INVALID_FIELD_VALUE:
				return REGTOPP_TDA_INVALID_FIELD_VALUE;
			case TDA_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_TDA_INVALID_MANDATORY_ID_REFERENCE;
			case TDA_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_TDA_INVALID_OPTIONAL_ID_REFERENCE;
			case TDA_DUPLICATE_KEY:
				return REGTOPP_TDA_DUPLICATE_KEY;

			case STP_INVALID_FIELD_VALUE:
				return REGTOPP_STP_INVALID_FIELD_VALUE;
			case STP_INVALID_MANDATORY_ID_REFERENCE:
				return REGTOPP_STP_INVALID_MANDATORY_ID_REFERENCE;
			case STP_INVALID_OPTIONAL_ID_REFERENCE:
				return REGTOPP_STP_INVALID_OPTIONAL_ID_REFERENCE;
			case STP_DUPLICATE_KEY:
				return REGTOPP_STP_DUPLICATE_KEY;

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
				throw new IllegalArgumentException("Cannot find checkpoint for error " + errorName);
		}
	}


	public void reportSuccess(Context context, String checkpointName, String filenameInfo) {
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		actionReporter.addFileReport(context, filenameInfo, IO_TYPE.INPUT);
		validationReporter.reportSuccess(context, checkpointName, filenameInfo);
	}

	public void validate(Context context, Set<mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR> errorCodes) {
		if (errorCodes != null) {
			for (mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR errorCode : errorCodes) {
				String checkPointName = checkPointName(errorCode);
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.reportSuccess(context, checkPointName);
			}
		}
	}

}
