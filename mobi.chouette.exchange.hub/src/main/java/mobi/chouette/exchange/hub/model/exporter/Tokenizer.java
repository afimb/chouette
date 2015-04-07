package mobi.chouette.exchange.hub.model.exporter;

import java.util.List;

public class Tokenizer {
	public static final char DELIMITER = ';';
	public static final char DQUOTE = '"';

	public static final String untokenize(List<String> values) {
		StringBuilder builder = new StringBuilder();

		final int size = values.size();
		for (int i = 0; i < size; i++) {
			String value = values.get(i);
			if (value.contains("\"") || value.contains(""+DELIMITER)) {
				builder.append(DQUOTE);
				final int length = value.length();
				for (int j = 0; j < length; j++) {
					char c = value.charAt(j);
					if (c == DQUOTE) {
						builder.append(DQUOTE);
					}
					builder.append(c);
				}
				builder.append(DQUOTE);
			} else {
				builder.append(value);
			}
			if (i + 1 < size) {
				builder.append(DELIMITER);
			}
		}

		return builder.toString();
	}


}
