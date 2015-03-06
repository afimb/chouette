package mobi.chouette.exchange.metadata;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * 
 * @author michel
 */

public class MetadataTest
{
   

   @Test(groups = { "metadata" }, description = "check period")
   public void verifyMetadataUpdatePeriod() throws Exception
   {
      Metadata data =new Metadata();
      Assert.assertFalse(data.getTemporalCoverage().isSet(), "temporal coverage not set on new");

      Calendar start = Calendar.getInstance();
      start.set(2014,Calendar.DECEMBER,01,13,00);
      Calendar end = Calendar.getInstance();
      end.set(2015,Calendar.MARCH,31,13,00);
      
      data.getTemporalCoverage().update(start, end);
      Assert.assertEquals(data.getTemporalCoverage().getStart(),start,"temporal start date must be good");
      Assert.assertEquals(data.getTemporalCoverage().getEnd(),end,"temporal end date must be good");
      
      start.set(2014,Calendar.DECEMBER,15,13,00);
      end.set(2015,Calendar.MARCH,15,13,00);
      data.getTemporalCoverage().update(start, end);
      Assert.assertNotSame(data.getTemporalCoverage().getStart(),start,"temporal start date must be good");
      Assert.assertNotSame(data.getTemporalCoverage().getEnd(),end,"temporal end date must be good");
      
      start.set(2014,Calendar.NOVEMBER,15,13,00);
      end.set(2015,Calendar.APRIL,15,13,00);
      data.getTemporalCoverage().update(start, end);
      Assert.assertEquals(data.getTemporalCoverage().getStart(),start,"temporal start date must be good");
      Assert.assertEquals(data.getTemporalCoverage().getEnd(),end,"temporal end date must be good");
   }
   
   
   @Test(groups = { "metadata" }, description = "check spatial")
   public void verifyMetadataUpdateSpatial() throws Exception
   {
      Metadata data =new Metadata();
      Assert.assertFalse(data.getSpatialCoverage().isSet(), "spatial coverage not set on new");

      double lat1 = 45;
      double lon1 = 0;
      double lat2 = 0;
      double lon2 = -15;
      double lat3 = 60;
      double lon3 = 15;
            
      
      data.getSpatialCoverage().update(lon1, lat1);
      Assert.assertEquals(data.getSpatialCoverage().getNorthLimit(),lat1,"spatial north must be good");
      Assert.assertEquals(data.getSpatialCoverage().getSouthLimit(),lat1,"spatial south must be good");
      Assert.assertEquals(data.getSpatialCoverage().getEastLimit(),lon1,"spatial east must be good");
      Assert.assertEquals(data.getSpatialCoverage().getWestLimit(),lon1,"spatial west must be good");
      
      data.getSpatialCoverage().update(lon2, lat2);
      Assert.assertEquals(data.getSpatialCoverage().getNorthLimit(),lat1,"spatial north must be good");
      Assert.assertEquals(data.getSpatialCoverage().getSouthLimit(),lat2,"spatial south must be good");
      Assert.assertEquals(data.getSpatialCoverage().getEastLimit(),lon1,"spatial east must be good");
      Assert.assertEquals(data.getSpatialCoverage().getWestLimit(),lon2,"spatial west must be good");

      data.getSpatialCoverage().update(lon3, lat3);
      Assert.assertEquals(data.getSpatialCoverage().getNorthLimit(),lat3,"spatial north must be good");
      Assert.assertEquals(data.getSpatialCoverage().getSouthLimit(),lat2,"spatial south must be good");
      Assert.assertEquals(data.getSpatialCoverage().getEastLimit(),lon3,"spatial east must be good");
      Assert.assertEquals(data.getSpatialCoverage().getWestLimit(),lon2,"spatial west must be good");

   }
   
   

}
