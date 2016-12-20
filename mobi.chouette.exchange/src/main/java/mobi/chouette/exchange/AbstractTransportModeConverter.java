package mobi.chouette.exchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.TransportMode;

/**
 * Classe permettant de gérer les conversions entre les modes de transports des différents format d'échange
 * @author gjamot
 *
 */
@Log4j
public class AbstractTransportModeConverter implements TransportModeConverter{
	protected static Path transportModePath;
	
	@Override
	public TransportMode importModeToSpecificMode(TransportMode importMode) {
		return null;
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		return null;
	}

	public static List<TransportMode> getTransportModeListFromJSONFile(Path transportModePath) {
		List<TransportMode> listTransportMode = null;
		byte[] bytes = null;
		String text = null;
		
		try {
			bytes = Files.readAllBytes(transportModePath);
			text = new String(bytes, "UTF-8");
			listTransportMode = new ArrayList<TransportMode>();
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
							TransportMode tM = new TransportMode(mode, subMode, pivot, pivotSub);
							listTransportMode.add(tM);
						}
					}
				} catch (JSONException e) {
					log.warn("unparsable GTFSTransportMode JSon Object");
				}

			}

		} catch (IOException e) {
			log.warn("Cannot read json file from server");
		}
		return listTransportMode;

	}

}
