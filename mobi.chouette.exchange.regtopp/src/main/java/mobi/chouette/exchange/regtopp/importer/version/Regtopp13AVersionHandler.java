package mobi.chouette.exchange.regtopp.importer.version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppRouteParser;
import mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppTripParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp13AVersionHandler extends Regtopp12VersionHandler {

	private static final List<String> mandatoryFileExtensions = Arrays.asList(RegtoppTripIndexTIX.FILE_EXTENSION, RegtoppRouteTMS.FILE_EXTENSION,
			RegtoppStopHPL.FILE_EXTENSION, RegtoppDayCodeDKO.FILE_EXTENSION, RegtoppStopPointSTP.FILE_EXTENSION);

	private static final List<String> optionalFileExtensions = Arrays.asList(RegtoppDestinationDST.FILE_EXTENSION, RegtoppFootnoteMRK.FILE_EXTENSION,
			RegtoppPathwayGAV.FILE_EXTENSION, RegtoppInterchangeSAM.FILE_EXTENSION, RegtoppZoneSON.FILE_EXTENSION, RegtoppLineLIN.FILE_EXTENSION,
			RegtoppVehicleJourneyVLP.FILE_EXTENSION, RegtoppTableVersionTAB.FILE_EXTENSION, RegtoppPeriodPER.FILE_EXTENSION,
			RegtoppRoutePointRUT.FILE_EXTENSION);

	@Override
	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException {
		return (RegtoppRouteParser) ParserFactory.create(RegtoppRouteParser.class.getName());
	}

	@Override
	public LineSpecificParser createTripParser() throws ClassNotFoundException, IOException {
		return (RegtoppTripParser) ParserFactory.create(RegtoppTripParser.class.getName());
	}

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		switch (extension) {

		case "STP": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopPointSTP.class }), file);
//TODO			importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile);
//TODO			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile);
			break;
		}
		case "RUT": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRoutePointRUT.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_POINT.name(), parseableFile);
			break;
		}
		default:
			super.registerFileForIndex(importer, fileName, extension, file);
		}
	}

	@Override
	public List<String> getMandatoryFileExtensions() {
		return mandatoryFileExtensions;
	}

	@Override
	public List<String> getOptionalFileExtensions() {
		return optionalFileExtensions;
	}
}
