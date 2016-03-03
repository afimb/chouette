package mobi.chouette.exchange.regtopp.importer;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.Constant;
import mobi.chouette.exchange.regtopp.JobDataTest;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;

public class RegtopImporterCommandTest  implements mobi.chouette.common.Constant{

	private InitialContext initialContext ;

	
	private void init()
	{
		if (initialContext == null)
		{
			try {
				initialContext = new InitialContext();
		
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void importRegtopAtBStopArea() throws Exception {
		init();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		File f = new File("src/test/data/atb-20160118-20160619.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = new JobDataTest();
		context.put(JOB_DATA, job);
		job.setAction("importer");
		job.setType("regtopp");
		job.setPathName("target/referential/test");
		job.setFilename(f.getName());
		job.setReferential("chouette_gui");
		
		ActionReport report = new ActionReport();
		ValidationReport validationReport = new ValidationReport();
		RegtoppImportParameters parameters = new RegtoppImportParameters();
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("stop_area");
		context.put(CONFIGURATION, parameters);
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		boolean result = command.execute(context);
		
		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}
}
