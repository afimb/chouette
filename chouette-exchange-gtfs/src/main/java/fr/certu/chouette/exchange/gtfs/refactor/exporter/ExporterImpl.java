package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ExporterImpl<T> implements Exporter<T>
{

   public static final char DELIMITER = ',';

   private BufferedWriter _writer;

   public ExporterImpl(String name) throws IOException
   {
      Path path = Paths.get(name);
      _writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
      writeHeader();
   }

   @Override
   public void write(String text) throws IOException
   {
      _writer.write(text);
   }

   @Override
   public void dispose() throws IOException
   {
      _writer.close();
   }

   void write(Enum[] values)
   {
      StringBuilder builder = new StringBuilder();
      final int length = values.length;
      for (int i = 0; i < length; i++)
      {
         Enum field = values[i];
         builder.append(field.name());
         if (i + 1 < length)
         {
            builder.append(DELIMITER);
         }
      }

   }

}
