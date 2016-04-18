package mobi.chouette.exchange.regtopp.importer.version;

import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12novus.RegtoppStopHPL;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp12NovusVersionHandler extends Regtopp12VersionHandler {

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
	
		switch(extension) {
		case "HPL":
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile);
			break;
		default:
			super.registerFileForIndex(importer, fileName, extension, file);
		}
	}
	
	@Override
	public String createStopPointId(RegtoppRouteTMS tms) {
		return tms.getStopId()+tms.getStopIdDeparture();
	}

}
