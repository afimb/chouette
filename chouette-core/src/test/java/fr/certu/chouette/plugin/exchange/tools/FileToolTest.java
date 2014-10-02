package fr.certu.chouette.plugin.exchange.tools;

import java.io.IOException;
import java.nio.charset.Charset;

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

}
