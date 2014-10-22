package fr.certu.chouette.plugin.exchange.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
public class FileToolTest extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "plugin_tool" }, description = "should accept usascii encoding")
   public void verifyUSAscii() throws IOException
   {
      Charset encoding = FileTool.getZipCharset("src/test/data/default.zip");
      Assert.assertNotNull(encoding);
      Assert.assertEquals(encoding.name(), Charset.defaultCharset().name());
   }

   @Test(groups = { "plugin_tool" }, description = "should accept utf8 encoding")
   public void verifyUtf8() throws IOException
   {
      Charset encoding = FileTool.getZipCharset("src/test/data/utf8.zip");
      Assert.assertNotNull(encoding);
      Assert.assertEquals(encoding.name(), "UTF-8");
   }

   @Test(groups = { "plugin_tool" }, description = "should accept IBM437 encoding")
   public void verifyIBM437() throws IOException
   {
      Charset encoding = FileTool.getZipCharset("src/test/data/ibm437.zip");
      Assert.assertNotNull(encoding);
      Assert.assertEquals(encoding.name(), "IBM437");
   }

   @Test(groups = { "plugin_tool" }, description = "should accept MacRoman encoding")
   public void verifyMacRoman() throws IOException
   {
      Charset encoding = FileTool.getZipCharset("src/test/data/macroman.zip");
      Assert.assertNotNull(encoding);
      Assert.assertEquals(encoding.name(), "UTF-8");
   }
   
   @Test(groups = { "plugin_tool" }, description = "should uncompress zip file")
   public void verifUncompress() throws IOException
   {
      Path dir = Files.createTempDirectory("test_chouette");
      FileTool.uncompress("src/test/data/default.zip", dir.toFile());
      Collection<File> files = FileUtils.listFiles(dir.toFile(), null, false);
      Assert.assertEquals(files.size(),1,"directory must contain 1 file");
      Assert.assertEquals(files.toArray(new File[0])[0].getName(), "C_NEPTUNE_reseau_5_28102887.xml", "file name must match");
      FileUtils.deleteDirectory(dir.toFile());
   }
   

}
