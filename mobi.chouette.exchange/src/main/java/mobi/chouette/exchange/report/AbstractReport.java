package mobi.chouette.exchange.report;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractReport {
	public static final int maxErrors = 15;

	public abstract void print(PrintStream out, StringBuilder ret, int level, boolean first);

	public StringBuilder toJsonString(StringBuilder ret, int level, String name, Object value, boolean first) {
		ret.setLength(0);
		if (!first)
			ret.append(',');
		addLevel(ret.append('\n'), level).append('"').append(name).append("\": ");
		if (value instanceof String) {
			return jsonString(ret, (String) value);
		} else if (value instanceof Enum) {
			return ret.append('"').append(value).append('"');
		} else if (value instanceof Number) {
			return ret.append(value);
		} else if (value instanceof Boolean) {
			return ret.append(value);
		}
		return ret.append("\"\""); // may not arrive
	}

	public StringBuilder addLevel(StringBuilder ret, int level) {
		for (int i = 0; i < level; i++)
			ret.append("  ");
		return ret;
	}

	public StringBuilder jsonString(StringBuilder ret, String value) {
		if (value == null)
			return ret.append("\"\"");
		ret.append('"');

		for (char car : value.toCharArray()) {
			if (car == '\\' || car == '/' || car == '"')
				ret.append('\\');
			ret.append(car);
		}

		return ret.append('"');
//		return ret
//				.append(value.replaceAll("[\\\\]", "\\\\\\\\").replaceAll("[\\/]", "\\\\/")
//						.replaceAll("[\"]", "\\\\\"")).append('"');
	}


	public void printIntArray(PrintStream out, StringBuilder ret, int level, String name,
	                          Collection<? extends Number> values, boolean first) {
		ret.setLength(0); // clear buffer
		if (!first)
			ret.append(',');
		addLevel(ret.append('\n'), level).append('"').append(name).append("\": [");
		if (values.isEmpty()) {
			out.print(ret.append(']'));
		} else if (values.size() == 1) {
			Number value = values.iterator().next();
			out.print(ret.append(value).append(']'));
		} else {
			out.print(ret);
			first = true;
			for (Number value : values) {
				ret.setLength(0);
				if (!first)
					ret.append(',');
				out.print(addLevel(ret.append('\n'), level + 1).append(value));
				first = false;
			}
			ret.setLength(0);
			out.print(addLevel(ret.append('\n'), level).append(']'));
		}
		ret.setLength(0);

	}

	public void printArray(PrintStream out, StringBuilder ret, int level, String name,
	                       Collection<? extends AbstractReport> objects, boolean first) {
		ret.setLength(0); // clear buffer
		if (!first)
			ret.append(',');
		out.print(addLevel(ret.append('\n'), level).append('"').append(name).append("\": ["));
		first = true;
		for (AbstractReport abstractReport : objects) {
			ret.setLength(0); // clear buffer
			if (!first)
				ret.append(',');
			out.print(ret.append('\n'));
			abstractReport.print(out, ret, level + 1, first);
			first = false;
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append(']'));
		ret.setLength(0);
	}

	public void printObject(PrintStream out, StringBuilder ret, int level, String name, AbstractReport object,
	                        boolean first) {
		ret.setLength(0); // clear buffer
		if (!first)
			ret.append(',');
		out.print(addLevel(ret.append('\n'), level).append('"').append(name).append("\":"));
		object.print(out, ret, level + 1, true);
		ret.setLength(0);
	}

	/**
	 * Prints a key / primitive value map to the out stream provided.
	 *
	 * @param out          Out stream
	 * @param ret          String builder to reuse.
	 * @param level        Recursivity.
	 * @param name         object name
	 * @param mappedValues values to print.
	 * @param first        Recursivity.
	 */
	public void printMap(PrintStream out, StringBuilder ret, int level, String name, Map<? extends Object, ? extends Object> mappedValues, boolean first) {
		ret.setLength(0); // clear buffer
		if (!first)
			ret.append(',');
		out.print(addLevel(ret.append('\n'), level).append('"').append(name).append("\":"));
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		first = true;
		for (Object key : mappedValues.keySet()) {
			out.print(toJsonString(ret, level + 1, key.toString().toLowerCase(), mappedValues.get(key), first));
			first = false;
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));
		ret.setLength(0);
	}

}
