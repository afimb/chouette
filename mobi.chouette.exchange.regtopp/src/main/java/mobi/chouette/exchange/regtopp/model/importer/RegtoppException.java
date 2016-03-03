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

	public RegtoppException(Context context) {
		this(context, null);
	}

	public RegtoppException(Context context, Throwable cause) {
		super(cause);

		this.field = (String) context.get(Context.FIELD);
		this.error = (ERROR) context.get(Context.ERROR);
		this.value = (String) context.get(Context.VALUE);
		this.lineNumber = (Integer) context.get(Context.LINE_NUMBER);
		this.errorMessage = (String) context.get(Context.ERROR_MESSAGE);

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
