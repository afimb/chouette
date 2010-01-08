package fr.certu.chouette.service.amivif.base;

public class SourceTypeTypeConverter {
	
	public chouette.schema.types.SourceTypeType atc(amivif.schema.types.SourceTypeType amivifSourceTypeType) {
		if (amivifSourceTypeType == null)
			return null;
		switch(amivifSourceTypeType.getType()) {
		case amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL_TYPE :
			return chouette.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
		case amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE_TYPE:
			return chouette.schema.types.SourceTypeType.BREAKDOWNSERVICE;
		case amivif.schema.types.SourceTypeType.CAMERAOBSERVATION_TYPE:
			return chouette.schema.types.SourceTypeType.CAMERAOBSERVATION;
		case amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL_TYPE:
			return chouette.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
		case amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR_TYPE:
			return chouette.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
		case amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY_TYPE:
			return chouette.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
		case amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION_TYPE:
			return chouette.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
		case amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION_TYPE:
			return chouette.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
		case amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION_TYPE:
			return chouette.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
		case amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER_TYPE:
			return chouette.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
		case amivif.schema.types.SourceTypeType.OTHERINFORMATION_TYPE:
			return chouette.schema.types.SourceTypeType.OTHERINFORMATION;
		case amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE_TYPE:
			return chouette.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
		case amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY_TYPE:
			return chouette.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
		case amivif.schema.types.SourceTypeType.POLICEPATROL_TYPE:
			return chouette.schema.types.SourceTypeType.POLICEPATROL;
		case amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES_TYPE:
			return chouette.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
		case amivif.schema.types.SourceTypeType.PUBLICTRANSPORT_TYPE:
			return chouette.schema.types.SourceTypeType.PUBLICTRANSPORT;
		case amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER_TYPE:
			return chouette.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
		case amivif.schema.types.SourceTypeType.ROADAUTHORITIES_TYPE:
			return chouette.schema.types.SourceTypeType.ROADAUTHORITIES;
		case amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER_TYPE:
			return chouette.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
		case amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION_TYPE:
			return chouette.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
		case amivif.schema.types.SourceTypeType.TRANSITOPERATOR_TYPE:
			return chouette.schema.types.SourceTypeType.TRANSITOPERATOR;
		case amivif.schema.types.SourceTypeType.TRAVELAGENCY_TYPE:
			return chouette.schema.types.SourceTypeType.TRAVELAGENCY;
		case amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER_TYPE:
			return chouette.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
		case amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT_TYPE:
			return chouette.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
		case amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION_TYPE:
			return chouette.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}
	
	public amivif.schema.types.SourceTypeType cta(chouette.schema.types.SourceTypeType chouetteSourceTypeType) {
		if (chouetteSourceTypeType == null)
			return null;
		switch(chouetteSourceTypeType.getType()) {
		case chouette.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL_TYPE :
			return amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
		case chouette.schema.types.SourceTypeType.BREAKDOWNSERVICE_TYPE:
			return amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE;
		case chouette.schema.types.SourceTypeType.CAMERAOBSERVATION_TYPE:
			return amivif.schema.types.SourceTypeType.CAMERAOBSERVATION;
		case chouette.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL_TYPE:
			return amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
		case chouette.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR_TYPE:
			return amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
		case chouette.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY_TYPE:
			return amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
		case chouette.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION_TYPE:
			return amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION_TYPE:
			return amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION_TYPE:
			return amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.MOBILETELEPHONECALLER_TYPE:
			return amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
		case chouette.schema.types.SourceTypeType.OTHERINFORMATION_TYPE:
			return amivif.schema.types.SourceTypeType.OTHERINFORMATION;
		case chouette.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE_TYPE:
			return amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
		case chouette.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY_TYPE:
			return amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
		case chouette.schema.types.SourceTypeType.POLICEPATROL_TYPE:
			return amivif.schema.types.SourceTypeType.POLICEPATROL;
		case chouette.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES_TYPE:
			return amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
		case chouette.schema.types.SourceTypeType.PUBLICTRANSPORT_TYPE:
			return amivif.schema.types.SourceTypeType.PUBLICTRANSPORT;
		case chouette.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER_TYPE:
			return amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
		case chouette.schema.types.SourceTypeType.ROADAUTHORITIES_TYPE:
			return amivif.schema.types.SourceTypeType.ROADAUTHORITIES;
		case chouette.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER_TYPE:
			return amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
		case chouette.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION_TYPE:
			return amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
		case chouette.schema.types.SourceTypeType.TRANSITOPERATOR_TYPE:
			return amivif.schema.types.SourceTypeType.TRANSITOPERATOR;
		case chouette.schema.types.SourceTypeType.TRAVELAGENCY_TYPE:
			return amivif.schema.types.SourceTypeType.TRAVELAGENCY;
		case chouette.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER_TYPE:
			return amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
		case chouette.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT_TYPE:
			return amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
		case chouette.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION_TYPE:
			return amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}

}
