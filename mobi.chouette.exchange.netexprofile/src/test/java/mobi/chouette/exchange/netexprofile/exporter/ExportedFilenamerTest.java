package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Line;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExportedFilenamerTest {


	@Test
	public void nonAsciiCharsInFileNameAreReplaced() {
		Context context = new Context();
		NetexprofileExportParameters params = new NetexprofileExportParameters();
		params.setDefaultCodespacePrefix("TST");
		context.put(Constant.CONFIGURATION, params);

		Line line = new Line();
		line.setName("ÆØÅæøå");
		line.setObjectId("TST:Line:1");
		String lineFilename = ExportedFilenamer.createLineFilename(context, line);

		Assert.assertEquals(lineFilename, "TST_TST-Line-1_EOAeoa.xml");

	}
}
