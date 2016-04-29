package mobi.chouette.exchange.regtopp.importer.index.v11;

import java.io.File;
import java.util.Arrays;

import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

public abstract class AbstractIndexTest {
	protected Context context;

	protected RegtoppImporter importer;

	protected RegtoppValidationReporter validationReporter;

	@Test
	protected void setupImporter() {
		validationReporter = new RegtoppValidationReporter();
		context = new Context();
		String path = "src/test/data";
		importer = new RegtoppImporter(context, path, validationReporter);
	}

	protected FileContentParser createUnderlyingFileParser(File file, Class[] regtoppClasses) throws Exception {
		FileInfo fileInfo = new FileInfo(file.getName(), FILE_STATE.ERROR);
		ParseableFile parseableFile = new ParseableFile(file, Arrays.asList(regtoppClasses), fileInfo);
		FileContentParser fileContentParser = new FileContentParser(parseableFile);
		RegtoppValidationReporter validationReporter = new RegtoppValidationReporter();
		Context context = new Context();
		fileContentParser.parse(context, validationReporter);
		return fileContentParser;
	}
}
