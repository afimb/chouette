package fr.certu.chouette.export.metadata.writer;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;

import fr.certu.chouette.export.metadata.model.Metadata;


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

   public File writePlainFile(Metadata data, String directory) throws IOException,
         DatatypeConfigurationException
   {

      // Prepare the model for velocity
      getModel().put("data",data);
      getModel().put("formater", new TextFormater());

      return writePlainFile(directory+"/metadata_chouette.txt","templates/metadata_txt.vm");

   }


}
