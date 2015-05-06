package mobi.chouette.exchange;

import java.util.List;

import mobi.chouette.common.chain.Command;

public interface CommandLineProcessingCommands {

	List<Class<? extends Command>> getPreProcessingCommandClasses();
	List<Class<? extends Command>> getLineProcessingCommandClasses();
	List<Class<? extends Command>> getPostProcessingCommandClasses();
	
}
