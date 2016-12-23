package mobi.chouette.exchange.gtfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.AbstractTransportModeConverter;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@Log4j
public class ExtendedGtfsTransportModeConverter extends AbstractTransportModeConverter{
	
	private static final String transportModeUrlSrc = "https://raw.githubusercontent.com/afimb/chouette-projects-i18n/master/data/transport_mode/gtfs_extended.json";
	@Getter
	private static BiMap<Integer, TransportMode> mapCodeToTransportMode;
	@Getter
	protected static Map<TransportMode, TransportMode> mapTransportToPivotMode;
	@Getter
	protected static Map<TransportMode, TransportMode> mapPivotToTransportMode;
	
	private ExtendedGtfsTransportModeConverter(){

	}

	private static ExtendedGtfsTransportModeConverter INSTANCE = null;
	
	@Getter
	private static List<TransportMode> listTransportMode = null;

	public static synchronized ExtendedGtfsTransportModeConverter getInstance(){
		
		if(INSTANCE == null){
			log.warn("Appel de la premiere instanciation du convertisseur GTFS");
			getTransportModeListFromJSONFile(transportModeUrlSrc);
			INSTANCE = new ExtendedGtfsTransportModeConverter();
		}
		
		return INSTANCE;
	}
	
	
	
	public static void getTransportModeListFromJSONFile(String urlStr) {
		byte[] bytes = null;
		String text = null;
		try {
			mapCodeToTransportMode = HashBiMap.create();
			mapTransportToPivotMode = new HashMap<TransportMode, TransportMode>();
			mapPivotToTransportMode = new HashMap<TransportMode, TransportMode>();
			URL url = new URL(urlStr);
		    
		    JSONArray arrayModesTransport = null;
			InputStream is = url.openStream();
			
			bytes = IOUtils.toByteArray(is);
			text = new String(bytes, "UTF-8");
			log.warn("Parsing fichier");
			if (text != null && text.trim().startsWith("[") && text.trim().endsWith("]")) {
				log.warn("Fichier GTFS JSON Mode transport conforme");
				
				try {
					arrayModesTransport = new JSONArray(text);
					log.warn(arrayModesTransport.toString());
					for (int i = 0; i < arrayModesTransport.length(); i++) {
						JSONObject transportMode = arrayModesTransport.getJSONObject(i);
						String code = transportMode.optString(CODE, null);
						String mode = transportMode.optString(MODE, null);
						String subMode = transportMode.optString(SUBMODE, null);
						String pivot = transportMode.optString(PIVOT, null);
						String pivotSub = transportMode.optString(PIVOT_SUBMODE, null);

						if (mode != null && subMode != null && pivot != null && pivotSub != null) {
							mapTransportToPivotMode.put(new TransportMode(mode, subMode), new TransportMode(pivot, pivotSub));
							mapPivotToTransportMode.put(new TransportMode(pivot, pivotSub), new TransportMode(mode, subMode));
						}
						
						if (code != null)
							mapCodeToTransportMode.put(new Integer(code), new TransportMode(mode, subMode));
							
					}
				} catch (JSONException e) {
					log.warn("unparsable GTFSTransportMode JSon Object");
				}

			}

		} catch (IOException e) {
			log.warn("Cannot read json file from server");
		}
		
		log.warn("GTFS mapTransportToPivotMode size : " + mapTransportToPivotMode.size());
		log.warn("GTFS mapPivotToTransportMode size : " + mapPivotToTransportMode.size());
		log.warn("mapCodeToTransportMode size : " + mapCodeToTransportMode.size());
	}
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode pivotMode) {
		
		// Search specific mode matching with pivot transport mode
		for (Map.Entry<TransportMode, TransportMode> entry : mapPivotToTransportMode.entrySet())
		{
		    TransportMode entryT = entry.getKey();
		    log.warn(entryT.getMode() + ":" + entryT.getSubMode());
		}
				
		// Search pivot mode matching with gtfs transport mode
		TransportMode tM = mapPivotToTransportMode.get(pivotMode);
		
		if (tM != null)
			return tM;
		
		// If not found set submode to unspecified and start again
		pivotMode.setSubMode("unspecified");
		tM = mapPivotToTransportMode.get(pivotMode);
		
		if (tM != null)
			return tM;	
		
		// If there is no transport mode matching
		return null;

	}
	
	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		log.warn("Appel GTFs specific to generic mode");
		return null;
	}

	public TransportMode fromCodeToPivotTransportMode(Integer code) {
		TransportMode specificTM = mapCodeToTransportMode.get(code);
		TransportMode pivotMode = mapTransportToPivotMode.get(specificTM);
		
		return pivotMode;
	}
	
	public Integer fromPivotTransportModeToCode(TransportMode transportPivotMode) {
		log.warn("GtfsTransportModeConverter pivot mode : " + transportPivotMode.getMode() + ":" + transportPivotMode.getSubMode());
		TransportMode tM = genericToSpecificMode(transportPivotMode);
		log.warn("Transport specific mode : " + tM.getMode() + ":" + tM.getSubMode());
		Integer code = null;
		
		if (tM != null) {
			BiMap<TransportMode, Integer> inverse = mapCodeToTransportMode.inverse();
			code = inverse.get(tM);
		
			if (code != null)
				return code;	
		
		}
		
		// If there is no transport mode matching
		return null;
	}
	
	public Integer fromSpecificTransportModeToCode(TransportMode tM) {
		Integer code = null;
		
		if (tM != null) {
			BiMap<TransportMode, Integer> inverse = mapCodeToTransportMode.inverse();
			code = inverse.get(tM);
		}
		
		return code;
	}
}
