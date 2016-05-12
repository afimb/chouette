package mobi.chouette.exchange.netex;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex.importer.NetexParserCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;

import org.testng.annotations.Test;

public class NetexParserTest  implements mobi.chouette.common.Constant{

	private InitialContext initialContext ;

	
	private void init()
	{
		if (initialContext == null)
		{
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void valid() throws Exception {
		init();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NetexParserCommand command = (NetexParserCommand) CommandFactory.create(initialContext, NetexParserCommand.class.getName());
		File f = new File("src/test/data/valid/line_test.xml");
		JobDataTest job = new JobDataTest();
		context.put(JOB_DATA, job);
		job.setAction("importer");
		job.setType("netex");
		job.setPathName("target/referential/test");
		job.setReferential("chouette_gui");
		
		ActionReport report = new ActionReport();
		ValidationReport validationReport = new ValidationReport();
		command.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		command.execute(context);
	}
}
