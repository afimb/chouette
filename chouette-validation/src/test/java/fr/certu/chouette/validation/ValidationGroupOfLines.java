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
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.GroupOfLineCheckPoints;

@ContextConfiguration(locations = { "classpath:testContext.xml",
"classpath*:chouetteContext.xml" })
public class ValidationGroupOfLines extends AbstractValidation
{
   private GroupOfLineCheckPoints checkPoint;
   private JSONObject parameters;
   private GroupOfLine bean1;
   private GroupOfLine bean2;
   private List<GroupOfLine> beans = new ArrayList<>();
   
   @BeforeGroups (groups = { "groupOfLine" })
   public void init()
   {
      checkPoint = (GroupOfLineCheckPoints) applicationContext
            .getBean("groupOfLineCheckPoints");
      long id = 1;

      parameters = null;
      try
      {
         parameters = new RuleParameterSet();
         parameters.put("check_group_of_line","1");

         bean1 = new GroupOfLine();
         bean1.setId(id++);
         bean1.setObjectId("test1:GroupOfLine:1");
         bean1.setName("test1");
         bean2 = new GroupOfLine();
         bean2.setId(id++);
         bean2.setObjectId("test2:GroupOfLine:1");
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
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 no test")
   public void verifyTest4_1_notest() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      parameters.put("check_group_of_line","0");
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK,
            " report must be on level ok");
      Assert.assertEquals(report.hasItems(), false, " report must have no items");

      parameters.put("check_group_of_line","1");
      report = new PhaseReportItem(PHASE.THREE);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();

      Assert.assertEquals(report.getStatus(), Report.STATE.OK," report must be on level ok");
      Assert.assertEquals(report.hasItems(), true, " report must have items");
      Assert.assertEquals(report.getItems().size(), 1, " report must have 1 item");
      Assert.assertEquals(report.getItems().get(0).getMessageKey(), "4-GroupOfLine-1", " report must have 1 item on correct key");
      Assert.assertEquals(report.getItems().get(0).getItems().size(), 0, " checkpoint must have no detail");

   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 unicity")
   public void verifyTest4_1_unique() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // unique
      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("objectid");
      column.put("unique",1);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("unique",0);

      DetailReportItem detail = checkReportForTest4_1(report,"4-GroupOfLine-1",bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"objectid","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getObjectId().split(":")[2],"detail must refer value");
      Assert.assertEquals(detail.getArgs().get("alternateId"),bean1.getObjectId(),"detail must refer fisrt bean");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 pattern numeric")
   public void verifyTest4_1_pattern_numeric() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      // pattern 
      bean1.setRegistrationNumber("1234");
      bean2.setRegistrationNumber("az234ZDER");
      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      column.put("pattern",1); // numeric

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }

   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 pattern alphabetic")
   public void verifyTest4_1_pattern_alpha() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      column.put("pattern",2); // alphabetic
      bean1.setRegistrationNumber("AzErTy");
      bean2.setRegistrationNumber("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 pattern uppercase")
   public void verifyTest4_1_pattern_upper() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      column.put("pattern",3); // upper
      bean1.setRegistrationNumber("AZERTY");
      bean2.setRegistrationNumber("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0); // all

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 pattern lowercase")
   public void verifyTest4_1_pattern_lower() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      column.put("pattern",4); // lower
      bean1.setRegistrationNumber("azerty");
      bean2.setRegistrationNumber("az234ZDER");

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 min_size alpha")
   public void verifyTest4_1_min_size_alpha() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      
      // minsize alpha
      bean1.setRegistrationNumber("1234");
      bean2.setRegistrationNumber("");
      column.put("pattern",0);
      column.put("min_size","1"); // mandatory
      
      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("min_size","");
      
      checkReportForTest4_1(report,"4-GroupOfLine-1");

   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 max_size alpha")
   public void verifyTest4_1_max_size_alpha() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      // maxsize alpha
      bean1.setRegistrationNumber("12345");
      bean2.setRegistrationNumber("123456");
      column.put("pattern",0);
      column.put("max_size","5"); // mandatory

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("max_size","");

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 min_size numeric")
   public void verifyTest4_1_min_size_numeric() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      // minsize num
      column.put("pattern",1);
      bean1.setRegistrationNumber("124");
      bean2.setRegistrationNumber("123");
      column.put("min_size","124"); // mandatory

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("min_size","");
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }
   
   @Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 max_size numeric")
   public void verifyTest4_1_max_size_numeric() throws ChouetteException
   {
      // 4-GroupOfLine-1 : check columns
      Assert.assertNotNull(parameters, "no parameters for test");

      PhaseReportItem report = new PhaseReportItem(PHASE.THREE);

      JSONObject column = parameters.getJSONObject("group_of_line").getJSONObject("registration_number");
      // maxsize num
      bean1.setRegistrationNumber("1240");
      bean2.setRegistrationNumber("1241");
      column.put("max_size","1240"); // mandatory
      column.put("pattern",1);

      checkPoint.check(beans, parameters, report, new HashMap<String, Object>());
      report.refreshStatus();
      column.put("max_size","");
      column.put("pattern",0);

      checkReportForTest4_1(report,"4-GroupOfLine-1");
   }

   /**
    * @param report
    */
   protected void checkReportForTest4_1(PhaseReportItem report, String key)
   {
      DetailReportItem detail = checkReportForTest4_1(report,key,bean2.getObjectId());
      Assert.assertEquals(detail.getArgs().get("column"),"registration_number","detail must refer column");
      Assert.assertEquals(detail.getArgs().get("value"),bean2.getRegistrationNumber(),"detail must refer value");
   }

}   
