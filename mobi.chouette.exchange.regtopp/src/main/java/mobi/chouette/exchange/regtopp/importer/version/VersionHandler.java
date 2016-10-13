package mobi.chouette.exchange.regtopp.importer.version;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.report.FileReport;

import java.io.IOException;
import java.nio.file.Path;

public interface VersionHandler {
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension);

	public Parser createStopParser() throws ClassNotFoundException, IOException;

	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException;

	public LineSpecificParser createTripParser() throws ClassNotFoundException, IOException;
	
	public Parser createConnectionLinkParser() throws ClassNotFoundException, IOException;

	public String[] getMandatoryFileExtensions();
	
	public String[] getOptionalFileExtensions();

}
