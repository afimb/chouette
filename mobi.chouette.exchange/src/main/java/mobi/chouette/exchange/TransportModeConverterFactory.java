package mobi.chouette.exchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class TransportModeConverterFactory {
	public static Map<String, TransportModeConverterFactory> factories = new HashMap<String, TransportModeConverterFactory>();

	   protected abstract TransportModeConverter create() throws IOException;

	   public static final TransportModeConverter create(String format)
	         throws ClassNotFoundException, IOException

	   {
		   String formatToLowerCase = format.toLowerCase();
		  String className = "mobi.chouette.exchange."+formatToLowerCase+"." + formatToLowerCase.substring(0, 1).toUpperCase() + formatToLowerCase.substring(1) + "TransportModeConverter";
	      if (!factories.containsKey(format))
	      {
	         Class.forName(className);
			 // log.info("[DSU] create : " + name);
	         if (!factories.containsKey(formatToLowerCase))
	            throw new ClassNotFoundException(format);
	      }
	      return ((TransportModeConverterFactory) factories.get(format)).create();
	   }
}
