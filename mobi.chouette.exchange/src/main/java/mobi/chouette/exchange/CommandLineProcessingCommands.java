package mobi.chouette.exchange;

import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;

/**
 * 
 * give for command line processing elementary commands <br/>
 * on import process, getLineProcessingCommands should return a command instance for each line to process <br>
 * on export process, getLineProcessingCommands should return a single command instance reusable for each line <br>
 * <ul><li>line should be provided in context on LINE key</li></ul>
 * 
 * <br/> compression, decompression and level 3 validation commands should not be provided
 * 
 * @author michel
 *
 */
public interface CommandLineProcessingCommands {

	List<? extends Command> getPreProcessingCommands(Context context);
	List<? extends Command> getLineProcessingCommands(Context context);
	List<? extends Command> getPostProcessingCommands(Context context);
	
}
