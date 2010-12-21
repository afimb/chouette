package fr.certu.chouette.service.amivif.base;

public class SourceTypeTypeConverter 
{
	
	public chouette.schema.types.SourceTypeType atc(amivif.schema.types.SourceTypeType amivifSourceTypeType) 
	{
		if (amivifSourceTypeType == null)
		{
			return null;
		}
		//EVOCASTOR
		switch(amivifSourceTypeType) 
		{
			case AUTOMOBILECLUBPATROL:
				return chouette.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
			case BREAKDOWNSERVICE:
				return chouette.schema.types.SourceTypeType.BREAKDOWNSERVICE;
			case CAMERAOBSERVATION:
				return chouette.schema.types.SourceTypeType.CAMERAOBSERVATION;
			case EMERGENCYSERVICEPATROL:
				return chouette.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
			case FREIGHTVEHICLEOPERATOR:
				return chouette.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
			case INDIVIDUALSUBJECTOFTRAVELITINERARY:
				return chouette.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
			case INDUCTIONLOOPMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
			case INFRAREDMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
			case MICROWAVEMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
			case MOBILETELEPHONECALLER:
				return chouette.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
			case OTHERINFORMATION:
				return chouette.schema.types.SourceTypeType.OTHERINFORMATION;
			case OTHEROFFICIALVEHICLE:
				return chouette.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
			case PASSENGERTRANSPORTCOORDINATINGAUTHORITY:
				return chouette.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
			case POLICEPATROL:
				return chouette.schema.types.SourceTypeType.POLICEPATROL;
			case PUBLICANDPRIVATEUTILITIES:
				return chouette.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
			case PUBLICTRANSPORT:
				return chouette.schema.types.SourceTypeType.PUBLICTRANSPORT;
			case REGISTEREDMOTORISTOBSERVER:
				return chouette.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
			case ROADAUTHORITIES:
				return chouette.schema.types.SourceTypeType.ROADAUTHORITIES;
			case ROADSIDETELEPHONECALLER:
				return chouette.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
			case TRAFFICMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
			case TRANSITOPERATOR:
				return chouette.schema.types.SourceTypeType.TRANSITOPERATOR;
			case TRAVELAGENCY:
				return chouette.schema.types.SourceTypeType.TRAVELAGENCY;
			case TRAVELINFORMATIONSERVICEPROVIDER:
				return chouette.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
			case VEHICLEPROBEMEASUREMENT:
				return chouette.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
			case VIDEOPROCESSINGMONITORINGSTATION:
				return chouette.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}
	
	public amivif.schema.types.SourceTypeType cta(chouette.schema.types.SourceTypeType chouetteSourceTypeType) {
		
		if (chouetteSourceTypeType == null)
		{
			return null;
		}
		
		switch(chouetteSourceTypeType) 
		{
			case AUTOMOBILECLUBPATROL:
				return amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL;
			case BREAKDOWNSERVICE:
				return amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE;
			case CAMERAOBSERVATION:
				return amivif.schema.types.SourceTypeType.CAMERAOBSERVATION;
			case EMERGENCYSERVICEPATROL:
				return amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL;
			case FREIGHTVEHICLEOPERATOR:
				return amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR;
			case INDIVIDUALSUBJECTOFTRAVELITINERARY:
				return amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY;
			case INDUCTIONLOOPMONITORINGSTATION:
				return amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION;
			case INFRAREDMONITORINGSTATION:
				return amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION;
			case MICROWAVEMONITORINGSTATION:
				return amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION;
			case MOBILETELEPHONECALLER:
				return amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER;
			case OTHERINFORMATION:
				return amivif.schema.types.SourceTypeType.OTHERINFORMATION;
			case OTHEROFFICIALVEHICLE:
				return amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE;
			case PASSENGERTRANSPORTCOORDINATINGAUTHORITY:
				return amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY;
			case POLICEPATROL:
				return amivif.schema.types.SourceTypeType.POLICEPATROL;
			case PUBLICANDPRIVATEUTILITIES:
				return amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES;
			case PUBLICTRANSPORT:
				return amivif.schema.types.SourceTypeType.PUBLICTRANSPORT;
			case REGISTEREDMOTORISTOBSERVER:
				return amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER;
			case ROADAUTHORITIES:
				return amivif.schema.types.SourceTypeType.ROADAUTHORITIES;
			case ROADSIDETELEPHONECALLER:
				return amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER;
			case TRAFFICMONITORINGSTATION:
				return amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION;
			case TRANSITOPERATOR:
				return amivif.schema.types.SourceTypeType.TRANSITOPERATOR;
			case TRAVELAGENCY:
				return amivif.schema.types.SourceTypeType.TRAVELAGENCY;
			case TRAVELINFORMATIONSERVICEPROVIDER:
				return amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER;
			case VEHICLEPROBEMEASUREMENT:
				return amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT;
			case VIDEOPROCESSINGMONITORINGSTATION:
				return amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION;
		}
		return null;
	}

}
