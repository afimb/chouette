package mobi.chouette.exchange.regtopp.model.importer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class RegtoppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum ERROR {
		MULTIPLE_ADMIN_CODES,
		MISSING_MANDATORY_FILES,
		INVALID_FIELD_VALUE,
		SYSTEM,
		FILE_WITH_NO_ENTRY
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

	public RegtoppException(FileParserValidationContext context) {
		this(context, null);
	}

	public RegtoppException(FileParserValidationContext context, Throwable cause) {
		super(cause);

		this.field = (String) context.get(FileParserValidationContext.FIELD);
		this.error = (ERROR) context.get(FileParserValidationContext.ERROR);
		this.value = (String) context.get(FileParserValidationContext.VALUE);
		this.lineNumber = (Integer) context.get(FileParserValidationContext.LINE_NUMBER);
		this.errorMessage = (String) context.get(FileParserValidationContext.ERROR_MESSAGE);

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
