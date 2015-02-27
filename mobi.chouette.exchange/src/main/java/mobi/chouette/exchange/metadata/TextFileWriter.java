package mobi.chouette.exchange.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;


public class TextFileWriter extends TemplateFileWriter
{


   public TextFileWriter()
   {
   }


   public ZipEntry writeZipEntry(Metadata data,
         ZipOutputStream zipFile) throws IOException,
         DatatypeConfigurationException
   {
      // Prepare the model for velocity
      getModel().put("data",data);
      getModel().put("formater", new TextFormater());
      
      return writeZipEntry("metadata_chouette.txt","templates/metadata_txt.vm", zipFile);
   }

   public File writePlainFile(Metadata data, Path directory) throws IOException,
         DatatypeConfigurationException
   {

      // Prepare the model for velocity
      getModel().put("data",data);
      getModel().put("formater", new TextFormater());

      return writePlainFile(directory.toFile(),"metadata_chouette.txt","templates/metadata_txt.vm");

   }


}
