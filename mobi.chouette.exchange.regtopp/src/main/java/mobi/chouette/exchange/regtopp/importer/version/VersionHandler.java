package mobi.chouette.exchange.regtopp.importer.version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.report.FileInfo;

public interface VersionHandler {
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file);

	public Parser createStopParser() throws ClassNotFoundException, IOException;

	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException;

	public LineSpecificParser createTripParser() throws ClassNotFoundException, IOException;
	
	public List<String> getMandatoryFileExtensions();
	
	public List<String> getOptionalFileExtensions();
}
