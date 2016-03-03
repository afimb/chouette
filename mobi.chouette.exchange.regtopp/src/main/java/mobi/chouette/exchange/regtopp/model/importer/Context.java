package mobi.chouette.exchange.regtopp.model.importer;

import java.util.HashMap;

public class Context extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static final String PATH = "path";
	public static final String FILE = "file";
	public static final String LINE_NUMBER = "line_number";
	public static final String FIELD = "field";
	public static final String VALUE = "value";
	public static final String ERROR = "error";
	public static final String ERROR_MESSAGE = "error_message";

	public Context() {
		super();
	}

	public Context(String file, int lineNumber, String field, Object value, RegtoppException.ERROR error, String errorMessage) {
		put(FIELD, field);
		put(ERROR, error);
		put(VALUE, value);
		put(LINE_NUMBER, lineNumber);
		put(ERROR_MESSAGE, errorMessage);
		put(FILE,file);
	}

	public Context(String path, String file) {
		put(PATH,path);
		put(FILE,file);
	}

	public Context(Context context) {
		put(FIELD, context.get(FIELD));
		put(ERROR, context.get(ERROR));
		put(VALUE, context.get(VALUE));
		put(LINE_NUMBER, context.get(LINE_NUMBER));
		put(ERROR_MESSAGE, context.get(ERROR_MESSAGE));
		put(PATH, context.get(PATH));
		put(FILE, context.get(FILE));
	}

}
