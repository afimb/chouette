package mobi.chouette.exchange.regtopp.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveException;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.FileUtil;
import mobi.chouette.exchange.regtopp.importer.version.RegtoppVersion;

public class RegtoppParameterGuesserTest {

	@Test
	public void testGuessTestdata1() throws IOException, ArchiveException {
		guess("src/test/data/fullsets/TB220116_1_1D.zip", "IBM865", RegtoppVersion.R11D);
	}

	@Test
	public void testGuessTestdata2() throws IOException, ArchiveException {
		guess("src/test/data/fullsets/atb-20160118-20160619.zip", "ISO-8859-1", RegtoppVersion.R12);
	}

	private void guess(String filename, String expectedEncoding, RegtoppVersion expectedVersion) throws IOException, ArchiveException {

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File testFolder = new File(tmpDir, UUID.randomUUID().toString());
		testFolder.mkdirs();

		FileUtil.uncompress(filename, testFolder.getAbsolutePath());

		Path p = testFolder.toPath();

		RegtoppParameterGuesser guesser = new RegtoppParameterGuesser(p);
		Assert.assertEquals(guesser.getEncoding(), expectedEncoding);
		Assert.assertEquals(guesser.getDetectedVersion(), expectedVersion);

	}
}
