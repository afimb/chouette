package mobi.chouette.exchange.regtopp.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;

@ToString
@EqualsAndHashCode(callSuper = false)
public class RegtoppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum ERROR {
		MULTIPLE_ADMIN_CODES,
		MISSING_MANDATORY_FILES,
//		INVALID_FIELD_VALUE,
		SYSTEM,
		FILE_WITH_NO_ENTRY,
//		MISSING_KEY,
//		DUPLICATE_KEY,
		HPL_DUPLICATE_KEY,
		HPL_INVALID_FIELD_VALUE,
		HPL_INVALID_MANDATORY_ID_REFERENCE,
		HPL_INVALID_OPTIONAL_ID_REFERENCE,
		TIX_DUPLICATE_KEY,
		TIX_INVALID_FIELD_VALUE,
		TIX_INVALID_MANDATORY_ID_REFERENCE,
		TIX_INVALID_OPTIONAL_ID_REFERENCE,
		DKO_DUPLICATE_KEY,
		DKO_INVALID_FIELD_VALUE,
		DKO_INVALID_MANDATORY_ID_REFERENCE,
		DKO_INVALID_OPTIONAL_ID_REFERENCE,
		GAV_DUPLICATE_KEY,
		GAV_INVALID_FIELD_VALUE,
		GAV_INVALID_MANDATORY_ID_REFERENCE,
		GAV_INVALID_OPTIONAL_ID_REFERENCE,
		TMS_DUPLICATE_KEY,
		TMS_INVALID_FIELD_VALUE,
		TMS_INVALID_MANDATORY_ID_REFERENCE,
		TMS_INVALID_OPTIONAL_ID_REFERENCE,
		DST_DUPLICATE_KEY,
		DST_INVALID_FIELD_VALUE,
		DST_INVALID_MANDATORY_ID_REFERENCE,
		DST_INVALID_OPTIONAL_ID_REFERENCE,
		MRK_DUPLICATE_KEY,
		MRK_INVALID_FIELD_VALUE,
		MRK_INVALID_MANDATORY_ID_REFERENCE,
		MRK_INVALID_OPTIONAL_ID_REFERENCE,
		LIN_DUPLICATE_KEY,
		LIN_INVALID_FIELD_VALUE,
		LIN_INVALID_MANDATORY_ID_REFERENCE,
		LIN_INVALID_OPTIONAL_ID_REFERENCE,
		TDA_DUPLICATE_KEY,
		TDA_INVALID_FIELD_VALUE,
		TDA_INVALID_MANDATORY_ID_REFERENCE,
		TDA_INVALID_OPTIONAL_ID_REFERENCE,
		STP_DUPLICATE_KEY,
		STP_INVALID_FIELD_VALUE,
		STP_INVALID_MANDATORY_ID_REFERENCE,
		STP_INVALID_OPTIONAL_ID_REFERENCE,
		VLP_INVALID_FIELD_VALUE
	}

	@Getter
	private String field;

	@Getter
	private ERROR error;

	@Getter
	private String value;

	@Getter
	private Integer lineNumber;

	@Getter
	private String errorMessage;

	public RegtoppException(FileParserValidationError context) {
		this(context, null);
	}

	public RegtoppException(FileParserValidationError context, Throwable cause) {
		super(cause);

		this.field = (String) context.get(FileParserValidationError.FIELD);
		this.error = (ERROR) context.get(FileParserValidationError.ERROR);
		this.value = (String) context.get(FileParserValidationError.VALUE);
		this.lineNumber = (Integer) context.get(FileParserValidationError.LINE_NUMBER);
		this.errorMessage = (String) context.get(FileParserValidationError.ERROR_MESSAGE);

	}

	public boolean isFatal() {
		switch (error) {
		case MULTIPLE_ADMIN_CODES:
		case MISSING_MANDATORY_FILES:
			return true;
		default:
			return false;
		}
	}

}
