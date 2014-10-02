package fr.certu.chouette.plugin.exchange.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.log4j.Log4j;

@Log4j
public class FileTool
{
   private static final String[] charsets = { "US-ASCII", "UTF-8", "IBM437",
         "MacRoman" };

   public static Charset getZipCharset(String zipName) throws IOException
   {
      Charset encoding = Charset.defaultCharset();
      try (ZipFile zip = new ZipFile(zipName, encoding))
      {
         if (checkCharset(zip))
         {
            log.info(zipName + " is compatible with " + encoding.name()
                  + " charset");
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
               log.info(zipName + " is compatible with " + encoding.name()
                     + " charset");
               return encoding;
            }
         }
      }
      log.error(zipName + " is not compatible with any known charset");
      return null;
   }

   private static boolean checkCharset(ZipFile zip)
   {
      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries
            .hasMoreElements();)
      {
         try
         {
            entries.nextElement();
         } catch (IllegalArgumentException e)
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
}
