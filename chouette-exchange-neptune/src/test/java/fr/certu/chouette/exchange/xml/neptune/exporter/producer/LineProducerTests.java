package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.model.neptune.Footnote;
import fr.certu.chouette.model.neptune.Line;

@ContextConfiguration(locations = { "classpath:testContext.xml",
"classpath*:chouetteContext.xml" })
public class LineProducerTests extends AbstractTestNGSpringContextTests
{

   private LineProducer producer = new LineProducer(); 
   
   private Footnote buildFootnote(String key, Line line)
   {
      Footnote note = new Footnote();
      note.setKey(key);
      note.setCode("code"+key);
      note.setLabel("label"+key);
      note.setLine(line);
      return note;
   }

   @Test(groups = { "buildComment" }, description = "check empty comment and extensions")
   public void verifyBuildEmptyComment() throws ChouetteException
   {
      Line line = new Line();

      String xmlComment = producer.buildComment(line,true);
      Assert.assertNull(xmlComment,"comment should be null");
   }

   @Test(groups = { "buildComment" }, description = "check normal comment without extensions")
   public void verifyBuildNormalComment() throws ChouetteException
   {
      Line line = new Line();
      line.setComment("dummy comment");

      String xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"dummy comment","comment should be correctly built");
   }

   @Test(groups = { "buildComment" }, description = "check null comment with colors extension")
   public void verifyBuildColorsComment() throws ChouetteException
   {
      Line line = new Line();
      line.setTextColor("ff23b3");
      line.setColor("00ff00");

      String xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"line_color\":\"00ff00\",\"text_color\":\"ff23b3\"}","comment should be correctly built");
   }

   @Test(groups = { "buildComment" }, description = "check null comment with flexible service extension")
   public void verifyBuildFlexibleServiceComment() throws ChouetteException
   {
      Line line = new Line();
      line.setFlexibleService(Boolean.TRUE);

      String xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"flexible_service\":true}","comment should be correctly built");

      line.setFlexibleService(Boolean.FALSE);

      xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"flexible_service\":false}","comment should be correctly built");

   }

   @Test(groups = { "buildComment" }, description = "check null comment and footnotes extensions")
   public void verifyBuildFootnotesComment() throws ChouetteException
   {
      Line line = new Line();
      line.getFootnotes().add(buildFootnote("1", line));
      line.getFootnotes().add(buildFootnote("2", line));
      
      String xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"footnotes\":["+
            "{\"label\":\"label1\",\"code\":\"code1\",\"key\":\"1\"},"+
            "{\"label\":\"label2\",\"code\":\"code2\",\"key\":\"2\"}"+
            "]}","comment should be correctly built");
   }

   @Test(groups = { "buildComment" }, description = "check comment with all extension")
   public void verifyBuildCompleteComment() throws ChouetteException
   {
      Line line = new Line();
      line.getFootnotes().add(buildFootnote("1", line));
      line.getFootnotes().add(buildFootnote("2", line));
      line.setComment("dummy comment");
      line.setFlexibleService(Boolean.FALSE);
      line.setTextColor("ff23b3");
      line.setColor("00ff00");
      
      String xmlComment = producer.buildComment(line,true);
      Reporter.log("comment = "+xmlComment);
      
      Assert.assertTrue(xmlComment.startsWith("{"),"comment should start with {");
      Assert.assertTrue(xmlComment.endsWith("}"),"comment should end with }");
      Assert.assertTrue(xmlComment.contains(JsonExtension.COMMENT),"comment should contain comment tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.FOOTNOTES),"comment should contain footnotes tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.KEY),"comment should contain key tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.CODE),"comment should contain code tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.LABEL),"comment should contain label tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.FLEXIBLE_SERVICE),"comment should contain flexible service tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.TEXT_COLOR),"comment should contain text color tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.LINE_COLOR),"comment should contain line color tag");

   }

   @Test(groups = { "buildComment" }, description = "check comment with no extension asked")
   public void verifyBuildNoExtension() throws ChouetteException
   {
      Line line = new Line();
      line.getFootnotes().add(buildFootnote("1", line));
      line.getFootnotes().add(buildFootnote("2", line));
      line.setComment("dummy comment");
      line.setFlexibleService(Boolean.FALSE);
      line.setTextColor("ff23b3");
      line.setColor("00ff00");
      
      String xmlComment = producer.buildComment(line,false);
      Reporter.log("comment = "+xmlComment);
      
      Assert.assertEquals(xmlComment,"dummy comment","comment should be correctly built");

   }


}
