package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

public class DuplicateIdCheckerCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		@SuppressWarnings("unchecked")
		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_FILE_IDENTIFICATORS);

		validationReporter.addItemToValidationReport(context, AbstractNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_COMMON_FILES, "E");
		boolean noDuplicates = true;
		
		//
		for(IdVersion id : commonIds.keySet()) {
			List<String> filenameList = commonIds.get(id);
			if(filenameList.size() > 1) {
				
				DataLocation sourceLocation = new DataLocation(filenameList.get(0), id.getLineNumber(), id.getColumnNumber());
				
				List<DataLocation> dataLocations = new ArrayList<>();
				
				for(int i=1; i<filenameList.size(); i++) {
					DataLocation d = new DataLocation(filenameList.get(i));
					dataLocations.add(d);
				}
				validationReporter.addCheckPointReportError(context, AbstractNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_COMMON_FILES,sourceLocation,id.getId(),null,dataLocations.toArray(new DataLocation[0]) );
				noDuplicates = false;
			}
		}
		
		if(noDuplicates) {
			validationReporter.reportSuccess(context, AbstractNetexProfileValidator._1_NETEX_DUPLICATE_IDS_ACROSS_COMMON_FILES);
		} else {
			result = ERROR;
		}
			
		
		return result;
	}
	
	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new DuplicateIdCheckerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(DuplicateIdCheckerCommand.class.getName(), new DefaultCommandFactory());
	}


}
