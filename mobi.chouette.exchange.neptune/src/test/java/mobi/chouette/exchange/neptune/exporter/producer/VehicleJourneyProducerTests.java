package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Line;
import mobi.chouette.model.VehicleJourney;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class VehicleJourneyProducerTests 
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

   @Test(groups = { "buildComment" }, description = "check empty comment and extensions")
   public void verifyBuildEmptyComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      String xmlComment = producer.buildComment(vj,true);
      Assert.assertNull(xmlComment,"comment should be null");

   }

   @Test(groups = { "buildComment" }, description = "check normal comment without extensions")
   public void verifyBuildNormalComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      vj.setComment("dummy comment");

      String xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"dummy comment","comment should be correctly built");
   }

   @Test(groups = { "buildComment" }, description = "check null comment with flexible service extension")
   public void verifyBuildFlexibleServiceComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      vj.setFlexibleService(Boolean.TRUE);

      String xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"flexible_service\":true}","comment should be correctly built");

      vj.setFlexibleService(Boolean.FALSE);

      xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"flexible_service\":false}","comment should be correctly built");

   }
   @Test(groups = { "buildComment" }, description = "check null comment with mobility restricted suitability  extension")
   public void verifyBuildMobilityComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      vj.setMobilityRestrictedSuitability(Boolean.TRUE);

      String xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"mobility_restriction\":true}","comment should be correctly built");

      vj.setMobilityRestrictedSuitability(Boolean.FALSE);

      xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"mobility_restriction\":false}","comment should be correctly built");

   }

   @Test(groups = { "buildComment" }, description = "check null comment and footnotes extensions")
   public void verifyBuildFootnotesComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      
      vj.getFootnotes().add(getLine().getFootnotes().get(0));
      vj.getFootnotes().add(getLine().getFootnotes().get(2));

      String xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"footnote_refs\":[\"1\",\"3\"]}","comment should be correctly built");

   }
   
   @Test(groups = { "buildComment" }, description = "check  comment with all extension")
   public void verifyBuildCompleteComment() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      vj.getFootnotes().add(getLine().getFootnotes().get(0));
      vj.getFootnotes().add(getLine().getFootnotes().get(2));
      vj.setMobilityRestrictedSuitability(Boolean.TRUE);
      vj.setFlexibleService(Boolean.TRUE);
      vj.setComment("dummy comment");
      String xmlComment = producer.buildComment(vj,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertTrue(xmlComment.startsWith("{"),"comment should start with {");
      Assert.assertTrue(xmlComment.endsWith("}"),"comment should end with }");
      Assert.assertTrue(xmlComment.contains(JsonExtension.COMMENT),"comment should contain comment tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.FOOTNOTE_REFS),"comment should contain footnote refs tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.FLEXIBLE_SERVICE),"comment should contain flexible service tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.MOBILITY_RESTRICTION),"comment should contain mobility restriction tag");

   }

   @Test(groups = { "buildComment" }, description = "check normal comment without extensions asked")
   public void verifyBuildNoExtensions() throws ChouetteException
   {
      VehicleJourney vj = new VehicleJourney();
      vj.getFootnotes().add(getLine().getFootnotes().get(0));
      vj.getFootnotes().add(getLine().getFootnotes().get(2));
      vj.setMobilityRestrictedSuitability(Boolean.TRUE);
      vj.setFlexibleService(Boolean.TRUE);
      vj.setComment("dummy comment");

      String xmlComment = producer.buildComment(vj,false);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"dummy comment","comment should be correctly built");
   }

}
