package mobi.chouette.exchange.netexprofile.importer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NetexLineParserCommandTest {

	@Test
	public void testParseDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();
		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		context.put(Constant.REPORT, new ActionReport());

		Path filePath = Paths.get("src/test/data/C_NETEX_1.xml");
		String url = filePath.toUri().toURL().toExternalForm();
		Assert.assertTrue(Files.exists(filePath));

		Referential referential = new Referential();
		context.put(Constant.REFERENTIAL,referential);

		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);
		
		NetexInitReferentialCommand initRef = new NetexInitReferentialCommand();
		initRef.setLineFile(true);
		initRef.setFileURL(url);
		initRef.execute(context);
		
		NetexLineParserCommand cmd = new NetexLineParserCommand();
		
		cmd.setFileURL(url);
		File file = new File(new URL(url).toURI());
		context.put(Constant.FILE_NAME, file.getName());
		
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, file.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);

		boolean result = cmd.execute(context );
		Assert.assertTrue(result);
		
		//TODO Assert.assertEquals(referential.getLines().size(),1);
	}
}
