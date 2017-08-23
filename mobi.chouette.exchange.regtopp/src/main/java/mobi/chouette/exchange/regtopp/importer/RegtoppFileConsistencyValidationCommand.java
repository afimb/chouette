package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp11DVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp13AVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;

public class RegtoppFileConsistencyValidationCommand implements Command {

	public static final String COMMAND = RegtoppFileConsistencyValidationCommand.class.getSimpleName();

	@Override
	public boolean execute(Context context) throws Exception {

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

		VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);
		
		if(importer.hasDSTImporter()) {
			importer.getDestinationById();
		}
		if(importer.hasMRKImporter()) {
			importer.getFootnoteById();
		}
		if(importer.hasDKOImporter()) {
			importer.getDayCodeById();
		}
		if(importer.hasHPLImporter()) {
			importer.getStopById();
		}
		if(importer.hasLINImporter()) {
			importer.getLineById();
		}
		if(importer.hasGAVImporter()) {
			importer.getPathwayByIndexingKey();
		}
		if(versionHandler instanceof Regtopp13AVersionHandler && importer.hasSTPImporter()) {
			importer.getStopPointsByIndexingKey();
		}
		if(versionHandler instanceof Regtopp11DVersionHandler && importer.hasTDAImporter()) {
			importer.getRouteSegmentByLineNumber();
		}
		if(importer.hasTIXImporter()) {
			importer.getTripIndex();
		}
		if(importer.hasTMSImporter()) {
			importer.getRouteIndex();
		}
		
		
		return SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppFileConsistencyValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppFileConsistencyValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
