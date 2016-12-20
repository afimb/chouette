package mobi.chouette.exchange.hub;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.AbstractTransportModeConverter;
import mobi.chouette.exchange.TransportModeConverter;
import mobi.chouette.exchange.TransportModeConverterFactory;

public class HubTransportModeConverter extends AbstractTransportModeConverter{
	
	private static final String transportModeUrlSrc = "https://github.com/afimb/chouette-projects-i18n/tree/master/data/transport_mode/hub.json";

	private HubTransportModeConverter(){

	}

	private static HubTransportModeConverter INSTANCE = null;
	
	@Getter
	private static List<TransportMode> listTransportMode = null;

	public static synchronized HubTransportModeConverter getInstance(){
		
		if(INSTANCE == null){
			getTransportModeListFromJSONFile(transportModeUrlSrc);
			INSTANCE = new HubTransportModeConverter();
		}
		
		return INSTANCE;
	}
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode pivotMode) {
		
		// Search pivot mode matching with hub transport mode
		TransportMode tM = mapPivotToTransportMode.get(pivotMode);
		
		if (tM != null)
			return tM;
		
		// If not found set submode to unspecified and start again
		pivotMode.setSubMode("unspecified");
		tM = mapPivotToTransportMode.get(pivotMode);
		
		return tM;	
	
	}
	
	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		// Search specific mode matching with pivot transport mode
		TransportMode tM = mapTransportToPivotMode.get(specificMode);
		
		return tM;	
	}

	public static class DefaultFactory extends TransportModeConverterFactory {

		@Override
		protected TransportModeConverter create() throws IOException {
			TransportModeConverter result = new HubTransportModeConverter();
			return result;
		}
	}

	static {
		TransportModeConverterFactory.factories.put("Hub", new DefaultFactory());
	}
}
