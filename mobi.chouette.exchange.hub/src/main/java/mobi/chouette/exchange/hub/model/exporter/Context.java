package mobi.chouette.exchange.hub.model.exporter;

import java.util.HashMap;

import mobi.chouette.exchange.hub.model.HubException;

public class Context extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static final String PATH = "path";

	public static final String ID = "id";
	public static final String FIELD = "field";
	public static final String ERROR = "error";
	public static final String CODE = "code";
	public static final String VALUE = "value";

	public Context() {
		super();
	}

	public Context(String path, int id, String field, HubException.ERROR error, String code, Object value) {
		put(PATH, path);
		put(ID, id);
		put(FIELD, field);
		put(ERROR, error);
		put(CODE, code);
		put(VALUE, value);

	}

	public Context(Context context) {
		put(PATH, context.get(PATH));
		put(ID, context.get(ID));
		put(FIELD, context.get(FIELD));
		put(ERROR, context.get(ERROR));
		put(CODE, context.get(CODE));
		put(VALUE, context.get(VALUE));

	}

}
