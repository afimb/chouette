package mobi.chouette.exchange.gtfs.model.importer;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GtfsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum ERROR {
		MISSING_FIELD,
		INVALID_FORMAT,
		MISSING_FOREIGN_KEY,
		DUPLICATE_FIELD,
		DUPLICATE_DEFAULT_KEY_FIELD,
		INVALID_FILE_FORMAT,
		SYSTEM,
		MISSING_FILE,
		INVALID_HEADER_FILE_FORMAT,
		FILE_WITH_NO_ENTRY,
		EMPTY_HEADER_FIELD,
		DUPLICATE_HEADER_FIELD,
		MISSING_REQUIRED_FIELDS,
		MISSING_REQUIRED_VALUES,
		EXTRA_SPACE_IN_HEADER_FIELD,
		EXTRA_SPACE_IN_FIELD,
		HTML_TAG_IN_HEADER_FIELD,
		EXTRA_HEADER_FIELD,
		INVALID_URL,
		INVALID_TIMEZONE,
		INVALID_FARE_URL,
		INVALID_LANG
	}

	@Getter
	private String path;
	@Getter
	private Integer id;
	@Getter
	private Integer column;
	@Getter
	private String field;
	@Getter
	private ERROR error;

	@Getter
	private String code;

	@Getter
	private String value;

	public GtfsException(Context context) {
		this(context, null);
	}

	public GtfsException(Context context, Throwable cause) {
		super(cause);
		this.path = (String) context.get(Context.PATH);
		this.id = (Integer) context.get(Context.ID);
		this.column = (Integer) context.get(Context.COLUMN);
		this.field = (String) context.get(Context.FIELD);
		this.error = (ERROR) context.get(Context.ERROR);
		this.code = (String) context.get(Context.CODE);
		this.value = (String) context.get(Context.VALUE);
	}

	public GtfsException(String path, Integer id, String field, ERROR error,
			String code, String value) {
		this(path, id, field, error, code, value, null);
	}

	public GtfsException(String path, Integer id, String field, ERROR error,
			String code, String value, Throwable cause) {
		super(cause);
		this.path = path;
		this.id = id;
		this.field = field;
		this.error = error;
		this.code = code;
		this.value = value;
	}

	public GtfsException(String path, Integer id, Integer column, String field, ERROR error,
			String code, String value) {
		this(path, id, column, field, error, code, value, null);
	}

	public GtfsException(String path, Integer id, Integer column, String field, ERROR error,
			String code, String value, Throwable cause) {
		super(cause);
		this.path = path;
		this.id = id;
		this.column = column;
		this.field = field;
		this.error = error;
		this.code = code;
		this.value = value;
	}

}
