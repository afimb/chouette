package fr.certu.chouette.service.amivif.base;

public class LongLatTypeConverter {

	chouette.schema.types.LongLatTypeType atc(amivif.schema.types.LongLatTypeType amivifStopPointLongLatType) {
		//return chouette.schema.types.LongLatTypeType.valueOf(amivifStopPointLongLatType.toString());
		switch(amivifStopPointLongLatType.getType()) {
		case amivif.schema.types.LongLatTypeType.STANDARD_TYPE :
			return chouette.schema.types.LongLatTypeType.STANDARD;
		case amivif.schema.types.LongLatTypeType.WGS84_TYPE :
			return chouette.schema.types.LongLatTypeType.WGS84;
		case amivif.schema.types.LongLatTypeType.WGS92_TYPE :
			return chouette.schema.types.LongLatTypeType.WGS92;
		}
		return null;
	}
	
	amivif.schema.types.LongLatTypeType cta(chouette.schema.types.LongLatTypeType chouetteStopPointLongLatType) {
		//return amivif.schema.types.LongLatTypeType.valueOf(chouetteStopPointLongLatType.toString());
		if ( chouetteStopPointLongLatType==null) return null;
		
		switch(chouetteStopPointLongLatType.getType()) {
		case chouette.schema.types.LongLatTypeType.STANDARD_TYPE :
			return amivif.schema.types.LongLatTypeType.STANDARD;
		case chouette.schema.types.LongLatTypeType.WGS84_TYPE :
			return amivif.schema.types.LongLatTypeType.WGS84;
		case chouette.schema.types.LongLatTypeType.WGS92_TYPE :
			return amivif.schema.types.LongLatTypeType.WGS92;
		}
		return null;
	}
}
