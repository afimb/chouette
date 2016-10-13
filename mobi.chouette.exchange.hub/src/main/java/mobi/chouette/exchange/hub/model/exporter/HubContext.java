package mobi.chouette.exchange.hub.model.exporter;

import java.util.HashMap;

import mobi.chouette.exchange.hub.model.HubException;

public class HubContext extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static final String PATH = "path";

	public static final String ID = "id";
	public static final String FIELD = "field";
	public static final String ERROR = "error";
	public static final String CODE = "code";
	public static final String VALUE = "value";

	public HubContext() {
		super();
	}

	public HubContext(String path, int id, String field, HubException.ERROR error, String code, Object value) {
		put(PATH, path);
		put(ID, id);
		put(FIELD, field);
		put(ERROR, error);
		put(CODE, code);
		put(VALUE, value);

	}

	public HubContext(HubContext hubContext) {
		put(PATH, hubContext.get(PATH));
		put(ID, hubContext.get(ID));
		put(FIELD, hubContext.get(FIELD));
		put(ERROR, hubContext.get(ERROR));
		put(CODE, hubContext.get(CODE));
		put(VALUE, hubContext.get(VALUE));

	}

}
