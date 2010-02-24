package fr.certu.chouette.service.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import chouette.schema.AreaCentroid;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRoute;
import chouette.schema.Company;
import chouette.schema.ConnectionLink;
import chouette.schema.JourneyPattern;
import chouette.schema.Line;
import chouette.schema.PTNetwork;
import chouette.schema.PtLink;
import chouette.schema.StopArea;
import chouette.schema.StopAreaExtension;
import chouette.schema.StopPoint;
import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class LecteurEchangeXML implements ILecteurEchangeXML 
{
    private static final Logger logger = Logger.getLogger( LecteurEchangeXML.class);
    
	private Transporteur transporteur;
	private Reseau reseau;
	private Ligne ligne;
	private List<Itineraire> itineraires;
	private List<TableauMarche> tableauxMarche;
	private List<ArretItineraire> arretsLogiques;
	private List<PositionGeographique> arretsPhysiques;
	private List<PositionGeographique> zonesCommerciales;
	private List<PositionGeographique> zonesPlaces;
	private List<Correspondance> correspondances;
	private List<Mission> missions;
	private List<Course> courses;
	private List<Horaire> horaires;
	private List<String> objectIdZonesGeneriques;
	private List<InterdictionTraficLocal> itls;
	private Map<String, String> itineraireParArretLogique;
	private Map<String, PositionGeographique> arretPhysiqueParObjectId;
	private Map<String, String> zoneParenteParObjectId;
	private Map<String, List<String>> arretsPhysiquesIdParITL;
	private Map<String, PositionGeographique> zoneITLParITL;
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.xml.ILecteurEchangeXML#lire(chouette.schema.ChouettePTNetworkType)
	 */
	public ILectureEchange lire( final ChouettePTNetworkTypeType chouettePTNetwork)
	{
		logger.debug("EVOCASTOR --> read ChouettePTNetworkTypeType");
		if (chouettePTNetwork.getCompanyCount() == 0)
		{
			throw new ServiceException(CodeIncident.ERR_LECT_TRSP_ABSENT);
		}
		Company company = chouettePTNetwork.getCompany( 0);
		if (company.getRegistration()==null || company.getRegistration().getRegistrationNumber()==null)
		{
			throw new ServiceException(CodeIncident.ERR_LECT_REGISTRE_TRSP_ABSENT);
		}
		transporteur = new Transporteur();
		transporteur.setCompany(company);

		if (chouettePTNetwork.getPTNetwork() == null)
		{
			throw new ServiceException( CodeIncident.ERR_LECT_RES_ABSENT);
		}
		PTNetwork ptNetwork = chouettePTNetwork.getPTNetwork();
		if ( ptNetwork.getRegistration()==null || ptNetwork.getRegistration().getRegistrationNumber()==null)
		{
			throw new ServiceException( CodeIncident.ERR_LECT_REGISTRE_RES_ABSENT);
		}
		reseau = new Reseau();
		reseau.setPtNetwork( ptNetwork);

		ChouetteLineDescription description = chouettePTNetwork.getChouetteLineDescription();
		if ( description==null || description.getLine()==null)
		{
			throw new ServiceException( CodeIncident.ERR_LECT_LIG_ABSENT);
		}
		Line line = description.getLine();
		if ( line.getRegistration()==null || line.getRegistration().getRegistrationNumber()==null)
		{
			throw new ServiceException( CodeIncident.ERR_LECT_REGISTRE_LIG_ABSENT);
		}
		ligne = new Ligne();
		ligne.setLine( line);
		
		// lecture des itineraires
		Map<String, List<String>> tronconsParItineraire = new Hashtable<String, List<String>>();
		int totalItineraires = description.getChouetteRouteCount();
		itineraires = new ArrayList<Itineraire>( totalItineraires);
		for (int i = 0; i < totalItineraires; i++) 
		{
			Itineraire itineraire = new Itineraire();
			ChouetteRoute route = description.getChouetteRoute( i);
			itineraire.setChouetteRoute( route);
			itineraires.add( itineraire);
			
			int totalTroncons = route.getPtLinkIdCount();
			List<String> lesTroncons = new ArrayList<String>( totalTroncons);
			for ( int j = 0; j < totalTroncons; j++)
			{
				lesTroncons.add( route.getPtLinkId( j));
			}
			tronconsParItineraire.put( route.getObjectId(), lesTroncons); 
		}
		
		// lecture des TM
		int totalTableauxMarche = chouettePTNetwork.getTimetableCount();
		tableauxMarche = new ArrayList<TableauMarche>( totalTableauxMarche);
		for (int i = 0; i < totalTableauxMarche; i++) 
		{
			TableauMarche tableauMarche = new TableauMarche();
			tableauMarche.setTimetable( chouettePTNetwork.getTimetable( i));

			// correction Faille Castor
			List<String> vehicleIds = new ArrayList<String>();
			int totalAp = tableauMarche.getVehicleJourneyIdCount();
			for (int j = 0; j < totalAp; j++) {
				if ( tableauMarche.getVehicleJourneyId(j)!=null)
					vehicleIds.add( tableauMarche.getVehicleJourneyId(j));
			}
			tableauMarche.getTimetable().setVehicleJourneyId( vehicleIds.toArray( new String[0]));
			tableauxMarche.add( tableauMarche);
		}
		
		// lecture des zonoes
		lireZones( chouettePTNetwork.getChouetteArea());
		
		// lecture des correspondances
		logger.debug( "ConnectionLinkCount()="+chouettePTNetwork.getConnectionLinkCount());
		lireCorrespondances( chouettePTNetwork.getConnectionLink());
		
		// lecture des missions
		logger.debug( "total missions="+chouettePTNetwork.getChouetteLineDescription().getJourneyPatternCount());
		lireMissions( chouettePTNetwork.getChouetteLineDescription().getJourneyPattern());
		logger.debug( "missions.size()="+missions.size());
		
		// lecture des troncons
		Map<String, List<String>> arretsParTroncon = new Hashtable<String, List<String>>();
		int totalTroncons = description.getPtLinkCount();
		for (int i = 0; i < totalTroncons; i++) 
		{
			PtLink troncon = description.getPtLink( i);
			List<String> arretsTroncon = new ArrayList<String>( 2);
			arretsTroncon.add( troncon.getStartOfLink());
			arretsTroncon.add( troncon.getEndOfLink());
			arretsParTroncon.put( troncon.getObjectId(), arretsTroncon);
		}
		
		// lecture des arrets
		int totalArrets = description.getStopPointCount();
		Map<String, StopPoint> arretParObjectId = new Hashtable<String, StopPoint>( totalArrets); 
		for (int i = 0; i < totalArrets; i++) 
		{
			StopPoint arret = description.getStopPoint( i);
			arretParObjectId.put( arret.getObjectId(), arret);
			
			// controler le lien sur l'arret physique
			String areaId = arret.getContainedIn();
			if ( areaId!=null && !arretPhysiqueParObjectId.containsKey( areaId))
			{
				throw new ServiceException( CodeIncident.ERR_LECT_ID_AREA_INCONNU, CodeDetailIncident.STOPPOINT_LINK,arret.getObjectId(),areaId,arretPhysiqueParObjectId.keySet().toString());
			}
		}
		
		// construction des relations arret -> position sur son itineraire
		// et arret -> son itineraire
		
		itineraireParArretLogique = new Hashtable<String, String>();
		Map<String, Integer> positionParArret = new Hashtable<String, Integer>();
		// parcours des itineraires
		Set<String> ensembleItineraires = tronconsParItineraire.keySet();
		for (String itineraireObjectId : ensembleItineraires) 
		{
			List<String> tronconsItineraire = tronconsParItineraire.get( itineraireObjectId);
			
			// parcours des troncons de l'itineraire
			int totalTronconsItineraire = tronconsItineraire.size();
			for (int i = 0; i < totalTronconsItineraire; i++) 
			{
				String tronconObjectId = tronconsItineraire.get( i);
				
				if ( !arretsParTroncon.containsKey( tronconObjectId))
				{
					throw new ServiceException( CodeIncident.ERR_LECT_TRONCON_NON_DEFINI, CodeDetailIncident.DEFAULT,tronconObjectId,itineraireObjectId,i);
				}
				List<String> arretsTroncon = arretsParTroncon.get( tronconObjectId);
				
				if ( i==0)
				{
					positionParArret.put( arretsTroncon.get( 0), new Integer( 0));
					itineraireParArretLogique.put( arretsTroncon.get( 0), itineraireObjectId);
					
					if ( !arretParObjectId.containsKey( arretsTroncon.get( 0)))
					{
						throw new ServiceException( CodeIncident.ERR_LECT_ARRET_NON_DEFINI, CodeDetailIncident.DEFAULT,arretsTroncon.get( 0));
					}
				}
				positionParArret.put( arretsTroncon.get( 1), new Integer( i+1));
				itineraireParArretLogique.put( arretsTroncon.get( 1), itineraireObjectId);
				
				if ( !arretParObjectId.containsKey( arretsTroncon.get( 1)))
				{
					throw new ServiceException( CodeIncident.ERR_LECT_ARRET_NON_DEFINI, CodeDetailIncident.DEFAULT,arretsTroncon.get( 1));
				}
			}
		}
		
		// chargement des arrets d'itineraire
		Set<String> arretsItineraire = itineraireParArretLogique.keySet();
		arretsLogiques = new ArrayList<ArretItineraire>();
		for (String arretObjectId : arretsItineraire) 
		{
			ArretItineraire arret = new ArretItineraire();
			
			StopPoint stopPoint = arretParObjectId.get( arretObjectId);
			int position = positionParArret.get( arretObjectId);
			arret.setStopPoint( stopPoint);
			arret.setPosition( position);
			
			arretsLogiques.add( arret);
		}
		// lecture des courses et des horaires
		int totalCourses = description.getVehicleJourneyCount();
		courses = new ArrayList<Course>( totalCourses);
		horaires = new ArrayList<Horaire>();
		for (int i = 0; i < totalCourses; i++) 
		{
			Course course = new Course();
			course.setVehicleJourney( description.getVehicleJourney( i));
			courses.add( course);
			
			int totalhorairesCourse = course.getVehicleJourney().getVehicleJourneyAtStopCount();
			for (int j = 0; j < totalhorairesCourse; j++) 
			{
				Horaire horaire = new Horaire();
				horaire.setDepart( j==0);
				horaire.setVehicleJourneyAtStop( course.getVehicleJourney().getVehicleJourneyAtStop( j));
				horaires.add( horaire);
			}
//			course.getVehicleJourney().setVehicleJourneyAtStop( new VehicleJourneyAtStop[ 0]);
		}
		
		
		int totalITLs = description.getITLCount();
		itls = new ArrayList<InterdictionTraficLocal>(totalITLs);
		if ( itls==null) throw new NullPointerException( "lecture itl null!!");
		arretsPhysiquesIdParITL = new Hashtable<String, List<String>>();
		
		for (int i = 0; i < totalITLs; i++) 
		{
			InterdictionTraficLocal itl = new InterdictionTraficLocal();
			itl.setObjectId( description.getITL(i).getAreaId());
			itl.setNom( description.getITL(i).getName());
			itls.add(itl);

			PositionGeographique positionsZoneITL = zoneITLParITL.get( itl.getObjectId());
			String[] objectIds = positionsZoneITL.getStopArea().getContains();
			arretsPhysiquesIdParITL.put( itl.getObjectId(), 
										Arrays.asList( objectIds));
		}
		
		LectureEchange resultat = new LectureEchange();
		resultat.setPhysiquesParITLId(arretsPhysiquesIdParITL);
		resultat.setArretsPhysiques(arretsPhysiques);
		resultat.setZonesCommerciales(zonesCommerciales);
		resultat.setZonesPlaces(zonesPlaces);
		resultat.setArrets(arretsLogiques);
		resultat.setItineraireParArret(itineraireParArretLogique);
		resultat.setItineraires(itineraires);
		resultat.setLigne(ligne);
		resultat.setReseau(reseau);
		resultat.setTableauxMarche(tableauxMarche);
		resultat.setTransporteur(transporteur);
		resultat.setCourses(courses);
		resultat.setMissions(missions);
		resultat.setHoraires(horaires);
		resultat.setObjectIdZonesGeneriques(objectIdZonesGeneriques);	
		resultat.setZoneParenteParObjectId(zoneParenteParObjectId);
		resultat.setCorrespondances(correspondances);
		resultat.setInterdictionTraficLocal(itls);
		return resultat;
	}
	
	private void lireMissions( final JourneyPattern[] journeys)
	{
		this.missions = new ArrayList<Mission>();
		
		if ( journeys==null) return;
		
		int total = journeys.length;
		for (int i = 0; i < total; i++) 
		{
			JourneyPattern journey = journeys[ i];
			Mission mission = new Mission();
			mission.setJourneyPattern( journey);
			this.missions.add( mission);
		}
	}
	
	private void lireCorrespondances( final ConnectionLink[] correspondances)
	{
		this.correspondances = new ArrayList<Correspondance>();
		
		if ( correspondances==null) return;
		
		int total = correspondances.length;
		for (int i = 0; i < total; i++) 
		{
			ConnectionLink connectionLink = correspondances[ i];
			Correspondance correspondance = new Correspondance();
			correspondance.setConnectionlink( connectionLink);
			this.correspondances.add( correspondance);
		}
	}

	private void lireZones( final ChouetteArea chouetteArea) 
	{
		arretPhysiqueParObjectId = new Hashtable<String, PositionGeographique>();
		arretsPhysiques = new ArrayList<PositionGeographique>();
		zonesCommerciales = new ArrayList<PositionGeographique>();
		zonesPlaces = new ArrayList<PositionGeographique>();
		objectIdZonesGeneriques = new ArrayList<String>();
		zoneITLParITL = new Hashtable<String, PositionGeographique>();
		
		if ( chouetteArea!=null)
		{
			// lecture des positions géographiques
			int totalPositionsGeo = chouetteArea.getAreaCentroidCount();
			
			Map<String, AreaCentroid> centroidParId = new Hashtable<String, AreaCentroid>( totalPositionsGeo);
			for (int i = 0; i < totalPositionsGeo; i++) 
			{
				AreaCentroid areaCentroid = chouetteArea.getAreaCentroid( i);
				
				if ( areaCentroid==null || areaCentroid.getObjectId()==null)
				{
					logger.debug( "i="+i+", areaCentroid==null ?"+(areaCentroid==null));
				}
					
				centroidParId.put( areaCentroid.getObjectId(), areaCentroid);
			}
			
			// lecture des zones
			int totalZonesGeneriques = chouetteArea.getStopAreaCount();
			
			for (int i = 0; i < totalZonesGeneriques; i++) 
			{
				StopArea stopArea = chouetteArea.getStopArea( i);
				objectIdZonesGeneriques.add( stopArea.getObjectId());
				
				ChouetteAreaType typeZone = stopArea.getStopAreaExtension().getAreaType();
				// identification du type de zone
				if ( ChouetteAreaType.BOARDINGPOSITION.equals( typeZone)
					|| ChouetteAreaType.QUAY.equals( typeZone))
				{
					PositionGeographique arretPhysique = lirePositionGeographique( stopArea, centroidParId);
					
					arretsPhysiques.add( arretPhysique);
					
					arretPhysiqueParObjectId.put( stopArea.getObjectId(), arretPhysique);
				}
				else if ( ChouetteAreaType.COMMERCIALSTOPPOINT.equals( typeZone))
				{
					zonesCommerciales.add( lirePositionGeographique( stopArea, centroidParId));
				}
				else if ( ChouetteAreaType.STOPPLACE.equals( typeZone))
				{
					zonesPlaces.add( lirePositionGeographique( stopArea, centroidParId));
				}
				else if ( ChouetteAreaType.ITL.equals( typeZone))
				{
					zoneITLParITL.put( stopArea.getObjectId(), lirePositionGeographique( stopArea, centroidParId));
				}
			}
			
			// vérifier que toutes les zones contiennent des zones définies
			Set<PositionGeographique> zones = new HashSet<PositionGeographique>( zonesCommerciales);
			zones.addAll( zonesPlaces);
			
			zoneParenteParObjectId = new Hashtable<String, String>();
			for (PositionGeographique zone : zones) 
			{
				String[] zonesContenues = zone.getStopArea().getContains();
				
				for (int j = 0; j < zonesContenues.length; j++) 
				{
					if ( !objectIdZonesGeneriques.contains( zonesContenues[ j]))
					{
//						throw new ServiceException( CodeIncident.DONNEE_INVALIDE,
//													"la zone "+zonesContenues[ j]+" référencée par "+zone.getObjectId()+" est inconnue");
					}
					if ( zoneParenteParObjectId.containsKey( zonesContenues[ j]))
					{
						throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.MULTIPLE_PARENT,zonesContenues[ j],zone.getObjectId(),zoneParenteParObjectId.get( zonesContenues[ j]));
					}
					zoneParenteParObjectId.put( zonesContenues[ j], zone.getObjectId());
				}
			}
			
		}
	}
	
	private PositionGeographique lirePositionGeographique( StopArea stopArea, Map<String, AreaCentroid> centroidParId)
	{
		StopAreaExtension extension = stopArea.getStopAreaExtension();
		if ( extension==null)
		{
			throw new ServiceException( CodeIncident.ERR_LECT_ZONE_NON_TYPEE, CodeDetailIncident.DEFAULT,stopArea.getObjectId());
		}
		
		// identification du type de zone
		PositionGeographique positionGeo = new PositionGeographique();
		
		positionGeo.setStopArea( stopArea);
		String centroidId = stopArea.getCentroidOfArea();
		if ( centroidId==null || !centroidParId.containsKey( centroidId))
		{
			//throw new ServiceException( CodeIncident.ERR_LECT_ID_CENTROID_INCONNU, "La zone "+stopArea.getObjectId()+" référence une position inconnue "+centroidId);
		}
		else
		{
			positionGeo.setAreaCentroid( centroidParId.get( centroidId));
		}
		return positionGeo;
	}
}
