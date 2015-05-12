package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class LineProcessingCommandsFactory {

	   public static Map<String, LineProcessingCommandsFactory> factories = new HashMap<String, LineProcessingCommandsFactory>();

	   protected abstract LineProcessingCommands create() throws IOException;

	   public static final LineProcessingCommands create(String name)
	         throws ClassNotFoundException, IOException

	   {
	      if (!factories.containsKey(name))
	      {
	         Class.forName(name);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(name))
	            throw new ClassNotFoundException(name);
	      }
	      return ((LineProcessingCommandsFactory) factories.get(name)).create();
	   }
}
