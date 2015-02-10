package mobi.chouette.common.chain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class CommandFactory {

	   public static Map<String, CommandFactory> factories = new HashMap<String, CommandFactory>();

	   protected abstract Command create(InitialContext context) throws IOException;

	   public static final Command create(InitialContext context, String name)
	         throws ClassNotFoundException, IOException

	   {
	      if (!factories.containsKey(name))
	      {
	         Class.forName(name);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(name))
	            throw new ClassNotFoundException(name);
	      }
	      return ((CommandFactory) factories.get(name)).create(context);
	   }
}
