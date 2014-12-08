package fr.certu.chouette.exchange.gtfs.export.producer;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsDummyProducer;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Context;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsExportAbstractProducerTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsExportAbstractProducerTests.class);

   private GtfsExporterMock mock = new GtfsExporterMock();
   private Context context = new Context();
   private GtfsDummyProducer producer = new GtfsDummyProducer(mock);

   @Test(groups = { "Producers" }, description = "test id conversion")
   public void verifyToGtfsId() throws ChouetteException
   {

      Assert.assertEquals(producer.toGtfsIdWrapper("GTFS:Type:1234", "GTFS"), "1234", "gtfs id must contains only third part of neptune id");
      Assert.assertEquals(producer.toGtfsIdWrapper("GTFS:Type:1234", "TEST"), "GTFS.1234",
            "gtfs id must contains combination of first and third parts of neptune id");

   }

   @Test(groups = { "Producers" }, description = "test color conversion")
   public void verifyGetColor() throws ChouetteException
   {
      Color c = producer.getColorWrapper("AABBCC");
      Assert.assertEquals(c.getRed(), Integer.parseInt("AA", 16), " color must have correct red component");
      Assert.assertEquals(c.getGreen(), Integer.parseInt("BB", 16), " color must have correct green component");
      Assert.assertEquals(c.getBlue(), Integer.parseInt("CC", 16), " color must have correct blue component");

      Assert.assertNull(producer.getColorWrapper(null), "no color returned on null pointer");

   }

   @Test(groups = { "Producers" }, description = "test Url conversion")
   public void verifyGetUrl() throws ChouetteException
   {
      URL url = producer.getUrlWrapper("http://www.cityway.fr");
      Assert.assertEquals(url.toString(), "http://www.cityway.fr", "Url must be correcty set");
      Assert.assertNull(producer.getUrlWrapper(null), "no url returned on null pointer");
      url = producer.getUrlWrapper("ftp://www.cityway.fr");
      Assert.assertNull(url, "url should be null on wrong protocol");

   }

   @Test(groups = { "Producers" }, description = "test String conversion")
   public void verifyGetValue() throws ChouetteException
   {
      Assert.assertEquals(producer.getValueWrapper("toto"), "toto", "value must be correct string");
      Assert.assertNull(producer.getValueWrapper(null), "no string returned on null pointer");
      Assert.assertNull(producer.getValueWrapper("   "), "no string returned on blank string");
   }

   @Test(groups = { "Producers" }, description = "test empty check on collection")
   public void verifyIsEmptyCollection() throws ChouetteException
   {
      List<String> list = null;
      Assert.assertTrue(producer.isEmptyWrapper(list), "null list must be empty");
      list = new ArrayList<>();
      Assert.assertTrue(producer.isEmptyWrapper(list), "empty list must be empty");
      list.add("toto");
      Assert.assertFalse(producer.isEmptyWrapper(list), "non empty list must not be empty");
   }

   @Test(groups = { "Producers" }, description = "test empty check on string")
   public void verifyIsEmptyString() throws ChouetteException
   {
      String value = null;
      Assert.assertTrue(producer.isEmptyWrapper(value), "null string must be empty");
      value = "";
      Assert.assertTrue(producer.isEmptyWrapper(value), "empty string must be empty");
      value = "  ";
      Assert.assertTrue(producer.isEmptyWrapper(value), "blank string must be empty");
      value = " toto ";
      Assert.assertFalse(producer.isEmptyWrapper(value), "non blank string must not be empty");
   }

}
