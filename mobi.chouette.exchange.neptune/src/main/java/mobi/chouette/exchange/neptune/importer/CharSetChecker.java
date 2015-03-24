package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;

import lombok.extern.log4j.Log4j;


@Log4j
public class CharSetChecker {
	
	   private static final int BOM_SIZE = 4;

	public static InputStreamReader getEncodedInputStreamReader(String fileName, InputStream in) throws IOException
	{
	      byte bom[] = new byte[BOM_SIZE];
	      String encoding;
	      int unread;
	      PushbackInputStream pushbackStream = new PushbackInputStream(in, 60);
	      int n = pushbackStream.read(bom, 0, bom.length);
	      // Read ahead four bytes and check for BOM marks.
	      if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
	            && (bom[2] == (byte) 0xBF))
	      {
	         encoding = "UTF-8";
	         unread = n - 3;
	      } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
	      {
	         encoding = "UTF-16BE";
	         unread = n - 2;
	      } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
	      {
	         encoding = "UTF-16LE";
	         unread = n - 2;
	      } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
	            && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF))
	      {
	         encoding = "UTF-32BE";
	         unread = n - 4;
	      } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
	            && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00))
	      {
	         encoding = "UTF-32LE";
	         unread = n - 4;
	      } else
	      {
	         pushbackStream.unread(bom, 0, n);
	         bom = new byte[60];
	         n = pushbackStream.read(bom, 0, bom.length);
	         byte[] array = new byte[n];
	         System.arraycopy(bom, 0, array, 0, n);
	         String header = new String(array);
	         encoding = getCharset(fileName, header);
	         unread = n;
	      }
	      // Unread bytes if necessary and skip BOM marks.
	      if (unread > 0)
	      {
	         pushbackStream.unread(bom, (n - unread), unread);
	      } else if (unread < -1)
	      {
	         pushbackStream.unread(bom, 0, 0);
	      }

	      // Use given encoding.
	      InputStreamReader reader;
	      log.info("file "+fileName+" : encoding = "+encoding);
	      if (encoding == null)
	      {
	         reader = new InputStreamReader(pushbackStream);
	      } else
	      {
	         reader = new InputStreamReader(pushbackStream, encoding);
	      }
		return reader;
	}
	
	   /**
	    * check and return specific charset <br>
	    * if default Neptune charset found : return null <br>
	    * if unknown charset found : throw ExchangeRuntimeException
	    * 
	    * @param contentName
	    *           name for log purpose
	    * @param contentXml
	    *           xml data to check
	    * @return
	    */
	   private static String getCharset(String filename, String contentXml)
	   {
	      int startIndex = contentXml.indexOf("encoding=");
	      if (startIndex == -1)
	      {
	         log.error("missing encoding for " + filename);
	         throw new RuntimeException("missing encoding for " + filename);
	      }
	      startIndex += 10;
	      int endIndex = contentXml.indexOf(contentXml.charAt(startIndex - 1),
	            startIndex);
	      if (endIndex <= 0)
	      {
	         log.error("empty encoding for " + filename);
	         throw new RuntimeException("empty encoding for " + filename);
	      }
	      String charsetName = contentXml.substring(startIndex, endIndex);
	      try
	      {
	         Charset.forName(charsetName);
	         return charsetName;
	      } catch (Exception e)
	      {
	         log.error("invalid encoding for " + filename + " : " + charsetName);
	         throw new RuntimeException("invalid encoding for " + filename + " : " + charsetName);
	      }

	   }


}
