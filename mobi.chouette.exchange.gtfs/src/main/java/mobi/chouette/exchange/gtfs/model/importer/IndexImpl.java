package mobi.chouette.exchange.gtfs.model.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
//import mobi.chouette.common.Color;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsObject;

//import com.jamonapi.Monitor;
//import com.jamonapi.MonitorFactory;

@Log4j
public abstract class IndexImpl<T> extends AbstractIndex<T> {

	public static final byte[] UTF_8 = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	protected String _path;
	protected String _key;
	protected String _value;
	protected Map<String, Integer> _fields;
	protected Map<String, Token> _tokens = new LinkedHashMap<String, Token>();

	private GtfsIterator _reader;
	private FileChannel _channel1;
	private MappedByteBuffer _buffer;
	private File _temp;
	private FileChannel _channel2;
	private IntBuffer _index;
	private int _total;
	private boolean _unique;

	public IndexImpl(String path, String key) throws IOException {
		this(path, key, "", true);
	}

	public IndexImpl(String path, String key, boolean unique) throws IOException {
		this(path, key, "", unique);
	}

	public IndexImpl(String path, String key, String value, boolean unique) throws IOException {
		_path = path;
		_key = key;
		_value = value;
		_unique = unique;
		_total = 0;

		initialize();
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	protected void initialize() throws IOException {
		boolean bom = hasBOM(_path);
		int offset = (bom) ? 3 : 0;
		//Monitor monitor = MonitorFactory.start("INITIALIZE INDEX IMPL : "+_path);
		RandomAccessFile file = new RandomAccessFile(_path, "r");
		try {
			_channel1 = file.getChannel();
			long length = (bom) ? _channel1.size() - 3 : _channel1.size();
			
			_buffer = _channel1.map(FileChannel.MapMode.READ_ONLY, offset, length);
			_buffer.load();
			_reader = new GtfsIteratorImpl(_buffer, 0);
			_total++;
			if (_reader.next()) { // The first line of this file is compatible GTFS-CSV 
				_fields = new HashMap<String, Integer>();
				for (int i = 0; i < _reader.getFieldCount(); i++) {
					String field = _reader.getValue(i); // Get the ith token 
					verify(field, i);
					_fields.put(field.trim(), i);
				}
				checkRequiredFields(_fields);
				index(); // read the rest of this file
			} else { // The header line doesn't comply with GTFS-CSV 
				throw new GtfsException(_path, _total, null,
						GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT, _reader.getCode(), null);
			}
		} finally {
			file.close();
			//log.info(Color.BLUE + monitor.stop() + Color.NORMAL);
		}
	}
	
	private void verify(String field, int i) throws GtfsException {
		if (field == null || field.trim().isEmpty()) { // key is empty
			throw new GtfsException(_path, _total, i, _key, GtfsException.ERROR.EMPTY_HEADER_FIELD, null, null);
		}
		
		if (_fields.get(field.trim()) != null) { // key already exists
			throw new GtfsException(_path, _total, i, _key, GtfsException.ERROR.DUPLICATE_HEADER_FIELD, null, null);
		}
	}
	
	protected abstract void checkRequiredFields(Map<String, Integer> fields);

	protected void testExtraSpace(String fieldName, String value, GtfsObject bean) {
		if (value != null && !value.equals(value.trim()) && withValidation) {
			bean.getErrors().add(new GtfsException(_path, bean.getId(), getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_SPACE_IN_FIELD, null, value));
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		try {
			_reader.dispose();
			_channel1.close();
			_channel2.close();
			_temp.delete();

		} catch (IOException ignored) {
		}
		_buffer.clear();
		_fields.clear();
		_tokens.clear();
		_index.clear();
		_reader = null;
		_channel1 = null;
		_channel2 = null;
		_temp = null;
		_buffer = null;
		_fields = null;
		_tokens = null;
		_index = null;
		
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private Iterator<Token> tokens = _tokens.values().iterator();
			private IndexIterator iterator = null;
			private Context context = createContext();

			@Override
			public boolean hasNext() {
				boolean result = false;
				if (iterator != null && iterator.hasNext()) {
					result = true;
				} else if (tokens.hasNext()) {
					result = true;
				}
				return result;
			}

			@Override
			public T next() {
				return next(true);
			}
			
			public T next(boolean withValidation) {
				T result = null;
				if (iterator != null && iterator.hasNext()) {
					result = iterator.next();
				} else if (tokens.hasNext()) {
					Token token = tokens.next();
					ByteBuffer buffer = getBuffer(token, context);
					iterator = new IndexIterator(buffer, context);
					result = iterator.next();
				}
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private Context createContext() {
				Context context = new Context();
				context.put(Context.PATH, _path);
				return context;
			}

		};
	}

	@Override
	public Iterable<String> keys() {
		return _tokens.keySet();
	}

	@Override
	public Iterable<T> values(final String key) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				Context context = new Context();
				context.put(Context.PATH, _path);
				ByteBuffer buffer = getBuffer(key, context);
				return new IndexIterator(buffer, context);
			}
		};
	}

	@Override
	public boolean containsKey(String key) {
		return _tokens.containsKey(key);
	}

	@Override
	public T getValue(String key) {
		T result = null;
		Context context = new Context();
		context.put(Context.PATH, _path);
		ByteBuffer buffer = getBuffer(key, context);
		IndexIterator iterator = new IndexIterator(buffer, context);
		result = iterator.next();
		return result;
	}

	@Override
	public int getLength() {
		return _total - 1;
	}

	@Override
	protected void index() throws IOException {
		//Monitor monitor = MonitorFactory.start("INDEX IMPL");
		boolean hasDefaultId = false;
		
		while (_reader.hasNext()) {
			_total++;
			
			if (hasDefaultId)
				throw new GtfsException(_path, _total, getIndex(_key), _key, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD, null, "");
			
			if (_reader.next()) {
				String key = getField(_key);
								
				if (key == null || key.trim().isEmpty()) { // key cannot be null! "" or GtfsAgency.DEFAULT_ID
					throw new GtfsException(_path, _total, getIndex(_key), _key, GtfsException.ERROR.MISSING_FIELD, null, null);
				}
				
				if (GtfsAgency.DEFAULT_ID.equals(key)) {
					if (_tokens.isEmpty()) {
						hasDefaultId = true;
					}
					else {
						throw new GtfsException(_path, _total, getIndex(_key), _key, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD, null, "");
					}
				}
				
				Token token = _tokens.get(key);
				if (token == null) {
					token = new Token();
					token.offset = 0;
					token.lenght = 1;
					_tokens.put(key, token);
				} else {
					if (_unique) {
						if (GtfsAgency.DEFAULT_ID.equals(key)) {
							throw new GtfsException(_path, _total, getIndex(_key), _key, GtfsException.ERROR.DUPLICATE_DEFAULT_KEY_FIELD, null, "");
						} else {
							throw new GtfsException(_path, _total, getIndex(_key), _key, GtfsException.ERROR.DUPLICATE_FIELD, null, key);
						}
					}
					token.lenght++;
				}
			} else {
				throw new GtfsException(_path, _total, _key, GtfsException.ERROR.INVALID_FILE_FORMAT, null, null);
			}
		}

		String name = Paths.get(_path).getFileName().toString();
		_temp = File.createTempFile(name + ".", ".index");
		_temp.deleteOnExit();
		RandomAccessFile file = new RandomAccessFile(_temp, "rw");
		_channel2 = file.getChannel();
		_index = _channel2.map(FileChannel.MapMode.READ_WRITE, 0, _total * 8).asIntBuffer();
		for (int i = 0; i < _total; i++) {
			_index.put(-1);
			_index.put(-1);
		}

		Token previous = null;
		for (String key : _tokens.keySet()) {
			Token token = _tokens.get(key);
			if (previous != null) {
				token.offset = previous.offset + previous.lenght * 2;
			}
			previous = token;
		}

		_reader.setPosition(0);
		_reader.next();
		int position = _reader.getPosition();
		int line = 1;
		while (_reader.hasNext()) {
			if (_reader.next()) {
				String key = getField(_key);				
				Token token = _tokens.get(key);
				for (int i = 0; i < token.lenght; i++) {
					int n = token.offset + i * 2;
					if (_index.get(n) == -1) {
						_index.put(n, position);
						_index.put(n + 1, ++line);
						break;
					}
				}
				position = _reader.getPosition();
			}
		}
		
		//log.info(Color.YELLOW + "[DSU] index " + _path + " " + _tokens.size() + " objects " + monitor.stop() + Color.NORMAL);
		//log.debug("[DSU] index " + _path + " " + _tokens.size() + " objects " + monitor.stop());
		file.close();
	}

	// @Override
	protected ByteBuffer getBuffer(String key, Context context) {
		Token token = _tokens.get(key);
		if (token != null) {
			return getBuffer(token, context);
		} else {
			return ByteBuffer.allocate(0);
		}
	}

	// @Override
	protected ByteBuffer getBuffer(Token token, Context context) {
		int offset = token.offset;
		int lenght = token.lenght;
		List<Integer> lines = new ArrayList<Integer>(lenght);
		List<ByteBuffer> list = new ArrayList<ByteBuffer>(lenght);
		for (int i = 0; i < lenght; i++) {
			int n = offset + i * 2;
			int position = _index.get(n);
			// TODO [DSU] line
			int line = _index.get(n + 1);
			lines.add(line);
			_reader.setPosition(position);
			ByteBuffer value = _reader.getBuffer();
			list.add(value);
		}
		context.put(IDS, lines);
		return concat(list);
	}

	@Override
	protected Set<String> getFieldIds() {
		return _fields.keySet();
	}

	protected String getField() {
		return _reader.getValue();
	}

	protected String getField(String key) {
		return getField(_reader, key, _value);
	}

	protected String getField(String key, String value) {
		return getField(_reader, key, value);
	}

	protected String getField(GtfsIterator reader, String key) {
		return getField(reader, key, "");
	}
	
	public Integer getIndex(String key) {
		if (_fields.get(key) == null)
			return new Integer(-1);
		return new Integer(_fields.get(key).intValue()+1);
	}

	protected String getField(GtfsIterator reader, String key, String value) {
		Integer index = _fields.get(key);
		if (index == null) {
			return value;
		}
		String result = reader.getValue(index);
		if (result == null || result.isEmpty()) {
			return value;
		}
		return result;
	}

	private ByteBuffer concat(List<ByteBuffer> buffers) {
		int length = 0;
		for (ByteBuffer buffer : buffers) {
			buffer.rewind();
			length += buffer.remaining();
		}
		ByteBuffer result = ByteBuffer.allocate(length);

		for (ByteBuffer buffer : buffers) {
			buffer.rewind();
			result.put(buffer);
		}
		result.rewind();
		return result;
	}

	@Override
	public boolean validate(T bean, GtfsImporter dao) {
		return validate(bean, dao);
	}

	private boolean hasBOM(String path) {
		boolean result = true;
		FileChannel channel = null;
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(path, "r");
			channel = file.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(UTF_8.length);
			channel.read(buffer);
			buffer.rewind();
			for (int i = 0; i < UTF_8.length; i++) {
				if (buffer.get() != UTF_8[i]) {
					result = false;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			log.error("File "+_path+" not found", e);
			throw new GtfsException(_path, 0, null, GtfsException.ERROR.MISSING_FILE, null, null, null, e);
		} catch (Exception e) {
			log.error("A system problem occurs while reading file "+_path, e);
			throw new GtfsException(_path, 0, null, GtfsException.ERROR.SYSTEM, null, null, null, e);
		} finally {
			try {
				if (channel != null) {
					channel.close();
				}
			} catch (IOException ignored) {
				channel = null;
			}
			try {
				if (file != null) {
					file.close();
				}
			} catch (IOException ignored) {
				file = null;
			}
		}
		return result;
	}
	
	protected boolean isEmpty(String value)
	{
		return value == null || value.trim().length() == 0;
	}
	
	protected boolean isPresent(String value)
	{
		return !isEmpty(value);
	}
	
	

	private class IndexIterator implements Iterator<T> {

		private GtfsIterator iterator;
		private int index;
		private List<Integer> list;

		private Context context;

		public IndexIterator(ByteBuffer buffer, Context context) {
			this.iterator = new GtfsIteratorImpl(buffer, _fields.size());
			this.index = 0;
			this.context = context;
		}

		@Override
		public T next() {
			T result = null;
			if (iterator.hasNext()) {
				iterator.next();
				updateContext();
				// TODO [DSU] line
				result = build(iterator, context);
				index++;
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		private void updateContext() {
			int result = -1;
			if (list == null) {
				list = (List<Integer>) context.get(IDS);
			}
			result = list.get(index);
			context.put(Context.ID, result);
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
