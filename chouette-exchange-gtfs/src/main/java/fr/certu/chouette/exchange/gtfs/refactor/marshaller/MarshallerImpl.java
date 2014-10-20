package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class MarshallerImpl<T> implements Marshaller<T>
{

   private BufferedWriter _writer;

   public MarshallerImpl(String name) throws IOException
   {
      File file = new File(name);
      if (!file.exists())
      {
         file.createNewFile();
      }
      _writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
   }

   @Override
   public void marshal(String text) throws IOException
   {
      _writer.write(text);
   }

   @Override
   public void dispose() throws IOException
   {
      _writer.close();
   }

}
