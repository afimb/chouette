package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class GtfsIterator2 implements Iterator<String>
{
   private ByteBuffer _buffer;

   public GtfsIterator2(ByteBuffer buffer)
   {
      _buffer = buffer;
   }

   public void dispose()
   {
      _buffer.clear();
   }

   @Override
   public boolean hasNext()
   {
      return _buffer.hasRemaining();
   }

   @Override
   public String next()
   {
      StringBuffer result = new StringBuffer();
      int c = -1;
      boolean eol = false;

      while (!eol)
      {
         switch (c = _buffer.get())
         {
         case '\n':
            eol = true;
            break;
         case '\r':
            eol = true;
            _buffer.mark();
            if ((_buffer.get()) != '\n')
            {
               _buffer.reset();
            }
            break;
         default:
            result.append((char) c);
            break;
         }
      }

      if ((c == -1) && (result.length() == 0))
      {
         return null;
      }
      return result.toString();
   }

   @Override
   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   int getPosition()
   {
      return _buffer.position();
   }

   void setPosition(int position)
   {
      _buffer.position(position);
   }

   public ByteBuffer slice(int offset, int lenght)
   {
      _buffer.position(offset);
      ByteBuffer result = _buffer.slice();
      result.limit(lenght);
      return result;
   }

}
