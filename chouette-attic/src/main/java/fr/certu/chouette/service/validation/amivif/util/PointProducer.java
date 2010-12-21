package fr.certu.chouette.service.validation.amivif.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import fr.certu.chouette.service.validation.amivif.LocationTridentObject;
import fr.certu.chouette.service.validation.amivif.Point;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.Point.Address;
import fr.certu.chouette.service.validation.amivif.Point.PointOfInterest;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class PointProducer extends LocalTridentObjectProducer 
{
	private final Log log = LogFactory.getLog(PointProducer.class);
    private ProjectedPointProducer	projectedPointProducer = new ProjectedPointProducer(getValidationException());
    
    public PointProducer(ValidationException validationException) {
    	super(validationException);
    }

	public Point getASG(amivif.schema.PointTypeType castorPoint) {
		if (castorPoint == null)
			return null;
		
		// TridentObject obligatoire
		LocationTridentObject locationTridentObject = super.getASG(castorPoint);
		Point point = new Point();
		point.setLocationTridentObject(locationTridentObject);
		
		// longitude obligatoire
		if (castorPoint.getLongitude() == null)
			getValidationException().add(TypeInvalidite.NoLongitude_Point, "Le \"StopPoint\" ("+castorPoint.getObjectId()+") n'a pas de \"longitude\".");
		else
			if ((castorPoint.getLongitude().compareTo(new BigDecimal(-180)) >= 0) && (castorPoint.getLongitude().compareTo(new BigDecimal(180)) <= 0))
				point.setLongitude(castorPoint.getLongitude());
			else
				getValidationException().add(TypeInvalidite.InvalidLongitude_Point, "La \"longitude\" du \"Point\" ("+point.getObjectId().toString()+") doit etre entre entre -180 et 180 ("+castorPoint.getLongitude().toString()+").");
		
		// latitude obligatoire
		if (castorPoint.getLatitude() == null)
			getValidationException().add(TypeInvalidite.NoLatitude_Point, "Le \"StopPoint\" ("+castorPoint.getObjectId()+") n'a pas de \"latitude\".");
		else
			if ((castorPoint.getLongitude().compareTo(new BigDecimal(-90)) >= 0) && (castorPoint.getLongitude().compareTo(new BigDecimal(90)) <= 0))
				point.setLatitude(castorPoint.getLatitude());
			else
				getValidationException().add(TypeInvalidite.InvalidLatitude_Point, "La \"latitude\" du \"Point\" ("+point.getObjectId().toString()+") doit etre entre entre -90 et 90 ("+castorPoint.getLatitude().toString()+").");
		
		// longLatType obligatoire
		if (castorPoint.getLongLatType() == null)
		{
			getValidationException().add(TypeInvalidite.NoLongLatType_Point, "Le \"StopPoint\" ("+castorPoint.getObjectId()+") n'a pas de \"longLatType\".");
		}
		else
		{
			log.debug("EVOCASTOR --> point LongLatTypeType determination");
						
			switch (castorPoint.getLongLatType()) 
			{
				case STANDARD:
					point.setLongLatType(Point.LongLatType.Standard);
					break;
				case WGS84:
					point.setLongLatType(Point.LongLatType.WGS84);
					break;
				case WGS92:
					point.setLongLatType(Point.LongLatType.WGS92);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidLongLatType_Point, 
							"Le \"longLatType\" du \"StopPoint\" ("+castorPoint.getObjectId()+") est invalid.");
			}
		}
		// languageCode optionnel
		point.setLanguageCode(castorPoint.getLanguageCode());
		
		// address optionnel
		if (castorPoint.getAddress() != null)
			point.setAddress(getASG(castorPoint.getAddress(), point));
		
		// pointOfInterest optionnel
		if (castorPoint.getPointOfInterest() != null)
			point.setPointOfInterest(getASG(castorPoint.getPointOfInterest(), point));
		
		// projectedPoint optionnel
		if (castorPoint.getProjectedPoint() != null)
			point.setProjectedPoint(projectedPointProducer.getASG(castorPoint.getProjectedPoint()));
		
		// congtainedIn 0..w
		Set<String> aSet = new HashSet<String>();
		String[] castorContainedIns = castorPoint.getContainedIn();
		if (castorContainedIns != null)
			for (int i = 0; i < castorContainedIns.length; i++)
				if (aSet.add(castorContainedIns[i])) {
					try {
						(new TridentObject()).new TridentId(castorContainedIns[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorContainedIns[i]+" est invalid.");
					}
					point.addContainedIn(castorContainedIns[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"containedIn\" du \"Point\" ("+point.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+castorContainedIns[i]+").");
		
		return point;
	}

	private Address getASG(amivif.schema.Address castorAddress, Point point) {
		if (castorAddress == null)
			return null;
		Address address = point.new Address();
		
		// streetName optionnel
		address.setStreetName(castorAddress.getStreetName());
		
		// countryCode optionnel
		address.setCountryCode(castorAddress.getCountryCode());
		
		// province optionnel
		address.setProvince(castorAddress.getProvince());
		
		// region optionne
		address.setRegion(castorAddress.getRegion());
		
		// town optionnel
		address.setTown(castorAddress.getTown());
		
		// roadNumber optionnel
		address.setRoadNumber(castorAddress.getRoadNumber());
		
		// houseNumber optionnel
		address.setHouseNumber(castorAddress.getHouseNumber());
		
		// postalCode obligatoire
		if (castorAddress.getPostalCode() == null)
			getValidationException().add(TypeInvalidite.NoPostalCode_Address, "Le \"postalCode\" de l'\"Adress\" est null.");
		else
			address.setPostalCode(castorAddress.getPostalCode());
		
		return address;
	}

	private PointOfInterest getASG(amivif.schema.PointOfInterest castorPointOfInterest, Point point) {
		if (castorPointOfInterest == null)
			return null;
		PointOfInterest pointOfInterest = point.new PointOfInterest();
		
		// name optionnel
		pointOfInterest.setName(castorPointOfInterest.getName());
		
		// type optionnel
		//if (castorPointOfInterest.getT != null)
		switch (castorPointOfInterest.getType()) 
		{
			case ACCOMMODATIONEATINGANDDRINKING:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.AccommodationEatingAndDrinking);
				break;		
			case ATTRACTION:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.Attraction);
				break;
			case COMMERCIALSERVICES:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.CommercialServices);
				break;
			case EDUCATIONANDHEALTH:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.EducationAndHealth);
				break;
			case MANUFACTURINGANDPRODUCTION:
				pointOfInterest
					.setPointOfInterestType(Point.PointOfInterestType.ManufacturingAndProduction);
				break;
			case PUBLICINFRASTRUCTURE:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.PublicInfrastructure);
				break;
			case RETAIL:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.Retail);
				break;
			case SPORTANDENTERTAINMENT:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.SportAndEntertainment);
				break;
			case TRANSPORT:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.Transport);
				break;
			case WHOLESALE:
				pointOfInterest.setPointOfInterestType(Point.PointOfInterestType.Wholesale);
				break;
			default:
				getValidationException().add(TypeInvalidite.InvalidType_PointOfInterest,
					"Le \"Type\" du \"PointOfInterest\" ("
							+ castorPointOfInterest.getName()
							+ ") est invalid.");
		}
		
		return pointOfInterest;
	}
}
