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
	
	@Getter
	protected static Map<TransportMode, TransportMode> mapTransportToPivotMode;
	@Getter
	protected static Map<TransportMode, TransportMode> mapPivotToTransportMode;
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode importMode) {
		return null;
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		return null;
	}

	public static void getTransportModeListFromJSONFile(String urlStr) {
		byte[] bytes = null;
		String text = null;
		
		try {
			mapTransportToPivotMode = new HashMap<TransportMode, TransportMode>();
			mapPivotToTransportMode = new HashMap<TransportMode, TransportMode>();
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			
			bytes = IOUtils.toByteArray(is);
			text = new String(bytes, "UTF-8");
			if (text != null && text.trim().startsWith("[") && text.trim().endsWith("]")) {
				JSONArray arrayModesTransport;
				try {
					arrayModesTransport = new JSONArray(text);

					for (int i = 0; i < arrayModesTransport.length(); i++) {
						JSONObject transportMode = arrayModesTransport.getJSONObject(i);
						String mode = transportMode.optString(MODE, null);
						String subMode = transportMode.optString(SUBMODE, null);
						String pivot = transportMode.optString(PIVOT, null);
						String pivotSub = transportMode.optString(PIVOT_SUBMODE, null);

						if (mode != null && subMode != null && pivot != null && pivotSub != null) {
							mapTransportToPivotMode.put(new TransportMode(mode, subMode), new TransportMode(pivot, pivotSub));
							mapPivotToTransportMode.put(new TransportMode(pivot, pivotSub), new TransportMode(mode, subMode));
						}
					}
				} catch (JSONException e) {
					log.warn("unparsable GTFSTransportMode JSon Object");
				}

			}

		} catch (IOException e) {
			log.warn("Cannot read json file from server");
		}
	}

}
