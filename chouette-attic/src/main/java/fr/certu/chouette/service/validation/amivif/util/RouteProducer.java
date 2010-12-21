package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.Route;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class RouteProducer extends TridentObjectProducer {
	
    public RouteProducer(ValidationException validationException) {
		super(validationException);
	}

	public Route getASG(amivif.schema.Route castorRoute) {
		if (castorRoute == null)
			return null;
		TridentObject tridentObject = super.getASG(castorRoute);
		Route route = new Route();
		route.setTridentObject(tridentObject);
		
		// name optionnel
		route.setName(castorRoute.getName());
		
		// publishedName optionnel
		route.setPublishedName(castorRoute.getPublishedName());
		
		// number optionnel
		route.setNumber(castorRoute.getNumber());
		
		// direction optionnel
		if (castorRoute.getDirection() != null)
		{
			switch (castorRoute.getDirection()) 
			{
				case A:
					route.setDirection(Route.Direction.A);
					break;
				case CLOCKWISE:
					route.setDirection(Route.Direction.ClockWise);
					break;
				case COUNTERCLOCKWISE:
					route.setDirection(Route.Direction.CounterClockWise);
					break;
				case EAST:
					route.setDirection(Route.Direction.East);
					break;
				case NORTH:
					route.setDirection(Route.Direction.North);
					break;
				case NORTHEAST:
					route.setDirection(Route.Direction.NorthEast);
					break;
				case NORTHWEST:
					route.setDirection(Route.Direction.NorthWest);
					break;
				case R:
					route.setDirection(Route.Direction.R);
					break;
				case SOUTH:
					route.setDirection(Route.Direction.South);
					break;
				case SOUTHEAST:
					route.setDirection(Route.Direction.SouthEast);
					break;
				case SOUTHWEST:
					route.setDirection(Route.Direction.SouthWest);
					break;
				case WEST:
					route.setDirection(Route.Direction.West);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidDirection_Route, "La \"Direction\" pour la \"Route\" ("+castorRoute.getObjectId()+") est invalid.");
			}
		}
		// ptLinkId 1..w
		Set<String> aSet = new HashSet<String>();
		String[] castorPtLinkIds = castorRoute.getPtLinkId();
		if ((castorPtLinkIds == null) || (castorPtLinkIds.length < 1))
			getValidationException().add(TypeInvalidite.NoPTLink_Route, "La \"Route\" ("+castorRoute.getObjectId()+") ne possede aucun \"ptLinkId\".");
		else
			for (int i = 0; i < castorPtLinkIds.length; i++)
				if (aSet.add(castorPtLinkIds[i])) {
					try {
						(new TridentObject()).new TridentId(castorPtLinkIds[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"ptLinkId\" d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"ptLinkId\" ("+castorPtLinkIds[i]+") d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est invalid.");
					}
					route.addPTLinkId(castorPtLinkIds[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"ptLinkId\" de la \"Route\" ("+castorRoute.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+castorPtLinkIds[i]+").");
		
		// journeyPatternId 1..w
		aSet = new HashSet<String>();
		String[] castorJourneyPatternIds = castorRoute.getJourneyPatternId();
		if ((castorJourneyPatternIds == null) || (castorJourneyPatternIds.length < 1))
			getValidationException().add(TypeInvalidite.NoJourneyPattern_Route, "La \"Route\" ("+castorRoute.getObjectId()+") ne possede aucun \"journeyPatternId\".");
		else
			for (int i = 0; i < castorJourneyPatternIds.length; i++)
				if (aSet.add(castorJourneyPatternIds[i])) {
					try {
						(new TridentObject()).new TridentId(castorJourneyPatternIds[i]);
					}
					catch(NullPointerException e) {
						getValidationException().add(TypeInvalidite.NullTridentObject, "La \"journeyPatternId\" d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est null.");
					}
					catch(IndexOutOfBoundsException e) {
						getValidationException().add(TypeInvalidite.InvalidTridentObject, "La \"journeyPatternId\" ("+castorJourneyPatternIds[i]+") d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est invalid.");
					}
					route.addJourneyPatternId(castorJourneyPatternIds[i]);
				}
				else
					getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"journeyPatternId\" de la \"Route\" ("+castorRoute.getObjectId().toString()+") contient plusieurs fois le meme identifiant ("+castorJourneyPatternIds[i]+").");
		
		// wayBackRouteId optionnel
		route.setWayBackRouteId(castorRoute.getWayBackRouteId());
		if (route.getWayBackRouteId() != null) {
			try {
				(new TridentObject()).new TridentId(route.getWayBackRouteId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "La \"wayBackRouteId\" d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "La \"wayBackRouteId\" ("+route.getWayBackRouteId()+") d'une \"Route\" ("+castorRoute.getObjectId().toString()+") est invalid.");
			}
		}
		
		// comment optionnel
		route.setComment(castorRoute.getComment());
		
		return route;
	}
}
