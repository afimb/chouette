package fr.certu.chouette.model;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
public class LineTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "model" }, description = "compareAttribute should answer false or true")
   public void verifyCompareAttribute()
   {
      Line line1 = new Line();
      Line line2 = new Line();

      Assert.assertTrue(line1.compareAttributes(line2),
            "both empty should be true");
      line1.setObjectId("NINOXE:Line:1");
      Assert.assertFalse(line1.compareAttributes(line2),
            "objectId null vs value should be false");
      line2.setObjectId("NINOXE:Line:2");
      Assert.assertFalse(line1.compareAttributes(line2),
            "objectId value1 vs value2 should be false");
      line2.setObjectId("NINOXE:Line:1");
      Assert.assertTrue(line1.compareAttributes(line2),
            "objectId value1 vs value1 should be true");

      line1.setObjectVersion(1);
      line2.setObjectVersion(2);
      Assert.assertFalse(line1.compareAttributes(line2),
            "objectVersion value1 vs value2 should be false");
      line2.setObjectVersion(1);
      Assert.assertTrue(line1.compareAttributes(line2),
            "objectVersion value1 vs value1 should be true");

      line1.setName("Line 1");
      Assert.assertFalse(line1.compareAttributes(line2),
            "name null vs value should be false");
      line2.setName("Line 2");
      Assert.assertFalse(line1.compareAttributes(line2),
            "name value1 vs value2 should be false");
      line2.setName("Line 1");
      Assert.assertTrue(line1.compareAttributes(line2),
            "name value1 vs value1 should be true");

      line1.setComment("Line 1");
      Assert.assertFalse(line1.compareAttributes(line2),
            "comment null vs value should be false");
      line2.setComment("Line 2");
      Assert.assertFalse(line1.compareAttributes(line2),
            "comment value1 vs value2 should be false");
      line2.setComment("Line 1");
      Assert.assertTrue(line1.compareAttributes(line2),
            "comment value1 vs value1 should be true");

      line1.setIntUserNeeds(new Integer(1));
      Assert.assertFalse(line1.compareAttributes(line2),
            "IntUserNeeds null vs value should be false");
      line2.setIntUserNeeds(new Integer(2));
      Assert.assertFalse(line1.compareAttributes(line2),
            "IntUserNeeds value1 vs value2 should be false");
      line2.setIntUserNeeds(new Integer(1));
      Assert.assertTrue(line1.compareAttributes(line2),
            "IntUserNeeds value1 vs value1 should be true");

   }
   
   @Test(groups = { "model" }, description = "check maximum size of fields")
   public void verifyFieldTruncating()
   {
      Line obj = new Line();
      obj.setName(null);
      Assert.assertNull(obj.getName(),"name should be null");
      obj.setName("short name");
      Assert.assertEquals(obj.getName(),"short name", "name should be preserved");
      String longString = "long string with more than 256 chrs";
      while (longString.length() < 256) longString += "0123456789";
      obj.setObjectId("toto:Line:"+longString);
      Assert.assertEquals(obj.getObjectId().length(),255, "objectId should be truncated");
      obj.setName(longString);
      Assert.assertEquals(obj.getName().length(),255, "name should be truncated");
      obj.setRegistrationNumber(longString);
      Assert.assertEquals(obj.getRegistrationNumber().length(),255, "registration number should be truncated");
      obj.setComment(longString);
      Assert.assertEquals(obj.getComment().length(),255, "comment should be truncated");
      obj.setUrl(longString);
      Assert.assertEquals(obj.getUrl().length(),255, "url should be truncated");
      obj.setColor("1234567");
      Assert.assertEquals(obj.getColor().length(),6, "color should be truncated");
      obj.setTextColor("1234567");
      Assert.assertEquals(obj.getTextColor().length(),6, "text color should be truncated");
      obj.setObjectId("toto:Line:"+longString);
      Assert.assertEquals(obj.getObjectId().length(),255, "objectId should be truncated");
   }

}
