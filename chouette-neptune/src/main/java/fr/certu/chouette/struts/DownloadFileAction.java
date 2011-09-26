package fr.certu.chouette.struts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Action to download a file
 *
 * @author Dryade
 */
public class DownloadFileAction extends GeneriqueAction
{


   private static final long serialVersionUID = 413791621361516790L;
   private String fileName;
   private String directory;
   private InputStream inputStream;
   private static final Logger logger = Logger.getLogger(DownloadFileAction.class);

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   public String getFileName()
   {
      return fileName;
   }

   public void setPreviousAction(String previousAction)
   {

      directory = "";

   }

   public InputStream getInputStream()
   {
      return inputStream;
   }

   public String downloadFile()
   {
      try
      {
         String file = directory + getFileName();
         inputStream = new FileInputStream(file);
      } catch (FileNotFoundException exception)
      {
         logger.error("file not found : " + exception);
      }

      return SUCCESS;
   }
}
