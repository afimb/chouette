package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;

@Data
public class NeptuneImporterProcessingClasses implements CommandLineProcessingCommands {

	private List<Class<? extends Command>> preProcessingCommandClasses = new ArrayList<>();
	private List<Class<? extends Command>> lineProcessingCommandClasses = new ArrayList<>();
	private List<Class<? extends Command>> postProcessingCommandClasses = new ArrayList<>();
	
	private NeptuneImporterProcessingClasses()
	{
		lineProcessingCommandClasses.add(NeptuneSAXParserCommand.class);
		lineProcessingCommandClasses.add(NeptuneParserCommand.class);
		lineProcessingCommandClasses.add(NeptuneValidationCommand.class);
	}
	
	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new NeptuneImporterProcessingClasses();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(NeptuneImporterProcessingClasses.class.getName(),
				new DefaultFactory());
	}

	

}
