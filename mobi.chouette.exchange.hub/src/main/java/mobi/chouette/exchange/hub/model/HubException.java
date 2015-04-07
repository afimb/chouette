package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.ToString;
import mobi.chouette.exchange.hub.model.exporter.Context;

@ToString
public class HubException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum ERROR {
		MISSING_FIELD, INVALID_FORMAT, MISSING_FOREIGN_KEY, DUPLICATE_FIELD, INVALID_FILE_FORMAT, SYSTEM
	}

	@Getter
	private String path;
	@Getter
	private Integer id;
	@Getter
	private String field;
	@Getter
	private ERROR error;

	@Getter
	private String code;

	@Getter
	private String value;

	public HubException(Context context) {
		this(context, null);
	}

	public HubException(Context context, Throwable cause) {
		super(cause);
		this.path = (String) context.get(Context.PATH);
		this.id = (Integer) context.get(Context.ID);
		this.field = (String) context.get(Context.FIELD);
		this.error = (ERROR) context.get(Context.ERROR);
		this.value = (String) context.get(Context.VALUE);
	}

	public HubException(String path, Integer id, String field, ERROR error,
			String code, String value) {
		this(path, id, field, error, code, value, null);
	}

	public HubException(String path, Integer id, String field, ERROR error,
			String code, String value, Throwable cause) {
		super(cause);
		this.path = path;
		this.id = id;
		this.field = field;
		this.error = error;
		this.code = code;
		this.value = value;
	}

}
