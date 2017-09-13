package mobi.chouette.exchange.regtopp.importer.parser;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class ControlCharacterFilteringReader extends FilterReader {

	final static int WHITE_SPACE_CHARACTER = Character.getNumericValue(' ');

	protected ControlCharacterFilteringReader(Reader in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		int read = super.read();
		if (Character.isIdentifierIgnorable(read)) {
			read = WHITE_SPACE_CHARACTER;
		}
		return read;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int read = super.read(cbuf, off, len);

		if (read == -1) {
			return -1;
		}

		for (int readPos = off; readPos < off + read; readPos++) {
			if (Character.isIdentifierIgnorable(cbuf[readPos])) {
				cbuf[readPos] = ' ';
			}
		}
		return read;
	}
}
