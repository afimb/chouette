package mobi.chouette.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.TransportMode;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Classe permettant de gérer les conversions entre les modes de transports des différents format d'échange
 * @author gjamot
 *
 */
@Log4j
public abstract class AbstractTransportModeConverter implements TransportModeConverter{
	
	
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode importMode) {
		return null;
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		return null;
	}

}
