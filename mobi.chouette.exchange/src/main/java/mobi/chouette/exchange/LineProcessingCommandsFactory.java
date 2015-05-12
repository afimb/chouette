package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class CommandLineProcessingCommandsFactory {

	   public static Map<String, CommandLineProcessingCommandsFactory> factories = new HashMap<String, CommandLineProcessingCommandsFactory>();

	   protected abstract CommandLineProcessingCommands create() throws IOException;

	   public static final CommandLineProcessingCommands create(String name)
	         throws ClassNotFoundException, IOException

	   {
	      if (!factories.containsKey(name))
	      {
	         Class.forName(name);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(name))
	            throw new ClassNotFoundException(name);
	      }
	      return ((CommandLineProcessingCommandsFactory) factories.get(name)).create();
	   }
}
