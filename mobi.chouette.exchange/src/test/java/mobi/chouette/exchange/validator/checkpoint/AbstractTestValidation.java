package mobi.chouette.exchange.validator.checkpoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Time;

import mobi.chouette.common.Constant;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.validator.parameters.ValidationParameters;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.NeptuneLocalizedObject;

import org.testng.Assert;
import org.testng.Reporter;

public abstract class AbstractTestValidation implements Constant
{

   public static void printReport(ValidationReport report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      } else
      {
    	  Reporter.log(report.toString());
      }
   }


   /**
    * calculate distance on spheroid
    * 
    * @param obj1
    * @param obj2
    * @return
    */
   public static double distance(NeptuneLocalizedObject obj1,
         NeptuneLocalizedObject obj2)
   {
      double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
      double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
      double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
      double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());

      double alpha = Math.cos(lat1rad) * Math.cos(lat2rad)
            * Math.cos(long2rad - long1rad) + Math.sin(lat1rad)
            * Math.sin(lat2rad);

      double distance = 6378. * Math.acos(alpha);

      return distance * 1000.;
   }

   public static long diffTime(Time first, Time last)
   {
      if (first == null || last == null)
         return Long.MIN_VALUE; 
      long diff = last.getTime() / 1000L - first.getTime() / 1000L;
      if (diff < 0)
         diff += 86400L; // step upon midnight : add one day in seconds
      return diff;
   }
   
   /**
    * @param report
    */
   protected Detail checkReportForTest4_1(ValidationReport report, String key, String objectId)
   {
      Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
      Assert.assertNotNull(report.findCheckPointByName(key), " report must have 1 item on key "+key);
      CheckPoint checkPointReport = report.findCheckPointByName(key);
      Assert.assertEquals(checkPointReport.getDetails().size(), 1, " checkpoint must have 1 detail");
      Detail detail =  checkPointReport.getDetails().get(0);
      return detail;
   }

   protected ValidationParameters loadFullParameters() throws IOException
   {
		String filename = "src/test/data/checkPoints/fullparameterset.json";
		File f = new File(filename);
		byte[] bytes = Files.readAllBytes(f.toPath());
		String text = new String(bytes, "UTF-8");
		
		return (ValidationParameters) JSONUtil.fromJSON(text, ValidationParameters.class);

   }
   
   protected ValidationParameters loadParameters() throws IOException
   {
		String filename = "src/test/data/checkPoints/parameterset.json";
		File f = new File(filename);
		byte[] bytes = Files.readAllBytes(f.toPath());
		String text = new String(bytes, "UTF-8");
		return (ValidationParameters) JSONUtil.fromJSON(text, ValidationParameters.class);

   }


}
