package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.ToString;

public class GtfsIteratorImpl implements Iterator<Boolean>, GtfsIterator {
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char DELIMITER = ',';
	public static final char DQUOTE = '"';

	private ByteBuffer _buffer;
	private int _index = 0;
	private boolean _escape = false;
	private int _mark = 0;
	private List<Field> _fields = new ArrayList<Field>();
	private int _position;
	private ByteBuffer _builder = ByteBuffer.allocate(1024);

	public GtfsIteratorImpl(ByteBuffer buffer, int count) {
		super();
		_buffer = buffer;
		for (int i = 0; i < count; i++) {
			_fields.add(new Field());
		}
	}

	@Override
	public void dispose() {
		_buffer.clear();
	}

	@Override
	public boolean hasNext() {
		return _buffer.hasRemaining() && (_mark < _buffer.limit());
	}

	@Override
	public Boolean next() {
		boolean result = false;
		try {
			_buffer.position(_mark);
			loop: while (_buffer.hasRemaining()) {

				if (_index >= _fields.size()) {
					_fields.add(new Field());
				}

				char value = (char) _buffer.get();
				switch (value) {
				case CR:
				case LF: {
					_fields.get(_index).offset = _mark;
					_fields.get(_index).length = _buffer.position() - 1
							- _fields.get(_index).offset;
					if (value == CR) {
						_mark = _buffer.position() + 1;
					} else {
						_mark = _buffer.position();
					}
					_index = 0;
					_position = _mark;
					result = true;
					break loop;
				}
				case DELIMITER: {
					if (!_escape) {
						_fields.get(_index).offset = _mark;
						_fields.get(_index).length = (_buffer.position() - 1 - _fields
								.get(_index).offset);
						_mark = _buffer.position();
						_index++;

					}
					break;
				}
				case DQUOTE: {
					if (!_escape) {
						_escape = true;
					} else {
						int next = nextByte();
						if (next == DELIMITER || next == CR || next == LF) {
							_escape = false;
						} else if (next == DQUOTE) {
							_buffer.get();
						}
					}
					break;
				}
				default:
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
	public String getValue(int index) {
		int length = _fields.get(index).length;
		int offset = _fields.get(index).offset;
		return getText(offset, length);
	}

	@Override
	public int getFieldCount() {
		return _fields.size();
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

	private byte nextByte() {
		int position = _buffer.position();
		byte result = _buffer.get();
		_buffer.position(position);
		return result;
	}

	private String getText(int offset, int length) {
		String result;
		_buffer.position(offset);
		boolean escape = false;
		_builder.clear();
		for (int i = 0; i < length; i++) {
			byte c = _buffer.get();
			if (!escape) {
				if (c == DQUOTE) {
					if (i == 0) {
						escape = true;
					} else if (i + 1 < length) {
						byte next = nextByte();
						if (next == DQUOTE && i < length - 1) {
							_builder.put(c);
						} else {
							escape = true;
						}
					}
				} else {
					_builder.put(c);
				}
			} else {
				if (c == DQUOTE) {
					if (i + 1 < length) {
						byte next = nextByte();
						if (next == DQUOTE) {
							_builder.put(c);
							_buffer.get();
						} else {
							escape = false;
						}
					} else {
						escape = false;
					}
				} else {
					_builder.put(c);
				}
			}
		}
		result = new String(_builder.array(), 0, _builder.position());
		return result;
	}

	@ToString
	class Field {
		int offset;
		int length;
	}

}
