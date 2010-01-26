package fr.certu.chouette.service.amivif.base;

public class LongLatTypeConverter {

	chouette.schema.types.LongLatTypeType atc(amivif.schema.types.LongLatTypeType amivifStopPointLongLatType) {
		//return chouette.schema.types.LongLatTypeType.fromValue(amivifStopPointLongLatType.toString());
		switch(amivifStopPointLongLatType) 
		{
			case STANDARD:
				return chouette.schema.types.LongLatTypeType.STANDARD;
			case WGS84:
				return chouette.schema.types.LongLatTypeType.WGS84;
			case WGS92:
				return chouette.schema.types.LongLatTypeType.WGS92;
		}
		return null;
	}
	
	amivif.schema.types.LongLatTypeType cta(chouette.schema.types.LongLatTypeType chouetteStopPointLongLatType) {
		//return amivif.schema.types.LongLatTypeType.fromValue(chouetteStopPointLongLatType.toString());
		if ( chouetteStopPointLongLatType==null) return null;
		
		switch(chouetteStopPointLongLatType) 
		{
			case STANDARD:
				return amivif.schema.types.LongLatTypeType.STANDARD;
			case WGS84:
				return amivif.schema.types.LongLatTypeType.WGS84;
			case WGS92:
				return amivif.schema.types.LongLatTypeType.WGS92;
		}
		return null;
	}
}
