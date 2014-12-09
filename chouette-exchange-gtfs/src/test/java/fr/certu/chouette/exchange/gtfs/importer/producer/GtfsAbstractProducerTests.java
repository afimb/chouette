package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Time;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsAbstractProducerTests extends AbstractTestNGSpringContextTests
{

	@Test (groups = {"Producers"}, description = "test getNonEmptyTrimedString" )
	public void verifyGetNonEmptyTrimedString() throws ChouetteException 
	{
		Assert.assertNull(AbstractProducer.getNonEmptyTrimedString(null),"should return null on null entry ");
      Assert.assertNull(AbstractProducer.getNonEmptyTrimedString("   "),"should return null on blank entry ");
      Assert.assertEquals(AbstractProducer.getNonEmptyTrimedString(" Test  "),"Test","should return trim string on entry with white spaces on ends");

	}

   @Test (groups = {"Producers"}, description = "test getTime" )
   public void verifyGetTime() throws ChouetteException 
   {
      GtfsTime gtfsTime = new GtfsTime(new Time(12,0,0),0);
      Assert.assertEquals(AbstractProducer.getTime(gtfsTime),new Time(12,0,0),"should return time part of gtfsTime on today ");
      gtfsTime = new GtfsTime(new Time(12,0,0),1);
      Assert.assertEquals(AbstractProducer.getTime(gtfsTime),new Time(12,0,0),"should return time part of gtfsTime on tomorrow");

   }


}
