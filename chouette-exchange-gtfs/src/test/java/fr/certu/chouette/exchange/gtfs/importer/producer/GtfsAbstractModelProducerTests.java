package fr.certu.chouette.exchange.gtfs.importer.producer;

import lombok.extern.log4j.Log4j;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@Log4j
public class GtfsAbstractModelProducerTests extends AbstractTestNGSpringContextTests
{

	@Test (groups = {"Producers"}, description = "test composeIncrementalObjectId" )
	public void verifyComposeIncrementalObjectId() throws ChouetteException 
	{
      AbstractModelProducer.setPrefix("GTFS");
	   AbstractModelProducer.setIncrementalPrefix("Inc_");
      Reporter.log(AbstractModelProducer.composeIncrementalObjectId("Type",null,log));
		Assert.assertTrue(AbstractModelProducer.composeIncrementalObjectId("Type",null,log).startsWith("GTFS:Type:Inc_NULL_"),"should return specific id on null entry ");
      Assert.assertEquals(AbstractModelProducer.composeIncrementalObjectId("Type","test",log),"GTFS:Type:Inc_test","should return incremental prefix on valid entry ");
      Assert.assertEquals(AbstractModelProducer.composeIncrementalObjectId("Type","test 2",log),"GTFS:Type:Inc_test_2","should replace invalid Neptune characters ");
      Assert.assertEquals(AbstractModelProducer.composeIncrementalObjectId("Type","toto.test 2",log),"toto:Type:Inc_test_2","should find prefic with . ");

	}

   @Test (groups = {"Producers"}, description = "test composeObjectId" )
   public void verifyComposeObjectId() throws ChouetteException 
   {
      AbstractModelProducer.setPrefix("GTFS");
      AbstractModelProducer.setIncrementalPrefix("Inc_");
      Reporter.log(AbstractModelProducer.composeObjectId("Type",null,log));
      Assert.assertTrue(AbstractModelProducer.composeObjectId("Type",null,log).startsWith("GTFS:Type:NULL_"),"should return specific id on null entry ");
      Assert.assertEquals(AbstractModelProducer.composeObjectId("Type","test",log),"GTFS:Type:test","should return incremental prefix on valid entry ");
      Assert.assertEquals(AbstractModelProducer.composeObjectId("Type","test 2",log),"GTFS:Type:test_2","should replace invalid Neptune characters ");
      Assert.assertEquals(AbstractModelProducer.composeObjectId("Type","toto.test 2",log),"toto:Type:test_2","should find prefic with . ");
   }


}
