package mobi.chouette.exchange.gtfs;

import java.nio.file.Path;
import java.util.List;

import lombok.Getter;
import mobi.chouette.common.TransportMode;
import mobi.chouette.exchange.AbstractTransportModeConverter;

public class GtfsTransportModeConverter extends AbstractTransportModeConverter{

	private GtfsTransportModeConverter(){

	}

	private static GtfsTransportModeConverter INSTANCE = null;
	
	@Getter
	private static List<TransportMode> listTransportMode = null;

	public static synchronized GtfsTransportModeConverter getInstance(){
		
		if(INSTANCE == null){
			listTransportMode = getTransportModeListFromJSONFile(transportModePath);
			INSTANCE = new GtfsTransportModeConverter();
		}
		
		return INSTANCE;
	}

	@Override
	public TransportMode importModeToSpecificMode(TransportMode importMode) {
		// Iterate on gtfs transport mode list
		for(TransportMode tM : listTransportMode) {
			//If pivot mode and pivot submode are same
			if (tM.getPivotMode().equalsIgnoreCase(importMode.getPivotMode())
					&& tM.getPivotSubMode().equalsIgnoreCase(importMode.getPivotSubMode()))
				return tM;
		}
		
		ExtendedGtfsTransportModeConverter egtMC = ExtendedGtfsTransportModeConverter.getInstance();
		
		// Iterate on extended gtfs transport mode list
		for(TransportMode tM : egtMC.getListTransportMode()) {
			//If pivot mode and pivot submode are same
			if (tM.getPivotMode().equalsIgnoreCase(importMode.getPivotMode())
					&& tM.getPivotSubMode().equalsIgnoreCase(importMode.getPivotSubMode()))
				return tM;
		}
		
		// If there is no transport mode matching
		return new TransportMode("exclu", "undefined", importMode.getPivotMode(), importMode.getPivotSubMode());

	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		return null;
	}
}
