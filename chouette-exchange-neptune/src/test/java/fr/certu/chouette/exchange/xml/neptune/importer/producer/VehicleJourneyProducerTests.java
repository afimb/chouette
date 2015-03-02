package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.model.neptune.Footnote;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.VehicleJourney;

@ContextConfiguration(locations = { "classpath:testContext.xml",
"classpath*:chouetteContext.xml" })
public class VehicleJourneyProducerTests extends AbstractTestNGSpringContextTests
{

   private VehicleJourneyProducer producer = new VehicleJourneyProducer(); 
   
   private Line line = null;
   
   private Footnote buildFootnote(String key, Line line)
   {
      Footnote note = new Footnote();
      note.setKey(key);
      note.setCode("code"+key);
      note.setLabel("label"+key);
      note.setLine(line);
      return note;
   }
   private Line getLine()
   {
      if (line != null) return line;
      line = new Line();
     
      line.getFootnotes().add(buildFootnote("1", line));
      line.getFootnotes().add(buildFootnote("2", line));
      line.getFootnotes().add(buildFootnote("3", line));
      line.getFootnotes().add(buildFootnote("4", line));
      
      return line;
   }

   @Test(groups = { "parseComment" }, description = "check empty comment and extensions")
   public void verifyBuildEmptyComment() throws ChouetteException
   {
      String xmlComment = null;
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertNull(vj.getComment(),"comment should be null");
      Assert.assertNull(vj.getFlexibleService(),"flexibleService should be null");
      Assert.assertNull(vj.getMobilityRestrictedSuitability(),"mobility should be null");
      Assert.assertEquals(vj.getFootnotes().size(), 0,"footnotes should be empty");
      
   }

   @Test(groups = { "parseComment" }, description = "check normal comment without extensions")
   public void verifyBuildNormalComment() throws ChouetteException
   {
      String xmlComment = "dummy text";
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertEquals(vj.getComment(),"dummy text","comment should be filled");
      Assert.assertNull(vj.getFlexibleService(),"flexibleService should be null");
      Assert.assertNull(vj.getMobilityRestrictedSuitability(),"mobility should be null");
      Assert.assertEquals(vj.getFootnotes().size(), 0,"footnotes should be empty");
   }

   @Test(groups = { "parseComment" }, description = "check null comment with flexible service extension")
   public void verifyBuildFlexibleServiceComment() throws ChouetteException
   {
      JSONObject jsonComment = new JSONObject();
      jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
      String xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertNull(vj.getComment(),"comment should be null");
      Assert.assertNull(vj.getMobilityRestrictedSuitability(),"mobility should be null");
      Assert.assertEquals(vj.getFootnotes().size(), 0,"footnotes should be empty");
      Assert.assertEquals(vj.getFlexibleService(),Boolean.TRUE,"flexibleService should be true");

      jsonComment = new JSONObject();
      jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.FALSE);
      xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertEquals(vj.getFlexibleService(),Boolean.FALSE,"flexibleService should be false");

   }
   @Test(groups = { "parseComment" }, description = "check null comment with mobility restricted suitability  extension")
   public void verifyBuildMobilityComment() throws ChouetteException
   {
      JSONObject jsonComment = new JSONObject();
      jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.TRUE);
      String xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertNull(vj.getComment(),"comment should be null");
      Assert.assertNull(vj.getFlexibleService(),"flexibleService should be null");
      Assert.assertEquals(vj.getFootnotes().size(), 0,"footnotes should be empty");
      Assert.assertEquals(vj.getMobilityRestrictedSuitability(),Boolean.TRUE,"mobility should be true");

      jsonComment = new JSONObject();
      jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.FALSE);
      xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertEquals(vj.getMobilityRestrictedSuitability(),Boolean.FALSE,"mobility should be false");

   }

   @Test(groups = { "parseComment" }, description = "check null comment and footnotes extensions")
   public void verifyBuildFootnotesComment() throws ChouetteException
   {
      JSONObject jsonComment = new JSONObject();
      JSONArray jsonFootnotes = new JSONArray();
      jsonComment.put(JsonExtension.FOOTNOTE_REFS, jsonFootnotes);
      
      jsonFootnotes.put("1");
      
      String xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertNull(vj.getComment(),"comment should be null");
      Assert.assertNull(vj.getFlexibleService(),"flexibleService should be null");
      Assert.assertNull(vj.getMobilityRestrictedSuitability(),"mobility should be null");
      Assert.assertEquals(vj.getFootnotes().size(), 1,"footnotes should be filled");
      Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1","note key should be filled");

      jsonFootnotes.put("3");
      
      xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertEquals(vj.getFootnotes().size(), 2,"footnotes should be filled");
      Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1","note key should be filled");
      Assert.assertEquals(vj.getFootnotes().get(1).getKey(), "3","note key should be filled");
   }
   
   @Test(groups = { "parseComment" }, description = "check  comment with all extension")
   public void verifyBuildCompleteComment() throws ChouetteException
   {
      JSONObject jsonComment = new JSONObject();
      jsonComment.put(JsonExtension.FLEXIBLE_SERVICE, Boolean.TRUE);
      JSONArray jsonFootnotes = new JSONArray();
      jsonComment.put(JsonExtension.FOOTNOTE_REFS, jsonFootnotes);
      jsonFootnotes.put("1");
      jsonFootnotes.put("3");
      jsonComment.put(JsonExtension.MOBILITY_RESTRICTION, Boolean.TRUE);
      jsonComment.put(JsonExtension.COMMENT, "dummy text");

      String xmlComment = jsonComment.toString();
      Reporter.log("comment = "+xmlComment);
      VehicleJourney vj = new VehicleJourney();

      producer.parseComment(xmlComment, vj, getLine());
      Assert.assertEquals(vj.getComment(),"dummy text","comment should be filled");
      Assert.assertEquals(vj.getFlexibleService(),Boolean.TRUE,"flexibleService should be true");
      Assert.assertEquals(vj.getMobilityRestrictedSuitability(),Boolean.TRUE,"mobility should be true");
      Assert.assertEquals(vj.getFootnotes().size(), 2,"footnotes should be filled");
      Assert.assertEquals(vj.getFootnotes().get(0).getKey(), "1","note key should be filled");
      Assert.assertEquals(vj.getFootnotes().get(1).getKey(), "3","note key should be filled");

   }

}
