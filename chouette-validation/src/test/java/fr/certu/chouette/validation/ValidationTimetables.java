package fr.certu.chouette.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.TimetableCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
"classpath*:chouetteContext.xml" })
public class ValidationTimetables extends AbstractValidation
{
   private TimetableCheckPoints checkPoint;
   private JSONObject parameters;
   private Timetable bean1;
   private Timetable bean2;
   private List<Timetable> beans = new ArrayList<>();
   
   @BeforeGroups (groups = { "timetable" })
   public void init()
   {
      checkPoint = (TimetableCheckPoints) applicationContext
            .getBean("timetableCheckPoints");
      long id = 1;

      parameters = null;
      try
      {
         parameters = new RuleParameterSet();
         parameters.put("check_time_table","1");

         bean1 = new Timetable();
         bean1.setId(id++);
         bean1.setObjectId("test1:Timetable:1");
         bean1.setName("test1");
         bean2 = new Timetable();
         bean2.setId(id++);
         bean2.setObjectId("test2:Timetable:1");
         bean2.setName("test2");
   
         beans.add(bean1);
         beans.add(bean2);
      } 
      catch (Exception e)
      {
         parameters = null;
         e.printStackTrace();
      }
      
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      parameters.put("check_time_table","0");
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), false, " report must have no items");

      parameters.put("check_time_table","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK," report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      Assert.assertEquals(report.getItems().size(), 1, " report must have 1 item");
      Assert.assertEquals(report.getItems().get(0).getMessageKey(), "4-Timetable-1", " report must have 1 item on correct key");
      Assert.assertEquals(report.getItems().get(0).getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-Timetable-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 pattern numeric")
   public void verifyTest4_1_pattern_numeric() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // pattern 
      bean1.setComment("1234");
      bean2.setComment("az234ZDER");
      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      column.put("pattern",1); // numeric

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-Timetable-1");
   }

   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 pattern alphabetic")
   public void verifyTest4_1_pattern_alpha() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      column.put("pattern",2); // alphabetic
      bean1.setComment("AzErTy");
      bean2.setComment("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-Timetable-1");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 pattern uppercase")
   public void verifyTest4_1_pattern_upper() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      column.put("pattern",3); // upper
      bean1.setComment("AZERTY");
      bean2.setComment("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-Timetable-1");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 pattern lowercase")
   public void verifyTest4_1_pattern_lower() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      column.put("pattern",4); // lower
      bean1.setComment("azerty");
      bean2.setComment("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-Timetable-1");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 min_size alpha")
   public void verifyTest4_1_min_size_alpha() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      
      // minsize alpha
      bean1.setComment("1234");
      bean2.setComment("");
      column.put("pattern",0);
      column.put("min_size","1"); // mandatory
      
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("min_size","");
      
      checkReportForTest4_1(report,"4-Timetable-1");

   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 max_size alpha")
   public void verifyTest4_1_max_size_alpha() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      // maxsize alpha
      bean1.setComment("12345");
      bean2.setComment("123456");
      column.put("pattern",0);
      column.put("max_size","5"); // mandatory

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("max_size","");

      checkReportForTest4_1(report,"4-Timetable-1");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 min_size numeric")
   public void verifyTest4_1_min_size_numeric() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      // minsize num
      column.put("pattern",1);
      bean1.setComment("124");
      bean2.setComment("123");
      column.put("min_size","124"); // mandatory

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("min_size","");
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-Timetable-1");
   }
   
   @Test(groups = { "timetable" }, description = "4-Timetable-1 max_size numeric")
   public void verifyTest4_1_max_size_numeric() throws ChouetteException
   {
      // 4-Timetable-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("time_table").getJSONObject("comment");
      // maxsize num
      bean1.setComment("1240");
      bean2.setComment("1241");
      column.put("max_size","1240"); // mandatory
      column.put("pattern",1);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("max_size","");
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-Timetable-1");
   }

   /**
    * @param report
    */
   protected void checkReportForTest4_1(PhaseReportItem report, String key)
   {
      DetailReportItem detail = checkReportForTest4_1(report,key,bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"comment","detail must refer comment");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getName(),"detail must refer value");
   }

}   
