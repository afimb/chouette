package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.ToString;

public abstract class AbstractImporter<T> implements Importer<T>
{

   public static final ThreadLocal<Context> LOCAL = new ThreadLocal<Context>();

   protected abstract void initialize() throws IOException;

   protected abstract Set<String> getFieldIds();

   protected abstract Map<String, Token> index() throws IOException;

   protected abstract ByteBuffer getBuffer(String id);

   protected abstract ByteBuffer getBuffer(Token id);

   protected abstract Iterator<Token> tokenIterator();

   protected abstract T build(GtfsIterator reader, int id);

   public static void set(Context context)
   {
      LOCAL.set(context);
   }

   public static void unset()
   {
      LOCAL.remove();
   }

   public static Context get()
   {
      return LOCAL.get();
   }

   @ToString
   class Token
   {
      int offset = -1;
      int lenght = 0;
   }

   static class Context extends HashMap<String, Object>
   {
      private static final long serialVersionUID = 1L;
   }
}
