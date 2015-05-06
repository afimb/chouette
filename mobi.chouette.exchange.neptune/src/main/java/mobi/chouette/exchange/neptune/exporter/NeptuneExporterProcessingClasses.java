package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;

@Data
public class NeptuneExporterProcessingClasses implements CommandLineProcessingCommands {

	private List<Class<? extends Command>> preProcessingCommandClasses = new ArrayList<>();
	private List<Class<? extends Command>> lineProcessingCommandClasses = new ArrayList<>();
	private List<Class<? extends Command>> postProcessingCommandClasses = new ArrayList<>();
	
	private NeptuneExporterProcessingClasses()
	{
		lineProcessingCommandClasses.add(NeptuneProducerCommand.class);
	}
	
	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new NeptuneExporterProcessingClasses();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(NeptuneExporterProcessingClasses.class.getName(),
				new DefaultFactory());
	}

	

}
