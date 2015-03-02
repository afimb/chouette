package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.AlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.BoardingPossibilityEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml",
"classpath*:chouetteContext.xml" })
public class StopPointProducerTests extends AbstractTestNGSpringContextTests
{

   private StopPointProducer producer = new StopPointProducer(); 

   @Test(groups = { "buildComment" }, description = "check empty comment and extensions")
   public void verifyBuildEmptyComment() throws ChouetteException
   {
      StopPoint point = new StopPoint();

      String xmlComment = producer.buildComment(point,true);
      Assert.assertNull(xmlComment,"comment should be null");
   }

   @Test(groups = { "buildComment" }, description = "check aligthing extension")
   public void verifyBuildAlightingComment() throws ChouetteException
   {
      StopPoint point = new StopPoint();
      point.setForAlighting(AlightingPossibilityEnum.forbidden);
      String xmlComment = producer.buildComment(point,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"routing_constraints\":{\"alighting\":\"forbidden\"}}","comment should be correctly built");
   }

   @Test(groups = { "buildComment" }, description = "check boarding extension")
   public void verifyBuildBoardingComment() throws ChouetteException
   {
      StopPoint point = new StopPoint();
      point.setForBoarding(BoardingPossibilityEnum.forbidden);
      String xmlComment = producer.buildComment(point,true);
      Reporter.log("comment = "+xmlComment);
      Assert.assertEquals(xmlComment,"{\"routing_constraints\":{\"boarding\":\"forbidden\"}}","comment should be correctly built");

   }

   @Test(groups = { "buildComment" }, description = "check complete extension")
   public void verifyBuildCompleteComment() throws ChouetteException
   {
      StopPoint point = new StopPoint();
      point.setForBoarding(BoardingPossibilityEnum.forbidden);
      point.setForAlighting(AlightingPossibilityEnum.is_flexible);
      
      String xmlComment = producer.buildComment(point,true);
      Reporter.log("comment = "+xmlComment);
      
      Assert.assertTrue(xmlComment.startsWith("{"),"comment should start with {");
      Assert.assertTrue(xmlComment.endsWith("}"),"comment should end with }");
      Assert.assertTrue(xmlComment.contains(JsonExtension.BOARDING),"comment should contain boarding tag");
      Assert.assertTrue(xmlComment.contains(JsonExtension.ALIGHTING),"comment should contain alighting tag");
      Assert.assertTrue(xmlComment.contains("is_flexible"),"comment should contain is_flexible value");
      Assert.assertTrue(xmlComment.contains("forbidden"),"comment should contain forbdden value");

   }
   
   @Test(groups = { "buildComment" }, description = "check empty comment with no extension asked")
   public void verifyBuildNoExtension() throws ChouetteException
   {
      StopPoint point = new StopPoint();
      point.setForBoarding(BoardingPossibilityEnum.forbidden);
      point.setForAlighting(AlightingPossibilityEnum.is_flexible);

      String xmlComment = producer.buildComment(point,false);
      Assert.assertNull(xmlComment,"comment should be null");
   }

}
