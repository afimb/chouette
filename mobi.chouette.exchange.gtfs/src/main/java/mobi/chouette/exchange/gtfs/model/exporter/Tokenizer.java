package mobi.chouette.exchange.gtfs.model.exporter;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char DELIMITER = ',';
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

	public static final List<String> tokenize(String text) {

		final StringBuilder builder = new StringBuilder();
		final List<String> tokens = new ArrayList<String>();
		boolean escape = false;
		int length = text.length();

		for (int i = 0; i < length; i++) {
			final char c = text.charAt(i);
			if (!escape) {
				if (c == DELIMITER) {
					tokens.add(builder.toString());
					builder.delete(0, builder.length());
				} else if (i + 1 == length) {
					builder.append(c);
					tokens.add(builder.toString());
					builder.delete(0, builder.length());
				} else if (c == DQUOTE) {
					if (i == 0) {
						escape = true;
					} else {
						if (text.charAt(i + 1) == DQUOTE) {
							if (i + 2 < length) {
								if (text.charAt(i + 2) != DELIMITER) {
									builder.append(c);
									i++;
									escape = true;
								} else {
									escape = true;
								}
							} else {
								escape = true;
							}
						} else {
							escape = true;
						}
					}
				} else {
					builder.append(c);
				}

			} else {
				if (c == DQUOTE) {
					if (i + 1 < length) {
						if (text.charAt(i + 1) == DQUOTE) {
							builder.append(c);
							i++;
						} else {
							escape = false;
						}
					} else {
						escape = false;
						tokens.add(builder.toString());
						builder.delete(0, builder.length());
					}
				} else {
					builder.append(c);
				}
			}

		}

		return tokens;
	}

}
