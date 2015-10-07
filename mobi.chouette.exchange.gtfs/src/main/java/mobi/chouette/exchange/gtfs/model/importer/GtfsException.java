package mobi.chouette.exchange.gtfs.model.importer;

import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@ToString
//@EqualsAndHashCode(callSuper=false, exclude={"id", "value"})
@EqualsAndHashCode(callSuper=false)
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
		MISSING_FILES,
		INVALID_HEADER_FILE_FORMAT,
		FILE_WITH_NO_ENTRY,
		OPTIONAL_FILE_WITH_NO_ENTRY,
		FILES_WITH_NO_ENTRY,
		EMPTY_HEADER_FIELD,
		DUPLICATE_HEADER_FIELD,
		MISSING_REQUIRED_FIELDS,
		MISSING_REQUIRED_FIELDS2,
		MISSING_REQUIRED_VALUES,
		MISSING_REQUIRED_VALUES2,
		EXTRA_SPACE_IN_HEADER_FIELD,
		EXTRA_SPACE_IN_FIELD,
		HTML_TAG_IN_HEADER_FIELD,
		EXTRA_HEADER_FIELD,
		DUPLICATE_DOUBLE_KEY,
		MISSING_ARRIVAL_TIME,
		MISSING_DEPARTURE_TIME,
		DUPLICATE_STOP_SEQUENCE,
		MISSING_TRANSFER_TIME,
		DEFAULT_VALUE,
		MISSING_OPTIONAL_FILE,
		UNUSED_FILE,
		MISSING_OPTIONAL_FIELD,
		UNUSED_ID,
		SHARED_VALUE,
		UNREFERENCED_ID,
		BAD_REFERENCED_ID,
		NO_LOCATION_TYPE,
		BAD_VALUE,
		NO_PARENT_FOR_STATION,
		DUPLICATE_ROUTE_NAMES,
		CONTAINS_ROUTE_NAMES,
		BAD_COLOR,
		INVERSE_DUPLICATE_ROUTE_NAMES,
		ALL_DAYS_ARE_INVALID,
		START_DATE_AFTER_END_DATE,
		EXCEPT_DATE_WITHOUT_SERVICE,
		EMPTY_SERVICE
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
