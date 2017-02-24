package mobi.chouette.exchange.netexprofile.importer;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.report.ActionReporter;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DuplicateIdCheckerCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
   		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_FILE_IDENTIFICATORS);

		
		for(IdVersion id : commonIds.keySet()) {
			List<String> filenameList = commonIds.get(id);
			if(filenameList.size() > 1) {
				for(String fileName : filenameList) {
					// TODO better error code
					actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, "Duplicate id "+id);
				}
				result = ERROR;
			}
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
