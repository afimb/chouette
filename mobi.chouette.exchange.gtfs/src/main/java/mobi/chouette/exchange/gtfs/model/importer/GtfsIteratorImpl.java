package mobi.chouette.exchange.gtfs.model.importer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.ToString;

public class GtfsIteratorImpl implements Iterator<Boolean>, GtfsIterator, Constant {
	
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char DELIMITER = ',';
	public static final char DQUOTE = '"';
	public static final char NULL = 0;

	private ByteBuffer _buffer;
	private int _index;
	private boolean _escape;
	private int _mark;
	private int _position;
	private String _code = "";
	private ByteBuffer _builder = ByteBuffer.allocate(1024);
	private List<Field> _fields = new ArrayList<Field>();

	public GtfsIteratorImpl(ByteBuffer buffer, int count) {
		super();
		setByteBuffer(buffer);
		for (int i = 0; i < count; i++) {
			_fields.add(new Field());
		}
	}

	@Override
	public void setByteBuffer(ByteBuffer buffer) {
		_buffer = buffer;
		_index = 0;
		_escape = false;
		_mark = 0;
		_position = 0;
	}

	@Override
	public void dispose() {
		_buffer.clear();
		_buffer = null;
		_builder.clear();
		_builder = null;
		_fields.clear();
		_fields = null;
	}

	@Override
	public boolean hasNext() {
		return _buffer.hasRemaining() && (_mark < _buffer.limit());
	}

	@Override
	public Boolean next() {
		try {
			_code = "";
			_escape = false;
			_buffer.position(_mark);
			while (_buffer.hasRemaining()) {

				if (_index >= _fields.size()) {
					_fields.add(new Field());
				}

				char value = (char) _buffer.get();
				switch (value) {
				case CR:
				case LF:
					if (_escape) {
						_code = NL_IN_TOKEN;
						return false; // new line inside a token
					}
					_fields.get(_index).offset = _mark;
					_fields.get(_index).length = _buffer.position() - 1 - _mark;
					if (value == CR) {
						_mark = _buffer.position() + 1;
					} else {
						_mark = _buffer.position();
					}
					_index = 0;
					_position = _mark;
					return true;
				case DELIMITER:
					if (!_escape) {
						_fields.get(_index).offset = _mark;
						_fields.get(_index).length = (_buffer.position() - 1 - _mark);
						_mark = _buffer.position();
						_index++;
					}
					break;
				case DQUOTE:
					if (!_escape) { // start DQUOTE token
						int previous = previousByte();
						if (previous == DELIMITER || previous == CR || previous == LF) { 
							_escape = true;
						} else { // a problem : only part of this token is encolosed between DQUOTE
							_code = DQUOTE_WITH_NO_ESCAPE;
							return false; // a DQUOTE that dosen't start a token
						}
					} else {
						int next = nextByte();
						if (next == DELIMITER || next == CR || next == LF) { // end DQOUTE token
							 _escape = false;
						} else if (next == NULL) { // EOF 
							_code = EOF_WITHOUT_NL;
							return false;
						} else if (next == DQUOTE) { // double quote in a token
							_buffer.get();
						} else { // a problem : only part of this token is encolosed between DQUOTE
							_code = TEXT_AFTER_ESCAPE_DQUOTE;
							return false; // a DQUOTE that dosen't end a token
						}
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception ignored) {

		}
		// MISSING LN/LF AT THE END OF THE END OF THE FILE
		return false;
	}

	private byte nextByte() {
		int position = _buffer.position();
		if (!_buffer.hasRemaining())
			return NULL;
		byte result = _buffer.get();
		_buffer.position(position);
		return result;
	}
	
	private byte previousByte(){
		int position = _buffer.position();
		if (position <= 1)
			return DELIMITER;
		_buffer.position(position-2);
		byte result = _buffer.get();
		_buffer.position(position);
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getValue() {
		Field last = _fields.get(_fields.size() - 1);
		int offset = _fields.get(0).offset;
		int length = last.offset + last.length - offset;
		return getText(offset, length);
	}

	@Override
	public String getValue(int index) {
		int length = _fields.get(index).length;
		int offset = _fields.get(index).offset;
		return getText(offset, length);
	}

	@Override
	public int getFieldCount() {
		return _fields.size();
	}

	public String getCode() {
		return _code;
	}
	
	@Override
 	public int getPosition() {
		return _position;
	}

	@Override
	public void setPosition(int position) {
		_buffer.position(position);
		_mark = position;
	}

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

	private String getText(int offset, int length) {
		String result;
		_buffer.position(offset);
		//boolean escape = false;
		_builder.clear();
		boolean quotedString = false;
		for (int i = 0; i < length; i++) {
			byte c = _buffer.get();
			if (i == 0 && c == DQUOTE) {
				quotedString = true;
				continue;
			}
			if (i== length-1 && quotedString &&  c == DQUOTE) {
				break;
			}
			if ( c == DQUOTE ) { // As it was validated with next() method the next character must be a DQUOTE
			    _buffer.get();
			    i++;
			}
			_builder.put(c);
		}
		result = new String(_builder.array(), 0, _builder.position(), StandardCharsets.UTF_8);
		return result;
	}

	@ToString
	class Field {
		int offset;
		int length;
	}

}
