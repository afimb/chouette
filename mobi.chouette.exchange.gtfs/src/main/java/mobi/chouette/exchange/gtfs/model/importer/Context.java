package mobi.chouette.exchange.gtfs.model.importer;

import java.util.HashMap;

public class Context extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static final String PATH = "path";

	public static final String ID = "id";
	public static final String FIELD = "field";
	public static final String ERROR = "error";
	public static final String CODE = "code";
	public static final String VALUE = "value";
	public static final String COLUMN = "column";

	public Context() {
		super();
	}

	public Context(String path, int id, int column, String field,
			GtfsException.ERROR error, String code, Object value) {
		put(PATH, path);
		put(ID, id);
		put(FIELD, field);
		put(ERROR, error);
		put(CODE, code);
		put(VALUE, value);
		put(COLUMN, column);
	}

	public Context(Context context) {
		put(PATH, context.get(PATH));
		put(ID, context.get(ID));
		put(FIELD, context.get(FIELD));
		put(ERROR, context.get(ERROR));
		put(CODE, context.get(CODE));
		put(VALUE, context.get(VALUE));
		put(COLUMN, context.get(COLUMN));
	}

}
