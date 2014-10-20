package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
public abstract class ParserImpl<T> extends AbstractParser<T>
{

   protected String _name;
   protected String _id;
   protected Map<String, Integer> _fields;
   protected Map<String, Token> _tokens = new LinkedHashMap<String, Token>();

   private GtfsReader _reader;
   private FileChannel _channel1;
   private MappedByteBuffer _buffer;
   private IntBuffer _index;
   private FileChannel _channel2;
   private File _temp;

   // public static final ThreadLocal<Context> local = new
   // ThreadLocal<Context>();
   private static final String LINES = "lines";

   public ParserImpl(String name, String id) throws IOException
   {
      _name = name;
      _id = id;
      initialize();
   }

   @Override
   protected void initialize() throws IOException
   {
      RandomAccessFile file = new RandomAccessFile(_name, "r");
      _channel1 = file.getChannel();
      _buffer = _channel1.map(FileChannel.MapMode.READ_ONLY, 0,
            _channel1.size());
      _buffer.load();

      _reader = new GtfsReader(_buffer, 0);
      _reader.next();
      _fields = new HashMap<String, Integer>();
      for (int i = 0; i < _reader.getFieldSize(); i++)
      {
         _fields.put(_reader.getValue(i), i);
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
         private GTFSIterator _iterator = null;

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
               ByteBuffer buffer = getBuffer(token);
               _iterator = new GTFSIterator(buffer);
               result = _iterator.next();
            }
            return result;
         }

         @Override
         public void remove()
         {
            throw new UnsupportedOperationException();
         }

      };
   }

   @Override
   protected Iterator<Token> tokenIterator()
   {
      return _tokens.values().iterator();
   }

   @Override
   public Iterator<String> keyIterator()
   {
      return _tokens.keySet().iterator();
   }

   @Override
   public Iterator<T> valuesIterator(String key)
   {
      ByteBuffer buffer = getBuffer(key);
      return new GTFSIterator(buffer);
   }

   @Override
   public boolean containsKey(String key)
   {
      return _tokens.containsKey(key);
   }

   @Override
   public T getValue(String key)
   {
      return valuesIterator(key).next();
   }

   @Override
   protected Map<String, Token> index() throws IOException
   {
      Monitor monitor = MonitorFactory.start();
      int total = 1;
      _reader.setPosition(0);
      _reader.next();

      while (_reader.hasNext())
      {
         _reader.next();

         String key = getField(_id);
         Token token = _tokens.get(key);
         if (token == null)
         {
            token = new Token();
            token.offset = 0;
            token.lenght = 1;
            _tokens.put(key, token);
         } else
         {
            token.lenght++;
         }
         total++;
      }

      String name = Paths.get(_name).getFileName().toString();
      _temp = File.createTempFile(name + "", ".index");
      _temp.deleteOnExit();
      RandomAccessFile file = new RandomAccessFile(_temp, "rw");
      _channel2 = file.getChannel();
      _index = _channel2.map(FileChannel.MapMode.READ_WRITE, 0, total * 8)
            .asIntBuffer();
      for (int i = 0; i < total; i++)
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
         String key = getField(_id);
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

      log.debug("[DSU] index " + _name + " " + _tokens.size() + " objects "
            + monitor.stop());
      return _tokens;
   }

   @Override
   protected ByteBuffer getBuffer(String key)
   {
      Token token = _tokens.get(key);
      return getBuffer(token);
   }

   @Override
   protected ByteBuffer getBuffer(Token token)
   {
      int offset = token.offset;
      int lenght = token.lenght;
      List<Integer> lines = new ArrayList<Integer>(lenght);
      List<ByteBuffer> list = new ArrayList<ByteBuffer>(lenght);
      for (int i = 0; i < lenght; i++)
      {
         int n = offset + i * 2;
         int index = _index.get(n);
         int line = _index.get(n + 1);
         lines.add(line);
         _reader.setPosition(index);
         _reader.next();
         ByteBuffer value = _reader.slice();
         list.add(value);
      }
      Context context = new Context();
      context.put(LINES, lines);
      set(context);
      return concat(list);
   }

   @Override
   protected Set<String> getFieldIds()
   {
      return _fields.keySet();
   }

   protected String getField(String key)
   {
      return getField(_reader, key, "");
   }

   protected String getField(String key, String value)
   {
      return getField(_reader, key, value);
   }

   protected String getField(GtfsReader reader, String key)
   {
      return getField(reader, key, "");
   }

   protected String getField(GtfsReader reader, String key, String value)
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

   class GTFSIterator implements Iterator<T>
   {

      private GtfsReader _reader;
      private int _index;
      private List<Integer> _lines;

      public GTFSIterator(ByteBuffer buffer)
      {
         _reader = new GtfsReader(buffer, _fields.size());
         _index = 0;
      }

      @Override
      public T next()
      {
         T result = null;
         if (_reader.hasNext())
         {
            _reader.next();
            result = build(_reader, getLineNumber());
            _index++;
         }
         return result;
      }

      private int getLineNumber()
      {
         if (_lines == null)
         {
            Context context = get();
            _lines = (List<Integer>) context.get(LINES);
         }
         return _lines.get(_index);
      }

      @Override
      public boolean hasNext()
      {
         return _reader.hasNext();
      }

      @Override
      public void remove()
      {
         throw new UnsupportedOperationException();
      }
   }
}
