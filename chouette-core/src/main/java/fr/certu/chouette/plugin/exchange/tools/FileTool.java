package fr.certu.chouette.plugin.exchange.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.log4j.Log4j;

@Log4j
public class FileTool
{
   private static final String[] charsets = { "US-ASCII", "UTF-8", "IBM437", "MacRoman" };

   public static Charset getZipCharset(String zipName) throws IOException
   {
      Charset encoding = Charset.defaultCharset();
      try (ZipFile zip = new ZipFile(zipName, encoding))
      {
         if (checkCharset(zip))
         {
            log.info(zipName + " is compatible with " + encoding.name() + " charset");
            return encoding;
         }
      }
      for (String charsetName : charsets)
      {
         encoding = Charset.forName(charsetName);
         try (ZipFile zip = new ZipFile(zipName, encoding))
         {
            if (checkCharset(zip))
            {
               log.info(zipName + " is compatible with " + encoding.name() + " charset");
               return encoding;
            }
         }
      }
      log.error(zipName + " is not compatible with any known charset");
      return null;
   }

   private static boolean checkCharset(ZipFile zip)
   {
      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
      {
         try
         {
            entries.nextElement();
         }
         catch (IllegalArgumentException e)
         {
            if (e.getMessage().startsWith("MALFORMED"))
            {
               // actual error with openjdk 7
               return false;
            }
            throw e;
         }
      }
      return true;
   }

   public static void uncompress(String zipFileName, File targetDirectory) throws IOException
   {
      File srcFile = new File(zipFileName);
      ZipFile zipFile = null;

      try
      {
         zipFile = new ZipFile(srcFile,getZipCharset(zipFileName));

         // get an enumeration of the ZIP file entries
         Enumeration<? extends ZipEntry> e = zipFile.entries();
         while (e.hasMoreElements())
         {
            ZipEntry entry = e.nextElement();
            File destinationPath = new File(targetDirectory, entry.getName());

            // create parent directories
            destinationPath.getParentFile().mkdirs();

            // if the entry is a file extract it
            if (entry.isDirectory())
            {
               continue;
            }
            else
            {
               BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

               int b;
               byte buffer[] = new byte[1024];

               FileOutputStream fos = new FileOutputStream(destinationPath);

               BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

               while ((b = bis.read(buffer, 0, 1024)) != -1)
               {
                  bos.write(buffer, 0, b);
               }

               bos.close();
               bis.close();

            }

         }

      }
      finally
      {
         try
         {
            if (zipFile != null)
            {
               zipFile.close();
            }
         }
         catch (IOException ioe)
         {
            log.error("Error while closing zip file" + ioe);
         }
      }

   }

}
