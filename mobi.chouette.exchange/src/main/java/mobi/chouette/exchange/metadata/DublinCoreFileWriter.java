package mobi.chouette.exchange.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;


public class DublinCoreFileWriter extends TemplateFileWriter
{


   public DublinCoreFileWriter()
   {
   }


   public ZipEntry writeZipEntry(Metadata data,
         ZipOutputStream zipFile) throws IOException,
         DatatypeConfigurationException
   {
      // Prepare the model for velocity
      getModel().put("data",data);
      getModel().put("formater", new DublinCoreFormater());
      
      return writeZipEntry("metadata_chouette_dc.xml","templates/metadata_dc.vm", zipFile);
   }

   public File writePlainFile(Metadata data, Path target) throws IOException,
         DatatypeConfigurationException
   {

      // Prepare the model for velocity
      getModel().put("data",data);
      getModel().put("formater", new DublinCoreFormater());
     

      return writePlainFile(target.toFile(),"metadata_chouette_dc.xml","templates/metadata_dc.vm");

   }


}
