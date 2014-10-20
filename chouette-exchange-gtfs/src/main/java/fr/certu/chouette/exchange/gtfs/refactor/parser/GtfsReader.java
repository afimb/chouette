package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.ToString;

public class GtfsReader implements Iterator<Boolean>
{

   private ByteBuffer _buffer;
   private int _index = 0;
   private boolean _escape = false;
   private int _mark = 0;
   private List<Field> _fields = new ArrayList<Field>();
   private int _position;
   private byte[] bytes = new byte[1024];

   public GtfsReader(ByteBuffer buffer, int count)
   {
      super();
      _buffer = buffer;
      for (int i = 0; i < count; i++)
      {
         _fields.add(new Field());
      }
   }

   public void dispose()
   {
      _buffer.clear();
   }

   @Override
   public boolean hasNext()
   {
      return _buffer.hasRemaining() && (_mark < _buffer.limit());
   }

   @Override
   public Boolean next()
   {
      boolean result = false;
      try
      {
         _buffer.position(_mark);
         loop: while (_buffer.hasRemaining())
         {

            if (_index >= _fields.size())
            {
               _fields.add(new Field());
            }

            byte value = _buffer.get();
            switch (value)
            {
            case '\r':
            case '\n':
            {
               _fields.get(_index).offset = _mark;
               _fields.get(_index).lenght = _buffer.position() - 1
                     - _fields.get(_index).offset;
               if (value == '\r')
               {
                  _mark = _buffer.position() + 1;
               } else
               {
                  _mark = _buffer.position();
               }
               _index = 0;
               _position = _mark;
               result = true;
               break loop;
            }
            case ',':
            {
               if (!_escape)
               {
                  _fields.get(_index).offset = _mark;
                  _fields.get(_index).lenght = (_buffer.position() - 1 - _fields
                        .get(_index).offset);
                  _mark = _buffer.position();
                  _index++;

               }
               break;
            }
            case '"':
            {
               if (!_escape)
               {
                  _escape = true;
               } else
               {
                  int next = nextByte();
                  if (next == ',' || next == '\r' || next == '\n')
                  {
                     _escape = false;
                  }
               }
               break;
            }
            default:
               break;
            }
         }
      } catch (Exception ignored)
      {

      }

      return result;
   }

   @Override
   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   public int getFieldSize()
   {
      return _fields.size();
   }

   int getPosition()
   {
      return _position;
   }

   void setPosition(int position)
   {
      _buffer.position(position);
      _mark = position;
   }

   public String getValue(int index)
   {
      int lenght = _fields.get(index).lenght;
      int offset = _fields.get(index).offset;

      byte first = _buffer.get(offset);
      byte last = _buffer.get(offset + lenght - 1);

      if (first == '"' && last == '"')
      {
         _buffer.position(offset + 1);
         _buffer.get(bytes, 0, lenght - 2);
         return new String(bytes, 0, lenght - 2);
      } else
      {
         _buffer.position(offset);
         _buffer.get(bytes, 0, lenght);
         return new String(bytes, 0, lenght);
      }
   }

   public String getValue()
   {
      int offset = _fields.get(0).offset;
      int lenght = _position - offset;
      _buffer.position(offset);
      _buffer.get(bytes, 0, lenght);
      return new String(bytes, 0, lenght);
   }

   public ByteBuffer slice()
   {
      int offset = _fields.get(0).offset;
      int lenght = _position - offset;
      return slice(offset, lenght);
   }

   public ByteBuffer slice(int offset, int lenght)
   {
      _buffer.position(offset);
      ByteBuffer result = _buffer.slice();
      result.limit(lenght);
      return result;
   }

   private byte nextByte()
   {
      int position = _buffer.position();
      byte result = _buffer.get();
      _buffer.position(position);
      return result;
   }

   @ToString
   class Field
   {
      int offset;
      int lenght;
   }

}
