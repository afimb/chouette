package fr.certu.chouette.service.amivif;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import chouette.schema.ChouetteRoute;
import chouette.schema.JourneyPattern;
import chouette.schema.PtLink;
import chouette.schema.StopPoint;
import chouette.schema.VehicleJourney;
import chouette.schema.VehicleJourneyAtStop;
import fr.certu.chouette.service.amivif.util.Paire;

public class StopAdapter 
{
    private static final Logger logger = Logger.getLogger( StopAdapter.class);
	private List<StopPoint> stopPoints;
	private List<PtLink> ptLinks;
	private List<VehicleJourney> vehicles;
	private List<JourneyPattern> journeys;
	private List<ChouetteRoute> routes;
	
	private List<String> routeIds;
	private Map<String, PtLink> linkById;
	private Map<Paire, String> nvIdByLinkIdRouteId;
	private Map<String, StopPoint> stopById;
	private Map<Paire, String> nvIdByStopIdRouteId;
	
	public void initialiser()
	{
		routeIds = new ArrayList<String>();
		linkById = new Hashtable<String, PtLink>();
		nvIdByLinkIdRouteId = new Hashtable<Paire, String>();
		stopById = new Hashtable<String, StopPoint>();
		nvIdByStopIdRouteId = new Hashtable<Paire, String>();
		
		for (PtLink ptLink : ptLinks) {
			linkById.put( ptLink.getObjectId(), ptLink);
		}
		for (StopPoint stop : stopPoints) {
			stopById.put( stop.getObjectId(), stop);
		}
		
		for (ChouetteRoute route : routes) {
			routeIds.add( route.getObjectId());
			
			int total = route.getPtLinkIdCount();
			for (int i = 0; i < total; i++) {
				Paire paire = new Paire( route.getObjectId(), route.getPtLinkId( i));
				nvIdByLinkIdRouteId.put( paire, paire.getNouvelId());
				
				if ( i==0)
				{
					PtLink premierLink = linkById.get( route.getPtLinkId( i));
					Paire pairePremStop = new Paire( route.getObjectId(), premierLink.getStartOfLink());
					nvIdByStopIdRouteId.put( pairePremStop, pairePremStop.getNouvelId());
				}
				PtLink link = linkById.get( route.getPtLinkId( i));
				Paire paireStop = new Paire( route.getObjectId(), link.getEndOfLink());
				nvIdByStopIdRouteId.put( paireStop, paireStop.getNouvelId());
			}
		}
		logger.debug( nvIdByStopIdRouteId.toString());
		
		
		// transformation
		
		for (ChouetteRoute route : routes) {
			int total = route.getPtLinkIdCount();
			List<String> nvLinkIds = new ArrayList<String>();
			for (int i = 0; i < total; i++) {
				nvLinkIds.add( nvIdByLinkIdRouteId.get( new Paire( route.getObjectId(), route.getPtLinkId( i))));
			}
			route.setPtLinkId( new String[ 0]);
			route.setPtLinkId( nvLinkIds.toArray( new String[ 0]));
		}
		
		List<StopPoint> nvStops = new ArrayList<StopPoint>();
		for (String routeId : routeIds) {
			for (StopPoint exStop : stopPoints) {
				if ( nvIdByStopIdRouteId.containsKey( new Paire( routeId, exStop.getObjectId())))
				{
					StopPoint nvStop = new StopPoint();
					try
					{
						BeanUtils.copyProperties(nvStop, exStop);
					}
					catch( Exception e){}
					nvStop.setObjectId( nvIdByStopIdRouteId.get( new Paire( routeId, exStop.getObjectId())));
					nvStops.add( nvStop);
				}
			}
		}
		
		stopPoints.clear();
		stopPoints.addAll( nvStops);
		
		List<PtLink> nvLinks = new ArrayList<PtLink>();
		for (String routeId : routeIds) {
			for (PtLink exLink : ptLinks) {
				Paire paire = new Paire( routeId, exLink.getObjectId());
				if ( nvIdByLinkIdRouteId.containsKey( paire))
				{
					PtLink nvPtLink = new PtLink();
					nvPtLink.setObjectId( nvIdByLinkIdRouteId.get( paire));
					nvPtLink.setStartOfLink( nvIdByStopIdRouteId.get( new Paire( routeId, exLink.getStartOfLink())));
					nvPtLink.setEndOfLink( nvIdByStopIdRouteId.get( new Paire( routeId, exLink.getEndOfLink())));
					nvLinks.add( nvPtLink);
				}
			}
		}
		
		ptLinks.clear();
		ptLinks.addAll( nvLinks);
		
		for (VehicleJourney vehicle : vehicles) {
			String routeId = vehicle.getRouteId();
			int total = vehicle.getVehicleJourneyAtStopCount();
			for (int i = 0; i < total; i++) {
				VehicleJourneyAtStop vehicleAtStop = vehicle.getVehicleJourneyAtStop( i);
				Paire paire = new Paire( routeId, vehicleAtStop.getStopPointId());
				vehicleAtStop.setStopPointId( nvIdByStopIdRouteId.get( paire));
			}
		}
	}


	public void setJourneys(List<JourneyPattern> journeys) {
		this.journeys = journeys;
	}


	public void setPtLinks(List<PtLink> ptLinks) {
		this.ptLinks = new ArrayList<PtLink>( ptLinks);
	}


	public void setRoutes(List<ChouetteRoute> routes) {
		this.routes = routes;
	}


	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = new ArrayList<StopPoint>( stopPoints);
	}


	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}


	public List<PtLink> getPtLinks() {
		return ptLinks;
	}


	public void setVehicles(List<VehicleJourney> vehicles) {
		this.vehicles = vehicles;
	}
	
}
