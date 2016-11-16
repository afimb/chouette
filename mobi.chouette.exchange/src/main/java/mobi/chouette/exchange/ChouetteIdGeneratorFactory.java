package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ChouetteIdGeneratorFactory {
	public static Map<String, ChouetteIdGeneratorFactory> factories = new HashMap<String, ChouetteIdGeneratorFactory>();

	   protected abstract ChouetteIdGenerator create() throws IOException;

	   public static final ChouetteIdGenerator create(String format)
	         throws ClassNotFoundException, IOException

	   {
		  String className = format.substring(0, 1).toUpperCase() + format.substring(1) + "ChouetteIdGenerator";
	      if (!factories.containsKey(format))
	      {
	         Class.forName(className);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(format))
	            throw new ClassNotFoundException(format);
	      }
	      return ((ChouetteIdGeneratorFactory) factories.get(className)).create();
	   }
}
