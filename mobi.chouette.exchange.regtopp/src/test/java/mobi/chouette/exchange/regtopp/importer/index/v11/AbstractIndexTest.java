package mobi.chouette.exchange.regtopp.importer.index.v11;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

public abstract class AbstractIndexTest {
	protected Context context;

	protected RegtoppImporter importer;

	protected RegtoppValidationReporter validationReporter;

	@Test
	protected void setupImporter() {
		context = new Context();
		validationReporter = new RegtoppValidationReporter(context);
		String path = "src/test/data";
		importer = new RegtoppImporter(context, path, validationReporter);
	}

	protected FileContentParser createUnderlyingFileParser(File file, Class[] regtoppClasses, RegtoppException.ERROR error) throws Exception {
		ParseableFile parseableFile = new ParseableFile(file, Arrays.asList(regtoppClasses), error);
		FileContentParser fileContentParser = new FileContentParser(parseableFile);
		RegtoppValidationReporter validationReporter = new RegtoppValidationReporter(context);
		fileContentParser.parse(context, validationReporter);
		return fileContentParser;
	}

}
