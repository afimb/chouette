package fr.certu.chouette.struts;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Action to download a file
 *
 * @author Dryade
 */
public class DownloadFileAction extends GeneriqueAction
{

  private String fileName;
  private String directory;
  private InputStream inputStream;
  private static final Log logger = LogFactory.getLog(DownloadFileAction.class);

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
    if (previousAction.equals("MassiveExportAction"))
    {
      directory = "exports/";
    } else
    {
      directory = "";
    }
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
