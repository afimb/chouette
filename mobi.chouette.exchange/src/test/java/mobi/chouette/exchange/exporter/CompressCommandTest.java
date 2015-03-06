package mobi.chouette.exchange.exporter;

import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.CommandFactory;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CompressCommandTest implements Constant 
{
	private  File d = new File("target/referential/test");
	
	@Test (groups = { "compress" }, description = "compress command")
	public void testProgressionInitialize() throws Exception 
	{
		InitialContext initialContext = new InitialContext();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(PATH, "target/referential/test");
		context.put(ARCHIVE, "output.zip");
		if (d.exists())
			try {
				org.apache.commons.io.FileUtils.deleteDirectory(d);
			} catch (IOException e) {
				e.printStackTrace();
			}
		d.mkdirs();
		File source = new File("src/test/data/compressTest.zip");
		File output = new File(d,OUTPUT);
		output.mkdir();
		FileUtils.uncompress(source.getAbsolutePath(), d.getAbsolutePath());
		CompressCommand command = (CompressCommand) CommandFactory
				.create(initialContext, CompressCommand.class.getName());
		command.execute(context);
		File archive = new File(d,"output.zip");
		Assert.assertTrue (archive.exists(), "arhive file should exists");

	}
	
}
