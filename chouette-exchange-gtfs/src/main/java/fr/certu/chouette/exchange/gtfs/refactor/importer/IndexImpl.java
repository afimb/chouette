package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public abstract class IndexImpl<T> extends AbstractIndex<T>
{

   public static final byte[] UTF_8 = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

   protected String _path;
   protected String _key;
   protected Map<String, Integer> _fields;
   protected Map<String, Token> _tokens = new LinkedHashMap<String, Token>();

   private GtfsIterator _reader;
   private FileChannel _channel1;
   private MappedByteBuffer _buffer;
   private IntBuffer _index;
   private FileChannel _channel2;
   private File _temp;
   private int _total;
   private boolean _unique;

   private Map<Object, Context> _map = new HashMap<Object, Context>();

   public IndexImpl(String name, String id) throws IOException
   {
      this(name, id, true);
   }

   public IndexImpl(String name, String id, boolean unique) throws IOException
   {
      _path = name;
      _key = id;
      _unique = unique;
      initialize();
   }

   public String getPath()
   {
      return _path;
   }

   @Override
   protected void initialize() throws IOException
   {

      int offset = (hasBOM(_path)) ? 3 : 0;
      RandomAccessFile file = new RandomAccessFile(_path, "r");
      _channel1 = file.getChannel();

      _buffer = _channel1.map(FileChannel.MapMode.READ_ONLY, offset,
            _channel1.size());
      _buffer.load();
      _reader = new GtfsIteratorImpl(_buffer, 0);
      _reader.next();
      _fields = new HashMap<String, Integer>();
      for (int i = 0; i < _reader.getFieldCount(); i++)
      {
         String key = _reader.getValue(i);
         _fields.put(key, i);
      }

      index();
   }

   @Override
   public void dispose()
   {
      try
      {
         _reader.dispose();
         _channel1.close();
         _channel2.close();
         _temp.delete();

      } catch (IOException ignored)
      {
      }
   }

   @Override
   public Iterator<T> iterator()
   {
      return new Iterator<T>()
      {

         private Iterator<Token> _tokens = tokenIterator();
         private IndexIterator _iterator = null;
         private Context _context = createContext();

         @Override
         public boolean hasNext()
         {
            boolean result = false;
            if (_iterator != null && _iterator.hasNext())
            {
               result = true;
            } else if (_tokens.hasNext())
            {
               result = true;
            }
            return result;
         }

         @Override
         public T next()
         {
            T result = null;
            if (_iterator != null && _iterator.hasNext())
            {
               result = _iterator.next();
            } else if (_tokens.hasNext())
            {
               Token token = _tokens.next();
               ByteBuffer buffer = getBuffer(token, _context);
               _iterator = new IndexIterator(buffer, _context);
               result = _iterator.next();
            }
            return result;
         }

         @Override
         public void remove()
         {
            throw new UnsupportedOperationException();
         }

         private Context createContext()
         {
            Context context = new Context();
            context.put(Context.PATH, _path);
            return context;
         }

      };
   }

   @Override
   protected Iterator<Token> tokenIterator()
   {
      return _tokens.values().iterator();
   }

   @Override
   public Iterable<String> keys()
   {
      return _tokens.keySet();
   }

   @Override
   public Iterable<T> values(final String key)
   {
      return new Iterable<T>()
      {

         @Override
         public Iterator<T> iterator()
         {
            Context context = new Context();
            context.put(Context.PATH, _path);
            ByteBuffer buffer = getBuffer(key, context);
            return new IndexIterator(buffer, context);
         }
      };
   }

   @Override
   public boolean containsKey(String key)
   {
      return _tokens.containsKey(key);
   }

   @Override
   public T getValue(String key)
   {
      T result = null;
      Context context = new Context();
      context.put(Context.PATH, _path);
      ByteBuffer buffer = getBuffer(key, context);
      IndexIterator iterator = new IndexIterator(buffer, context);
      result = iterator.next();
      return result;
   }

   @Override
   public int getLength()
   {
      return _total;
   }

   @Override
   protected Map<String, Token> index() throws IOException
   {
      Monitor monitor = MonitorFactory.start();
      _total = 1;
      _reader.setPosition(0);
      _reader.next();

      while (_reader.hasNext())
      {
         _reader.next();

         String key = getField(_key);
         Token token = _tokens.get(key);

         if (token == null)
         {
            token = new Token();
            token.offset = 0;
            token.lenght = 1;
            _tokens.put(key, token);
         } else
         {
            if (_unique)
            {
               Context context = new Context();
               context.put(Context.PATH, _path);
               context.put(Context.ID, _total);
               context.put(Context.FIELD, _key);
               context.put(Context.ERROR, GtfsException.ERROR.DUPLICATE_FIELD);
               throw new GtfsException(context);
            }
            token.lenght++;
         }
         _total++;
      }

      String name = Paths.get(_path).getFileName().toString();
      _temp = File.createTempFile(name + ".", ".index");
      _temp.deleteOnExit();
      RandomAccessFile file = new RandomAccessFile(_temp, "rw");
      _channel2 = file.getChannel();
      _index = _channel2.map(FileChannel.MapMode.READ_WRITE, 0, _total * 8)
            .asIntBuffer();
      for (int i = 0; i < _total; i++)
      {
         _index.put(-1);
         _index.put(-1);
      }

      Token previous = null;
      for (String key : _tokens.keySet())
      {
         Token token = _tokens.get(key);
         if (previous != null)
         {
            token.offset = previous.offset + previous.lenght * 2;
         }
         previous = token;
      }

      _reader.setPosition(0);
      _reader.next();
      int position = _reader.getPosition();
      int line = 1;
      while (_reader.hasNext())
      {
         _reader.next();
         String key = getField(_key);
         Token token = _tokens.get(key);
         for (int i = 0; i < token.lenght; i++)
         {
            int n = token.offset + i * 2;
            if (_index.get(n) == -1)
            {
               _index.put(n, position);
               _index.put(n + 1, ++line);
               break;
            }
         }
         position = _reader.getPosition();
      }

      log.debug("[DSU] index " + _path + " " + _tokens.size() + " objects "
            + monitor.stop());
      return _tokens;
   }

   @Override
   protected ByteBuffer getBuffer(String key, Context context)
   {
      Token token = _tokens.get(key);
      if (token != null)
      {
         return getBuffer(token, context);
      } else
      {
         return ByteBuffer.allocate(0);
      }
   }

   @Override
   protected ByteBuffer getBuffer(Token token, Context context)
   {
      int offset = token.offset;
      int lenght = token.lenght;
      List<Integer> lines = new ArrayList<Integer>(lenght);
      List<ByteBuffer> list = new ArrayList<ByteBuffer>(lenght);
      for (int i = 0; i < lenght; i++)
      {
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
   protected Set<String> getFieldIds()
   {
      return _fields.keySet();
   }

   protected String getField()
   {
      return _reader.getValue();
   }

   protected String getField(String key)
   {
      return getField(_reader, key, "");
   }

   protected String getField(String key, String value)
   {
      return getField(_reader, key, value);
   }

   protected String getField(GtfsIterator reader, String key)
   {
      return getField(reader, key, "");
   }

   protected String getField(GtfsIterator reader, String key, String value)
   {
      Integer index = _fields.get(key);
      if (index == null)
      {
         return value;
      }
      String result = reader.getValue(index);
      if (result == null || result.isEmpty())
      {
         return value;
      }
      return result;
   }

   private ByteBuffer concat(ByteBuffer... buffers)
   {
      return concat(Arrays.asList(buffers));
   }

   private ByteBuffer concat(List<ByteBuffer> buffers)
   {
      int length = 0;
      for (ByteBuffer buffer : buffers)
      {
         buffer.rewind();
         length += buffer.remaining();
      }
      ByteBuffer result = ByteBuffer.allocate(length);

      for (ByteBuffer buffer : buffers)
      {
         buffer.rewind();
         result.put(buffer);
      }
      result.rewind();
      return result;
   }

   @Override
   public boolean validate(T bean, GtfsImporter dao)
   {
      return validate(bean, dao);
   }

   private boolean hasBOM(String path)
   {
      boolean result = true;
      FileChannel channel = null;
      try
      {
         RandomAccessFile file = new RandomAccessFile(path, "r");
         channel = file.getChannel();
         ByteBuffer buffer = ByteBuffer.allocate(UTF_8.length);
         channel.read(buffer);
         buffer.rewind();
         for (int i = 0; i < UTF_8.length; i++)
         {
            if (buffer.get() != UTF_8[i])
            {
               result = false;
               break;
            }
         }
      } catch (Exception ignored)
      {
      } finally
      {
         try
         {
            if (channel != null)
            {
               channel.close();
            }
         } catch (IOException ignored)
         {
         }
      }

      return result;
   }

   private class IndexIterator implements Iterator<T>
   {

      private GtfsIterator _iterator;
      private int _index;
      private List<Integer> _list;

      private Context _context;

      public IndexIterator(ByteBuffer buffer, Context context)
      {
         _iterator = new GtfsIteratorImpl(buffer, _fields.size());
         _index = 0;
         _context = context;
      }

      public void setByteBuffer(ByteBuffer buffer)
      {
         _iterator.setByteBuffer(buffer);
         _list = null;
         _index = 0;
      }

      @Override
      public T next()
      {
         T result = null;
         if (_iterator.hasNext())
         {
            _iterator.next();
            updateContext();
            // TODO [DSU] line
            result = build(_iterator, _context);
            _index++;
         }
         return result;
      }

      private void updateContext()
      {
         int result = -1;
         if (_list == null)
         {
            _list = (List<Integer>) _context.get(IDS);
         }
         result = _list.get(_index);
         _context.put(Context.ID, result);
      }

      @Override
      public boolean hasNext()
      {
         return _iterator.hasNext();
      }

      @Override
      public void remove()
      {
         throw new UnsupportedOperationException();
      }
   }
}
