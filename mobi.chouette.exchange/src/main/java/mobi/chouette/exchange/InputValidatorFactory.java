package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class InputValidatorFactory {

	   public static Map<String, InputValidatorFactory> factories = new HashMap<String, InputValidatorFactory>();

	   protected abstract InputValidator create() throws IOException;

	   public static final InputValidator create(String name)
	         throws ClassNotFoundException, IOException

	   {
	      if (!factories.containsKey(name))
	      {
	         Class.forName(name);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(name))
	            throw new ClassNotFoundException(name);
	      }
	      return ((InputValidatorFactory) factories.get(name)).create();
	   }
}
