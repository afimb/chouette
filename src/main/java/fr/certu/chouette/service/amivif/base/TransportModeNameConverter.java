package fr.certu.chouette.service.amivif.base;

public class TransportModeNameConverter {
	
	public chouette.schema.types.TransportModeNameType atc(amivif.schema.types.TransportModeNameType amivifTransportModeNameType) {
		if (amivifTransportModeNameType == null)
			return null;
		if (amivifTransportModeNameType.toString().trim().equals("Bus"))
			return chouette.schema.types.TransportModeNameType.BUS;
		if (amivifTransportModeNameType.toString().trim().equals("Air")) 
			return chouette.schema.types.TransportModeNameType.AIR;
		if (amivifTransportModeNameType.toString().trim().equals("Bicycle"))
			return chouette.schema.types.TransportModeNameType.BICYCLE;
		if (amivifTransportModeNameType.toString().trim().equals("Coach"))
			return chouette.schema.types.TransportModeNameType.COACH;
		if (amivifTransportModeNameType.toString().trim().equals("Ferry"))
			return chouette.schema.types.TransportModeNameType.FERRY;
		if (amivifTransportModeNameType.toString().trim().equals("LocalTrain"))
			return chouette.schema.types.TransportModeNameType.LOCALTRAIN;
		if (amivifTransportModeNameType.toString().trim().equals("LongDistanceTrain"))
			return chouette.schema.types.TransportModeNameType.LONGDISTANCETRAIN;
		if (amivifTransportModeNameType.toString().trim().equals("Metro"))
			return chouette.schema.types.TransportModeNameType.METRO;
		if (amivifTransportModeNameType.toString().trim().equals("PrivateVehicle"))
			return chouette.schema.types.TransportModeNameType.PRIVATEVEHICLE;
		if (amivifTransportModeNameType.toString().trim().equals("RapidTransit"))
			return chouette.schema.types.TransportModeNameType.RAPIDTRANSIT;
		if (amivifTransportModeNameType.toString().trim().equals("Shuttle"))
			return chouette.schema.types.TransportModeNameType.SHUTTLE;
		if (amivifTransportModeNameType.toString().trim().equals("Taxi"))
			return chouette.schema.types.TransportModeNameType.TAXI;
		if (amivifTransportModeNameType.toString().trim().equals("Train"))
			return chouette.schema.types.TransportModeNameType.TRAIN;
		if (amivifTransportModeNameType.toString().trim().equals("Tramway"))
			return chouette.schema.types.TransportModeNameType.TRAMWAY;
		if (amivifTransportModeNameType.toString().trim().equals("Trolleybus"))
			return chouette.schema.types.TransportModeNameType.TROLLEYBUS;
		if (amivifTransportModeNameType.toString().trim().equals("Val"))
			return chouette.schema.types.TransportModeNameType.VAL;
		if (amivifTransportModeNameType.toString().trim().equals("Walk"))
			return chouette.schema.types.TransportModeNameType.WALK;
		if (amivifTransportModeNameType.toString().trim().equals("Waterborne"))
			return chouette.schema.types.TransportModeNameType.WATERBORNE;
		if (amivifTransportModeNameType.toString().trim().equals("Other"))
			return chouette.schema.types.TransportModeNameType.OTHER;
		return null;
	}
	
	public amivif.schema.types.TransportModeNameType cta(chouette.schema.types.TransportModeNameType chouetteTransportModeNameType) {
		if (chouetteTransportModeNameType == null)
			return null;
		if (chouetteTransportModeNameType.toString().trim().equals("Bus"))
			return amivif.schema.types.TransportModeNameType.BUS;
		if (chouetteTransportModeNameType.toString().trim().equals("Air")) 
			return amivif.schema.types.TransportModeNameType.AIR;
		if (chouetteTransportModeNameType.toString().trim().equals("Bicycle"))
			return amivif.schema.types.TransportModeNameType.BICYCLE;
		if (chouetteTransportModeNameType.toString().trim().equals("Coach"))
			return amivif.schema.types.TransportModeNameType.COACH;
		if (chouetteTransportModeNameType.toString().trim().equals("Ferry"))
			return amivif.schema.types.TransportModeNameType.FERRY;
		if (chouetteTransportModeNameType.toString().trim().equals("LocalTrain"))
			return amivif.schema.types.TransportModeNameType.LOCALTRAIN;
		if (chouetteTransportModeNameType.toString().trim().equals("LongDistanceTrain"))
			return amivif.schema.types.TransportModeNameType.LONGDISTANCETRAIN;
		if (chouetteTransportModeNameType.toString().trim().equals("Metro"))
			return amivif.schema.types.TransportModeNameType.METRO;
		if (chouetteTransportModeNameType.toString().trim().equals("PrivateVehicle"))
			return amivif.schema.types.TransportModeNameType.PRIVATEVEHICLE;
		if (chouetteTransportModeNameType.toString().trim().equals("RapidTransit"))
			return amivif.schema.types.TransportModeNameType.RAPIDTRANSIT;
		if (chouetteTransportModeNameType.toString().trim().equals("Shuttle"))
			return amivif.schema.types.TransportModeNameType.SHUTTLE;
		if (chouetteTransportModeNameType.toString().trim().equals("Taxi"))
			return amivif.schema.types.TransportModeNameType.TAXI;
		if (chouetteTransportModeNameType.toString().trim().equals("Train"))
			return amivif.schema.types.TransportModeNameType.TRAIN;
		if (chouetteTransportModeNameType.toString().trim().equals("Tramway"))
			return amivif.schema.types.TransportModeNameType.TRAMWAY;
		if (chouetteTransportModeNameType.toString().trim().equals("Trolleybus"))
			return amivif.schema.types.TransportModeNameType.TROLLEYBUS;
		if (chouetteTransportModeNameType.toString().trim().equals("Val"))
			return amivif.schema.types.TransportModeNameType.VAL;
		if (chouetteTransportModeNameType.toString().trim().equals("Walk"))
			return amivif.schema.types.TransportModeNameType.WALK;
		if (chouetteTransportModeNameType.toString().trim().equals("Waterborne"))
			return amivif.schema.types.TransportModeNameType.WATERBORNE;
		if (chouetteTransportModeNameType.toString().trim().equals("Other"))
			return amivif.schema.types.TransportModeNameType.OTHER;
		return null;
	}
}
