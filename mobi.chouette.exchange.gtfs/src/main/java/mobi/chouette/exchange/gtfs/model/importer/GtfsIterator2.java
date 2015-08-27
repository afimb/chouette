package mobi.chouette.exchange.gtfs.model.importer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.exporter.Tokenizer;

public class GtfsIterator2 implements Iterator<Boolean>, GtfsIterator, Constant {
	
	private String _code = "";

	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char DELIMITER = ',';
	public static final char DQUOTE = '"';

	private ByteBuffer _buffer;
	private List<String> _tokens = new ArrayList<String>(0);
	private ByteBuffer _builder = ByteBuffer.allocate(1024);
	private String _text;

	public GtfsIterator2(ByteBuffer buffer, int size) {
		_buffer = buffer;
	}

	@Override
	public void setByteBuffer(ByteBuffer buffer) {
		_buffer = buffer;

	}

	@Override
	public void dispose() {
		_buffer.clear();
	}

	@Override
	public boolean hasNext() {
		return _buffer.hasRemaining();
	}

	@Override
	public Boolean next() {
		boolean result = false;
		try {
			_builder.clear();
			loop: while (_buffer.hasRemaining()) {
				byte value = _buffer.get();
				switch (value) {
				case CR:
					break;
				case LF:
					_text = new String(_builder.array(), 0, _builder.position());
					_tokens = Tokenizer.tokenize(_text);
					result = true;
					break loop;
				default:
					_builder.put(value);
					break;
				}
			}
		} catch (Exception ignored) {

		}

		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFieldCount() {
		return _tokens.size();
	}

	@Override
	public String getValue() {
		return _text;
	}

	@Override
	public String getValue(int index) {
		String result = null;
		if (index < _tokens.size()) {
			result = _tokens.get(index);
		}
		return result;
	}

	public String getCode() {
		return _code;
	}

	@Override
	public int getPosition() {
		return _buffer.position();
	}

	@Override
	public void setPosition(int position) {
		_buffer.position(position);
	}

	@Override
	public ByteBuffer getBuffer() {
		ByteBuffer result = null;
		int offset = _buffer.position();
		try {
			while (_buffer.hasRemaining()) {
				byte value = _buffer.get();
				if (value == LF) {
					int length = _buffer.position() - offset;
					result = getBuffer(offset, length);
					break;
				}
			}
		} catch (Exception ignored) {

		}
		return result;
	}

	private ByteBuffer getBuffer(int offset, int length) {
		_buffer.position(offset);
		ByteBuffer result = _buffer.slice();
		result.limit(length);
		return result;
	}

}
