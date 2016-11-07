package mobi.chouette.exchange.gtfs.exporter.producer;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsDummyProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsExportAbstractProducerTests 
{

   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsDummyProducer producer = new GtfsDummyProducer(mock);

   @Test(groups = { "Producers" }, description = "test id conversion")
   public void verifyToGtfsId() throws ChouetteException
   {

      Assert.assertEquals(producer.toGtfsIdWrapper("GTFS:Type:1234", "GTFS",false), "1234", "gtfs id must contains only third part of neptune id");
      Assert.assertEquals(producer.toGtfsIdWrapper("GTFS:Type:1234", "TEST",false), "GTFS.1234",
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
