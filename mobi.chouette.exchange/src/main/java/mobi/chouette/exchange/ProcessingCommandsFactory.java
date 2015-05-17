package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class ProcessingCommandsFactory {

	   public static Map<String, ProcessingCommandsFactory> factories = new HashMap<String, ProcessingCommandsFactory>();

	   protected abstract ProcessingCommands create() throws IOException;

	   public static final ProcessingCommands create(String name)
	         throws ClassNotFoundException, IOException

	   {
	      if (!factories.containsKey(name))
	      {
	         Class.forName(name);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(name))
	            throw new ClassNotFoundException(name);
	      }
	      return ((ProcessingCommandsFactory) factories.get(name)).create();
	   }
}
