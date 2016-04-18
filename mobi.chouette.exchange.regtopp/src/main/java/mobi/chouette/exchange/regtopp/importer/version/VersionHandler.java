package mobi.chouette.exchange.regtopp.importer.version;

import java.nio.file.Path;

import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.report.FileInfo;

public interface VersionHandler {
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file);

	public String createStopPointId(RegtoppRouteTMS tms);
}
