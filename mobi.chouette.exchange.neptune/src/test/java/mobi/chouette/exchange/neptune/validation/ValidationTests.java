package mobi.chouette.exchange.neptune.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.JobDataTest;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneSAXParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneValidationCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;


public class ValidationTests implements Constant, ReportConstant
{

   private static final String path = "src/test/data/lignes_neptune_err/";

	protected static InitialContext initialContext;
	

	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		NeptuneImportParameters configuration = new NeptuneImportParameters();
		context.put(CONFIGURATION, configuration);
		context.put(REFERENTIAL, new Referential());
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		
		test.setPathName( "target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential( "chouette_gui");
		test.setAction( IMPORTER);
		test.setType("neptune");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

   protected void verifyValidation(String testFile,
         String mandatoryErrorTest, CheckPoint.SEVERITY severity, RESULT status) throws Exception
   {
      Context context = initImportContext();
    		  
      NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
      File f = new File(path,testFile);
      parser.setFileURL("file://"+f.getAbsolutePath());
      parser.execute(context);
      NeptuneValidationCommand validator = (NeptuneValidationCommand) CommandFactory.create(initialContext, NeptuneValidationCommand.class.getName());
      validator.execute(context);
      
      checkMandatoryTest(context, mandatoryErrorTest, severity, status);

   }

   protected void verifySaxValidation(String testFile,
	         String mandatoryErrorTest, CheckPoint.SEVERITY severity, RESULT status) throws Exception
	   {
	      Context context = initImportContext();
	    		  
	      NeptuneSAXParserCommand parser = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
	      File f = new File(path,testFile);
	      parser.setFileURL("file://"+f.getAbsolutePath());
	      parser.execute(context);
	      
	      checkMandatoryTest(context, mandatoryErrorTest, severity, status);

	   }

   protected void verifyCrossValidation(String testFile,
	         String mandatoryErrorTest, RESULT status) throws Exception
	   {
//	      Context context = initImportContext();
	    		
	      // TODO implement tests with multiple files
//	      NeptuneSAXParserCommand parser = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
//	      File f = new File(path,testFile);
//	      parser.setFileURL("file://"+f.getAbsolutePath());
//	      parser.execute(context);
//	      
//	      checkMandatoryTest(context, mandatoryErrorTest, status);

	   }

   /**
    * @param mandatoryTest
    * @param importReport
    * @param valReport
    * @param state
    */
   private void checkMandatoryTest(Context context, String mandatoryTest, CheckPoint.SEVERITY severity,
          RESULT state)
   {
	   ValidationReport valReport = (ValidationReport) context.get(VALIDATION_REPORT);
      if (mandatoryTest.equals("NONE"))
      {
         for (CheckPoint phase : valReport.getCheckPoints())
         {
            Assert.assertFalse(phase.getState().equals(RESULT.NOK),
                  phase.getName() + " must have status " + state);
         }
      } else
      {
    	  CheckPoint foundItem = null;
         for (CheckPoint cp : valReport.getCheckPoints())
            {
               if (cp.getName().equals(mandatoryTest))
               {
                  foundItem = cp;
                  break;
               }
            
         }
         Assert.assertNotNull(foundItem, mandatoryTest + " must be reported");
         Assert.assertEquals(foundItem.getSeverity(), severity,
                 mandatoryTest + " must have severity " + severity);
         Assert.assertEquals(foundItem.getState(), state,
               mandatoryTest + " must have status " + state);
         if (foundItem.getState().equals(RESULT.NOK))
         {
        	 String detailKey = mandatoryTest.replaceAll("-", "_").toLowerCase();
        	 Assert.assertNotEquals(foundItem.getDetails(), 0, "details should be present");
        	 List<Detail> details = foundItem.getDetails();
        	 for (Detail detail : details) {
				Assert.assertTrue(detail.getKey().startsWith(detailKey),"details key should start with test key : expected "+detailKey+", found : "+detail.getKey());
			}
         }
      }
   }


}
