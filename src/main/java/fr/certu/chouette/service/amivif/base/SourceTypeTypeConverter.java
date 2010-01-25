package fr.certu.chouette.service.amivif.base;

public class SourceTypeTypeConverter {
	
	public chouette.schema.types.SourceTypeType atc(amivif.schema.types.SourceTypeType amivifSourceTypeType) {
		if (amivifSourceTypeType == null)
			return null;
		//EVOCASTOR
		switch(amivifSourceTypeType) 
		{
			case amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL:
				return chouette.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
			case amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE:
				return chouette.schema.types.SourceTypeType.BREAKDOWNSERVICE;
			case amivif.schema.types.SourceTypeType.CAMERAOBSERVATION:
				return chouette.schema.types.SourceTypeType.CAMERAOBSERVATION;
			case amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL:
				return chouette.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
			case amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR:
				return chouette.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
			case amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY:
				return chouette.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
			case amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
			case amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
			case amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
			case amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER:
				return chouette.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
			case amivif.schema.types.SourceTypeType.OTHERINFORMATION:
				return chouette.schema.types.SourceTypeType.OTHERINFORMATION;
			case amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE:
				return chouette.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
			case amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY:
				return chouette.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
			case amivif.schema.types.SourceTypeType.POLICEPATROL:
				return chouette.schema.types.SourceTypeType.POLICEPATROL;
			case amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES:
				return chouette.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
			case amivif.schema.types.SourceTypeType.PUBLICTRANSPORT:
				return chouette.schema.types.SourceTypeType.PUBLICTRANSPORT;
			case amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER:
				return chouette.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
			case amivif.schema.types.SourceTypeType.ROADAUTHORITIES:
				return chouette.schema.types.SourceTypeType.ROADAUTHORITIES;
			case amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER:
				return chouette.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
			case amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
			case amivif.schema.types.SourceTypeType.TRANSITOPERATOR:
				return chouette.schema.types.SourceTypeType.TRANSITOPERATOR;
			case amivif.schema.types.SourceTypeType.TRAVELAGENCY:
				return chouette.schema.types.SourceTypeType.TRAVELAGENCY;
			case amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER:
				return chouette.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
			case amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT:
				return chouette.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
			case amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}
	
	public amivif.schema.types.SourceTypeType cta(chouette.schema.types.SourceTypeType chouetteSourceTypeType) {
		if (chouetteSourceTypeType == null)
			return null;
		switch(chouetteSourceTypeType) {
		case chouette.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL:
			return amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
		case chouette.schema.types.SourceTypeType.BREAKDOWNSERVICE:
			return amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE;
		case chouette.schema.types.SourceTypeType.CAMERAOBSERVATION:
			return amivif.schema.types.SourceTypeType.CAMERAOBSERVATION;
		case chouette.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL:
			return amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
		case chouette.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR:
			return amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
		case chouette.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY:
			return amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
		case chouette.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION:
			return amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION:
			return amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION:
			return amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.MOBILETELEPHONECALLER:
			return amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
		case chouette.schema.types.SourceTypeType.OTHERINFORMATION:
			return amivif.schema.types.SourceTypeType.OTHERINFORMATION;
		case chouette.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE:
			return amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
		case chouette.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY:
			return amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
		case chouette.schema.types.SourceTypeType.POLICEPATROL:
			return amivif.schema.types.SourceTypeType.POLICEPATROL;
		case chouette.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES:
			return amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
		case chouette.schema.types.SourceTypeType.PUBLICTRANSPORT:
			return amivif.schema.types.SourceTypeType.PUBLICTRANSPORT;
		case chouette.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER:
			return amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
		case chouette.schema.types.SourceTypeType.ROADAUTHORITIES:
			return amivif.schema.types.SourceTypeType.ROADAUTHORITIES;
		case chouette.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER:
			return amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
		case chouette.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION:
			return amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.TRANSITOPERATOR:
			return amivif.schema.types.SourceTypeType.TRANSITOPERATOR;
		case chouette.schema.types.SourceTypeType.TRAVELAGENCY:
			return amivif.schema.types.SourceTypeType.TRAVELAGENCY;
		case chouette.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER:
			return amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
		case chouette.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT:
			return amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
		case chouette.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION:
			return amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}

}
