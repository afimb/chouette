package mobi.chouette.exchange.regtopp.importer.version;

import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp11DVersionHandler implements VersionHandler {

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		// TODO
	}

	@Override
	public String createStopPointId(RegtoppRouteTMS tms) {
		// TODO Auto-generated method stub
		return null;
	}
}
