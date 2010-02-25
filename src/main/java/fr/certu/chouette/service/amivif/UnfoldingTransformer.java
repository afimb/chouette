package fr.certu.chouette.service.amivif;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouetteRoute;
import chouette.schema.JourneyPattern;
import chouette.schema.PtLink;
import chouette.schema.StopArea;
import chouette.schema.StopAreaExtension;
import chouette.schema.StopPoint;
import chouette.schema.VehicleJourney;
import chouette.schema.VehicleJourneyAtStop;
import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.service.amivif.util.Paire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class UnfoldingTransformer 
{
    private static final Logger logger = Logger.getLogger( UnfoldingTransformer.class);
	private ChouetteLineDescription lineDescription;
	private ChouetteArea chouetteArea;
	
	// table id troncon initial -> paire( id arret physique depart, id arret physique arrivée)
	private Map<String, Paire> phyArretsParTroncon = new Hashtable<String, Paire>();
	// table id itinéraire -> liste des arrets physiques parcourus  (si un arret physique est parcouru n fois, les n fois sont comptabilisées)
	private Map<String, List<String>> phyArretsParItineraire = new Hashtable<String, List<String>>();

	// table paire (id itinéraire, id arret physique initial) -> liste des positions occupées sur l'itinéraire
	private Map<Paire, List<Integer>> positionsParItinerairePhyArret = new Hashtable<Paire, List<Integer>>();
	// table paire (id itinéraire, position sur l'itineraire) -> id arret physique correspondant
	private Map<Paire, String> phyArretParItinerairePosition = new Hashtable<Paire, String>();
	// table id itinéraire -> total de passages aux arrets (si un arret physique est parcouru n fois, les n fois sont comptabilisées)
	private Map<String, Integer> totalPhyArretParItineraire = new Hashtable<String, Integer>();

	// table dictionnaire id troncon initial -> structure troncon initial
	private Map<String, PtLink> tronconParObjectId = new Hashtable<String, PtLink>();
	// table dictionnaire id arret arret physique initial -> structure arret physique initial
	private Map<String, StopPoint> phyArretParObjectId = new Hashtable<String, StopPoint>();
	
	/**
	 * Transformation de la ligne XML
	 * 
	 * Precondition: le membre lineDescription est défini, et modélise une ligne avec des StopPoint
	 * comme des arrets physiques (un même Stoppoint plusieurs fois sur un ou des itinéraires) 
	 * 
	 * Postcondition: le membre lineDescription est défini et modélise une ligne avec des StopPoint
	 * comme des arrets sur itinéraires (chaque StopPoint ne figure qu'une et seule fois sur un et un seul itinéraire)
	 */
	public void transform()
	{
		initialise();
		transformPtLink();
		transformRoute();
		transformRouteStopPoint();
		transformVehicle();
		transformJourneyPattern();
		transformStopArea();
	}
	
	/**
	 * Au départ les StopPoint modélisent des arrets physiques (et non des arrets sur itinéraire)
	 * Un même StopPoint peut figurer plusieurs fois sur le même itinéraire, ou sur des itinéraires différents
	 * 
	 * 
	 * Lecture du fichier XML (sans mise à jour)
	 * et contruction des variables membres.
	 * 
	 * Invariant: la ligne XML (membre lineDescription)
	 * 
	 */
	private void initialise()
	{
		phyArretsParTroncon.clear();
		phyArretsParItineraire.clear();
		positionsParItinerairePhyArret.clear();
		phyArretParItinerairePosition.clear();
		totalPhyArretParItineraire.clear();
		tronconParObjectId.clear();
		phyArretParObjectId.clear();
		
		// parcours des arrets physiques
		int totalArretsPhysiques = lineDescription.getStopPointCount();
		for (int i = 0; i < totalArretsPhysiques; i++) 
		{
			StopPoint stopPoint = lineDescription.getStopPoint( i);
			phyArretParObjectId.put( stopPoint.getObjectId(), stopPoint);
		}
		
		// parcours des troncons
		int totalTroncons = lineDescription.getPtLinkCount();
		for (int i = 0; i < totalTroncons; i++) 
		{
			PtLink ptLink = lineDescription.getPtLink( i);
			phyArretsParTroncon.put( ptLink.getObjectId(), new Paire( ptLink.getStartOfLink(), ptLink.getEndOfLink()));
			tronconParObjectId.put( ptLink.getObjectId(), ptLink);
		}
		
		// parcours des itineraires
		int totalItineraires = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute route = lineDescription.getChouetteRoute(i);
			List<String> phyArrets = new ArrayList<String>();
			String routeId = route.getObjectId();
			phyArretsParItineraire.put( routeId, phyArrets);
			int totalItiTroncons = route.getPtLinkIdCount();
			
			for (int j = 0; j < totalItiTroncons; j++) 
			{
				if ( j==0) 
				{ 
					Paire paireInitiale = phyArretsParTroncon.get( route.getPtLinkId( 0));
					phyArrets.add( paireInitiale.getPremier());
				}
				Paire paireInitiale = phyArretsParTroncon.get( route.getPtLinkId( j));
				phyArrets.add( paireInitiale.getSecond());
			}
		}
		
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute route = lineDescription.getChouetteRoute(i);
			String routeId = route.getObjectId();
			List<String> phyArrets = phyArretsParItineraire.get( routeId);
			int totalPhyArrets = phyArrets.size();
			
			totalPhyArretParItineraire.put( routeId, new Integer( totalPhyArrets));
			
			for (int j = 0; j < totalPhyArrets; j++) 
			{
				String phyArret = phyArrets.get( j);
				
				Paire itinerairePhyArret = new Paire( routeId, phyArret);
				List<Integer> positions = positionsParItinerairePhyArret.get( itinerairePhyArret);
				if ( positions==null)
				{
					positions = new ArrayList<Integer>();
					positionsParItinerairePhyArret.put( itinerairePhyArret, positions);
				}
				positions.add( new Integer( j));
				
				Paire itinerairePosition = new Paire( routeId, String.valueOf( j));
				phyArretParItinerairePosition.put( itinerairePosition, phyArret);
			}
		}
	}
	
	/**
	 * Transformation de la liste des troncons (PtLink)
	 * 
	 * Precondition: la séquence des troncons de chaque itineraire ne doit pas avoir ete transformee
	 * 
	 * Invariant: Les variables membres Map (accédes uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des troncons
	 */
	private void transformPtLink()
	{
		lineDescription.setPtLink( new PtLink[ 0]);
		
		int totalItineraires = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute route = lineDescription.getChouetteRoute(i);
			String routeId = route.getObjectId();
			
			int totalPhyArrets = totalPhyArretParItineraire.get( routeId).intValue();
			String arretPrecedent = phyArretParItinerairePosition.get( new Paire( routeId, String.valueOf( 0)));
			
			logger.debug( "iti="+routeId+", total arrets="+totalPhyArrets);
			
			for (int j = 1; j < totalPhyArrets; j++) 
			{
				String arret = phyArretParItinerairePosition.get( new Paire( routeId, String.valueOf( j)));
				
				PtLink unrolledPtLink = new PtLink();
				String idItnitial = route.getPtLinkId( j-1);
				unrolledPtLink.setObjectId( getIdComplete( idItnitial, routeId, j-1));
				unrolledPtLink.setStartOfLink( getIdComplete( arretPrecedent, routeId, j-1));
				unrolledPtLink.setEndOfLink( getIdComplete( arret, routeId, j));
				
				arretPrecedent = arret;
				lineDescription.addPtLink( unrolledPtLink);
			}
		}
	}
	
	/**
	 * Transformation de la liste des arrets d'itinéraire (StopPoint)
	 * 
	 * Invariant: Les variables membres Map (accédes uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des arrets d'itinéraire
	 */
	private void transformRouteStopPoint()
	{
		List<StopPoint> unrolledRouteStopPoints = new ArrayList<StopPoint>();
		int totalItineraires = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute route = lineDescription.getChouetteRoute(i);
			String routeId = route.getObjectId();
			
			int totalPhyArrets = totalPhyArretParItineraire.get( routeId).intValue();
			
			for (int j = 0; j < totalPhyArrets; j++) 
			{
				String phyStopId = phyArretParItinerairePosition.get( new Paire( routeId, String.valueOf( j)));
				StopPoint phyArret = phyArretParObjectId.get( phyStopId);
				StopPoint unrolledStoppPoint = new StopPoint();
				
				try{ BeanUtils.copyProperties( unrolledStoppPoint, phyArret); }
				catch( Exception e){ logger.error( e.getMessage(),e );}
				
				unrolledStoppPoint.setObjectVersion( 1);
				unrolledStoppPoint.setObjectId( getIdComplete( phyStopId, routeId, j));
				unrolledRouteStopPoints.add( unrolledStoppPoint);
			}
		}
		lineDescription.setStopPoint( unrolledRouteStopPoints.toArray( new StopPoint[ 0]));
	}
	
	/**
	 * Transformation de la liste des itinéraires (Route)
	 * 
	 * Invariant: Les variables membres Map (accédées uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des itinéraires
	 */
	private void transformRoute()
	{
		int totalItineraires = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute route = lineDescription.getChouetteRoute(i);
			String routeId = route.getObjectId();
			List<String> unrolledPtLinks = new ArrayList<String>();
			
			int totalPhyArrets = totalPhyArretParItineraire.get( routeId).intValue();
			
			for (int j = 1; j < totalPhyArrets; j++) 
			{
				String idItnitial = route.getPtLinkId( j-1);
				unrolledPtLinks.add( getIdComplete( idItnitial, routeId, j-1));
			}
			route.setPtLinkId( unrolledPtLinks.toArray( new String[ 0]));
		}
	}
	
	/**
	 * Transformation de la liste des arrets physiques
	 * de façon ce qu'ils pointent leurs arrêts d'itinéraire
	 * 
	 * Invariant: Les variables membres Map (accédées uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des arrêts physiques
	 * 
	 */
	private void transformStopArea()
	{
		Set<String> itineraires = phyArretsParItineraire.keySet();
		
		int totalStopArea = chouetteArea.getStopAreaCount();
		for (int i = 0; i < totalStopArea; i++) 
		{
			StopArea stopArea = chouetteArea.getStopArea( i);
			
			// filtrer les arrets physiques seulement
			StopAreaExtension extension = stopArea.getStopAreaExtension();
			if ( extension!=null && ( ChouetteAreaType.BOARDINGPOSITION.equals( extension.getAreaType())
									|| ChouetteAreaType.QUAY.equals( extension.getAreaType())))
			{
				String physiqueId = stopArea.getObjectId();
				for (String itineraire : itineraires) 
				{
					List<Integer> positons = positionsParItinerairePhyArret.get( new Paire( itineraire, physiqueId));
					
					if ( positons!=null)
					{
						for (Integer position : positons) 
						{
							stopArea.addContains( getIdComplete( physiqueId, itineraire, position.intValue()));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Transformation de la liste des courses (VehicleJourney)
	 * 
	 * Invariant: Les variables membres Map (accédées uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des courses
	 */
	private void transformVehicle()
	{
		int totalCourses = lineDescription.getVehicleJourneyCount();
		for (int i = 0; i < totalCourses; i++) 
		{
			VehicleJourney vehicle = lineDescription.getVehicleJourney( i);
			String routeId = vehicle.getRouteId();
			
			int totalHoraires = vehicle.getVehicleJourneyAtStopCount();
			int position = -1; // conserve la position sur itinéraire du dernier horaire transformé
			
			// parcours des horaires à transformer
			for (int j = 0; j < totalHoraires; j++) 
			{
				VehicleJourneyAtStop horaire = vehicle.getVehicleJourneyAtStop( j);
				
				// accès à l'arret physique associé à l'horaire
				String phyArret = horaire.getStopPointId();
				
				// accès à la liste (ordonnée) des positions occupées par l'arret physique
				List<Integer> positionsPhyArret = positionsParItinerairePhyArret.get(
						new Paire( routeId, phyArret));
				if ( positionsPhyArret==null)
				{
					throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_VEHICLEJOURNEYSTOPPOINTMISMATCH,vehicle.getObjectId(),routeId,phyArret);
				}
				
				String unrolledStopId = null;
				
				// parcours de toutes les positions occupées (par ordre croissant)
				for (Integer positionUnrolledStop : positionsPhyArret) 
				{
					// si la position occupée courante est placée après celle du dernier horaire transformé
					// on l'enregistre et on sort de la boucle
					if ( position<positionUnrolledStop.intValue())
					{
						unrolledStopId = getIdComplete( phyArret, routeId, positionUnrolledStop.intValue());
						position = positionUnrolledStop.intValue();
						break;
					}
				}
				
				// tester si on a trouvé une position placée celle du dernier horaire transformé
				if ( unrolledStopId==null)
				{
					throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_VEHICLEJOURNEYMISMATCH,vehicle.getObjectId(),routeId);
				}
				horaire.setStopPointId( unrolledStopId);
			}
		}
	}
	
	/**
	 * Transformation de la liste des missions (JourneyPattern)
	 * 
	 * Invariant: Les variables membres Map (accédées uniquement en consultation)
	 * 
	 * Postcondition: lineDescription contient la liste transformée des missions
	 */
	private void transformJourneyPattern()
	{
		int totalMissions = lineDescription.getJourneyPatternCount();
		for (int i = 0; i < totalMissions; i++) 
		{
			JourneyPattern journey = lineDescription.getJourneyPattern(i);
			String routeId = journey.getRouteId();
			
			int totalArrets = journey.getStopPointListCount();
			int position = -1; // conserve la position sur itinéraire du dernier arrêt transformé
			
			// parcours des arrêt à transformer
			for (int j = 0; j < totalArrets; j++) 
			{
				// accès à l'arret physique associé 
				String phyArret = journey.getStopPointList( j);
				
				// accès à la liste (ordonnée) des positions occupées par l'arret physique
				List<Integer> positionsPhyArret = positionsParItinerairePhyArret.get(
						new Paire( routeId, phyArret));
				if ( positionsPhyArret==null)
				{
					throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_JOURNEYPATTERNSTOPPOINTMISMATCH,journey.getObjectId(),routeId,phyArret);
				}
				
				String unrolledStopId = null;
				
				// parcours de toutes les positions occupées (par ordre croissant)
				for (Integer positionUnrolledStop : positionsPhyArret) 
				{
					// si la position occupée courante est placée après celle du dernier horaire transformé
					// on l'enregistre et on sort de la boucle
					if ( position<positionUnrolledStop.intValue())
					{
						unrolledStopId = getIdComplete( phyArret, routeId, positionUnrolledStop.intValue());
						position = positionUnrolledStop.intValue();
						break;
					}
				}
				
				// tester si on a trouvé une position placée celle du dernier horaire transformé
				if ( unrolledStopId==null)
				{
					throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_JOURNEYPATTERNMISMATCH,journey.getObjectId(),routeId);
				}
				journey.setStopPointList( j, unrolledStopId);
			}
		}
	}
	
	private String getIdComplete( String premier, String complement, int position)
	{
		String[] tblObjectId = premier.split( ":");
		String[] tblRacine = complement.split( ":");
		
		if ( tblRacine.length!=3) throw new IllegalArgumentException( "Identifiant "+premier+" non valide");
		if ( tblObjectId.length!=3) throw new IllegalArgumentException( "Identifiant "+complement+" non valide");
		
		StringBuffer buf = new StringBuffer( tblObjectId[ 0]);
		buf.append( ":");
		buf.append( tblObjectId[ 1]);
		buf.append( ":");
		buf.append( tblRacine[ 2]);
		buf.append( "A");
		buf.append( position);
		buf.append( "A");
		buf.append( tblObjectId[ 2]);
		
		return buf.toString();
	}	
	private Map<Paire, Queue<VehicleJourneyAtStop>> vehicleJourneyAtStopParRouteIdEtStopPointId = new Hashtable<Paire, Queue<VehicleJourneyAtStop>>();
	/****************************************************************************
	 * La liste (qui est normalement ordonnée) des PtLink des itinéraires sera  *
	 * remplacée par une liste équivalente sans doublants. Pour cela on ajoute  *
	 * à chaque identifiant d'arrêt de tronçon la partie code de l'identifiant  *
	 * de l'itinéraire et le numéro d'ordre d'apparaition du tronçon dans       *
	 * l'itinéraire. On modifie aussi les deux bouts de chaque tronçon          *
	 * startOfLink et endOflink de la même manière. Par la même occasion on     *
	 * reporte cette transformation sur les arrêts physiques (StopPoint) et sur *
	 * les courses (VehiculeJourney).                                           *
	 ***************************************************************************/
	public void transformPtLink_Route_StopPoint_VehicleJourney() {
		List<PtLink> 	ptLinks 	= new ArrayList<PtLink>();
		List<StopPoint>	stopPoints	= new ArrayList<StopPoint>();
		int numberOfChouetteRoutes = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < numberOfChouetteRoutes; i++) {
			ChouetteRoute chouetteRoute = lineDescription.getChouetteRoute(i);
			String chouetteRouteId = chouetteRoute.getObjectId();
			int numberOfChouetteRoutePtLinks = chouetteRoute.getPtLinkIdCount();
			List<String> chouetteRoutePtLinks = new ArrayList<String>();
			for (int j = 0; j < numberOfChouetteRoutePtLinks; j++) {
				String chouetteRoutePtLinkId = chouetteRoute.getPtLinkId(j);
				String newChouetteRoutePtLinkId = getIdComplete(chouetteRoutePtLinkId, chouetteRouteId, j);
				chouetteRoutePtLinks.add(newChouetteRoutePtLinkId);
				
				PtLink oldPtLink = tronconParObjectId.get(chouetteRoutePtLinkId);				
				String startOfLink = oldPtLink.getStartOfLink();
				String endOfLink = oldPtLink.getEndOfLink();
				PtLink ptLink = new PtLink();
				try {
					BeanUtils.copyProperties(ptLink, oldPtLink);
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				ptLink.setObjectId(newChouetteRoutePtLinkId);
				String newStartOfLink = getIdComplete(startOfLink, chouetteRouteId, j);
				ptLink.setStartOfLink(newStartOfLink);
				String newEndOfLink = getIdComplete(endOfLink, chouetteRouteId, j+1);
				ptLink.setEndOfLink(newEndOfLink);
				ptLinks.add(ptLink);
				
				if (j == 0) {
					StopPoint firstStopPoint;
					StopPoint newFirstStopPoint = new StopPoint();
					firstStopPoint = phyArretParObjectId.get(startOfLink);
					try {
						BeanUtils.copyProperties(newFirstStopPoint, firstStopPoint);
					}
					catch (Exception e) {
						logger.error(e.getMessage(), e);
					}					
					newFirstStopPoint.setObjectId(newStartOfLink);
					stopPoints.add(newFirstStopPoint);
				}
				StopPoint stopPoint;
				StopPoint newStopPoint = new StopPoint();
				stopPoint = phyArretParObjectId.get(endOfLink);				
				try {
					BeanUtils.copyProperties(newStopPoint, stopPoint);
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				newStopPoint.setObjectId(newEndOfLink);
				stopPoints.add(newStopPoint);
				
				if (j == 0) {
					Queue<VehicleJourneyAtStop> __vehicleJourneyAtStops = vehicleJourneyAtStopParRouteIdEtStopPointId.get(new Paire(chouetteRouteId, startOfLink));
					// GET THE FIRST ELEMENT AND REMOVE IT FROM THE QUEUE
					VehicleJourneyAtStop __vehicleJourneyAtStop = __vehicleJourneyAtStops.poll();
					__vehicleJourneyAtStop.setStopPointId(newStartOfLink);
				}
				Queue<VehicleJourneyAtStop> _vehicleJourneyAtStops = vehicleJourneyAtStopParRouteIdEtStopPointId.get(new Paire(chouetteRouteId, endOfLink));
				// GET THE FIRST ELEMENT AND REMOVE IT FROM THE QUEUE
				VehicleJourneyAtStop _vehicleJourneyAtStop = _vehicleJourneyAtStops.poll();
				_vehicleJourneyAtStop.setStopPointId(newEndOfLink);
			}
			chouetteRoute.setPtLinkId((String[])chouetteRoutePtLinks.toArray());
		}
		lineDescription.setPtLink((PtLink[])ptLinks.toArray());
		lineDescription.setStopPoint((StopPoint[])stopPoints.toArray());
	}
	
	public void transform2() {
		initialise2();
		transformPtLink_Route_StopPoint_VehicleJourney();
	}

	private void initialise2() {
		int totalArretsPhysiques = lineDescription.getStopPointCount();
		for (int i = 0; i < totalArretsPhysiques; i++) {
			StopPoint stopPoint = lineDescription.getStopPoint(i);
			phyArretParObjectId.put(stopPoint.getObjectId(), stopPoint);
		}
		int totalTroncons = lineDescription.getPtLinkCount();
		for (int i = 0; i < totalTroncons; i++) {
			PtLink ptLink = lineDescription.getPtLink(i);
			tronconParObjectId.put(ptLink.getObjectId(), ptLink);
		}
		int totalVehicleJourney = lineDescription.getVehicleJourneyCount();
		for (int i = 0; i < totalVehicleJourney; i++) {
			VehicleJourney vehicleJourney = lineDescription.getVehicleJourney(i);
			String routeId = vehicleJourney.getRouteId();
			int totalVehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStopCount();
			for (int j = 0; j < totalVehicleJourneyAtStop; j++) {
				VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStop(j);
				String stopPointId = vehicleJourneyAtStop.getStopPointId();
				Paire routeIdStopPointId = new Paire(routeId, stopPointId);
				Queue<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourneyAtStopParRouteIdEtStopPointId.get(routeIdStopPointId);
				if (vehicleJourneyAtStops == null) {
					vehicleJourneyAtStops = new ConcurrentLinkedQueue<VehicleJourneyAtStop>();
					vehicleJourneyAtStopParRouteIdEtStopPointId.put(routeIdStopPointId, vehicleJourneyAtStops);
				}
				vehicleJourneyAtStops.add(vehicleJourneyAtStop);
			}
		}
	}

	public void setChouetteArea(ChouetteArea chouetteArea) {
		this.chouetteArea = chouetteArea;
	}

	public void setLineDescription(ChouetteLineDescription lineDescription) {
		this.lineDescription = lineDescription;
	}
}
