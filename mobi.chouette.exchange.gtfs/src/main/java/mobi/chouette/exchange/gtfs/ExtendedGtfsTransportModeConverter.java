package mobi.chouette.exchange.gtfs;

import java.util.List;

import lombok.Getter;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.AbstractTransportModeConverter;

public class ExtendedGtfsTransportModeConverter extends AbstractTransportModeConverter{
	private ExtendedGtfsTransportModeConverter(){

	}

	private static ExtendedGtfsTransportModeConverter INSTANCE = null;
	
	@Getter
	private static List<TransportMode> listTransportMode = null;

	public static synchronized ExtendedGtfsTransportModeConverter getInstance(){
		if(INSTANCE == null){
			listTransportMode = getTransportModeListFromJSONFile(transportModePath);
			INSTANCE = new ExtendedGtfsTransportModeConverter();
		}
		
		return INSTANCE;
	}

	@Override
	public TransportMode importModeToSpecificMode(TransportMode importMode) {
		return null;
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		return null;
	}
}
