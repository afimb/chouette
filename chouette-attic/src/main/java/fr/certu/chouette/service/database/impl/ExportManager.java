package fr.certu.chouette.service.database.impl;

import chouette.schema.AreaCentroid;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLine;
import chouette.schema.ChouetteRemoveLineTypeType;
import chouette.schema.ChouetteRoute;
import chouette.schema.ITL;
import chouette.schema.JourneyPattern;
import chouette.schema.PtLink;
import chouette.schema.StopArea;
import chouette.schema.StopAreaExtension;
import chouette.schema.StopPoint;
import chouette.schema.VehicleJourney;
import chouette.schema.types.ChouetteAreaType;
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
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.identification.ObjectIdLecteur;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class ExportManager implements IExportManager 
{
	
	private static final Logger                       logger                     = Logger.getLogger(ExportManager.class);
	private static final String                       SEPARATOR                  = "A";
	private              ILigneManager                ligneManager;
	private              IItineraireManager           itineraireManager;
	private              ITransporteurManager         transporteurManager;
	private              IReseauManager               reseauManager;
	private              IPositionGeographiqueManager positionGeographiqueManager;
	private              ICorrespondanceManager       correspondanceManager;
	private              ITableauMarcheManager        tableauMarcheManager;
	private              IIdentificationManager       identificationManager;
	private              IMissionManager              missionManager;
	private              IITLManager                  itlManager;
	private              String						  systemId;
	private              Map<Long, String>            objectIdParArretPhysiqueId = new Hashtable<Long, String>();
	private              Map<Long, String>            objectIdParArretLogiqueId  = new Hashtable<Long, String>();
	private              Map<Long, String>            objectIdParItineraireId    = new Hashtable<Long, String>();
	private              Map<Long, String>            objectIdParCourseId        = new Hashtable<Long, String>();
	private              Map<Long, Integer>           positionParArretId         = new Hashtable<Long, Integer>();
	private              Set<Long>                    idsArretsPhysiquesSurZone  = new HashSet<Long>();
	private              ChouettePTNetworkTypeType    resultat;
	
		
	public ChouetteRemoveLineTypeType getSuppressionParIdLigne(final Long idLigne)
	{
		return getSuppression(ligneManager.lire(idLigne));
	}
	
	public ChouetteRemoveLineTypeType getSuppressionParRegistration(final String registrationNumber) 
	{
		return getSuppression(ligneManager.getLigneParRegistration(registrationNumber));
	}
	
	private ChouetteRemoveLineTypeType getSuppression(final Ligne ligne) 
	{
		logger.debug("EVOCASTOR --> new ChouetteRemoveLine()");
		ChouetteRemoveLineTypeType resultat = new ChouetteRemoveLine();
		if (ligne.getIdReseau() != null) 
		{
			Reseau reseau = reseauManager.lire(ligne.getIdReseau());
			ligne.getLine().setPtNetworkIdShortcut(reseau.getObjectId());
		}
		List<Itineraire> itineraires = ligneManager.getItinerairesLigne(ligne.getId());
		for (Itineraire itineraire : itineraires)
			ligne.getLine().addRouteId(itineraire.getObjectId());
		logger.debug("EVOCASTOR --> ChouetteRemoveLine:setLine");
		resultat.setLine(ligne.getLine());
		return resultat;
	}	
	
	private void reinit() 
	{
		objectIdParArretPhysiqueId.clear();
		objectIdParArretLogiqueId.clear();
		objectIdParItineraireId.clear();
		objectIdParCourseId.clear();
		positionParArretId.clear();
		idsArretsPhysiquesSurZone.clear();
	}
		
	/**
	 * @param ligne
	 * @param exportMode will be used to specify the export type following desired extensions
	 * @return
	 */
	private ChouettePTNetworkTypeType getExport(final Ligne ligne, ExportMode exportMode) 
	{
		reinit();
		logger.debug("EVOCASTOR --> new ChouettePTNetwork");
		resultat = new ChouettePTNetwork();
		if (ligne.getIdReseau() == null)
			throw new ServiceException(CodeIncident.EXPORT_SANS_RESEAU);
		if (ligne.getIdTransporteur() == null)
			throw new ServiceException(CodeIncident.EXPORT_SANS_TRANSPORTEUR);
		Reseau reseau = reseauManager.lire(ligne.getIdReseau());
		Transporteur transporteur = transporteurManager.lire(ligne.getIdTransporteur());
		List<Itineraire> itineraires = ligneManager.getLigneItinerairesExportables(ligne.getId());
		Collection<Long> idItineraires = new ArrayList<Long>();
		for (Itineraire itineraire : itineraires)
		{
			idItineraires.add(itineraire.getId());
		}
		systemId = lireSystemeOrigine(ligne.getObjectId());
		
		logger.debug("EVOCASTOR --> ChouettePTNetwork:addCompagny");
		resultat.addCompany(transporteur.getCompany());
		logger.debug("EVOCASTOR --> ChouettePTNetwork:setPTNetwork");
		resultat.setPTNetwork(reseau.getPtNetwork());
		ChouetteLineDescription description = new ChouetteLineDescription();
		logger.debug("EVOCASTOR --> ChouettePTNetwork:setChouetteLineDescription");
		resultat.setChouetteLineDescription(description);
		description.setLine(ligne.getLine());
		construireRoutesArretsLogiques(itineraires);
		construireCourseTM(itineraires);
		construireZoneCorrespondances();
		construireITL(ligne);
		reinit();
		return resultat;
	}
	
	public ChouettePTNetworkTypeType getExportParRegistration(final String registrationNumber) 
	{
		return getExportParRegistration(registrationNumber, ExportMode.CHOUETTE);
	}
	
	public ChouettePTNetworkTypeType getExportParIdLigne(final Long idLigne) 
	{
		return getExportParIdLigne(idLigne, ExportMode.CHOUETTE);
	}
	
	public ChouettePTNetworkTypeType getExportParRegistration(final String registrationNumber, ExportMode exportMode) 
	{
		return getExport(ligneManager.getLigneParRegistration(registrationNumber), exportMode);
	}
	
	public ChouettePTNetworkTypeType getExportParIdLigne(final Long idLigne, ExportMode exportMode) 
	{
		return getExport(ligneManager.lire(idLigne), exportMode);
	}
	
	private void construireITL(Ligne ligne) 
	{
		ChouetteLineDescription description = resultat.getChouetteLineDescription();
		ChouetteArea chouetteArea = resultat.getChouetteArea();
		List<InterdictionTraficLocal> itls = itlManager.getITLLigne(ligne.getId());
		logger.debug("id ligne="+ligne.getId()+" total itls = "+itls.size());
		for (InterdictionTraficLocal itl : itls) 
		{
			description.addITL(getITL(itl, ligne));
			chouetteArea.addStopArea(getITLStopArea(itl));
		}
	}
	
	private void construireZoneCorrespondances() 
	{
		Map<Long, PositionGeographique> zonesParentesParId = new Hashtable<Long, PositionGeographique>();
		Map<Long, String> zonesParentesObjectIdParId = new Hashtable<Long, String>();
		Map<Long, List<Long>> zonesContenuesParIdZone = new Hashtable<Long, List<Long>>();
		for (Long zoneId : idsArretsPhysiquesSurZone) 
		{
			List<PositionGeographique> zonesParentes = positionGeographiqueManager.getGeoPositionsParentes(zoneId);
			for (PositionGeographique zone : zonesParentes) 
			{
				zonesParentesParId.put(zone.getId(), zone);
				zonesParentesObjectIdParId.put(zone.getId(), zone.getObjectId());
				if (zone.getIdParent() != null) 
				{
					List<Long> idsZonesContenues = zonesContenuesParIdZone.get(zone.getIdParent());
					if (idsZonesContenues == null) 
					{
						idsZonesContenues = new ArrayList<Long>();
						zonesContenuesParIdZone.put(zone.getIdParent(), idsZonesContenues);
					}
					if (!idsZonesContenues.contains(zone.getId()))
						idsZonesContenues.add(zone.getId());
				}
			}
		}
		for (PositionGeographique zone : zonesParentesParId.values()) 
		{
			StopArea stopArea = zone.getStopArea();
			List<Long> zonesContenues = zonesContenuesParIdZone.get(zone.getId());
			if (zonesContenues!=null && !zonesContenues.isEmpty()) 
			{
				for (Long idZoneContenue : zonesContenues)
					stopArea.addContains(zonesParentesParId.get(idZoneContenue).getObjectId());
				retirerStructuresVides(stopArea);
				if (!zone.isEmptyAreaCentroid()) 
				{
					AreaCentroid centroid = getExportAreaCentroid(zone);
					stopArea.setCentroidOfArea(centroid.getObjectId());
					resultat.getChouetteArea().addAreaCentroid(centroid);
				}
				resultat.getChouetteArea().addStopArea(stopArea);
			}
		}
		Collection<Long> positionIds = new ArrayList<Long>();
		positionIds.addAll(objectIdParArretPhysiqueId.keySet());
		positionIds.addAll(zonesParentesParId.keySet());
                if (positionIds != null && !positionIds.isEmpty()) {
		List<Correspondance> correspondances = correspondanceManager.selectionParPositions(positionIds);
		Collection<Long> positionHorsLigneIds = new HashSet<Long>();
		for (Correspondance correspondance : correspondances) 
		{
			Long depart = correspondance.getIdDepart();
			Long arrivee = correspondance.getIdArrivee();
			if (!objectIdParArretPhysiqueId.containsKey(depart) && !zonesParentesParId.containsKey(depart))
				positionHorsLigneIds.add(depart);
			if (!objectIdParArretPhysiqueId.containsKey(arrivee) && !zonesParentesParId.containsKey(arrivee))
				positionHorsLigneIds.add(arrivee);
		}
		Collection<PositionGeographique> positionHorsLignes = positionGeographiqueManager.getArretsPhysiques(positionHorsLigneIds);
		Map<Long, String> positionParId = new Hashtable<Long, String>();
		positionParId.putAll(objectIdParArretPhysiqueId);
		positionParId.putAll(zonesParentesObjectIdParId);
		for (PositionGeographique positionGeographique : positionHorsLignes)
			positionParId.put(positionGeographique.getId(), positionGeographique.getObjectId());
		for (Correspondance correspondance : correspondances) 
		{
			correspondance.setStartOfLink(positionParId.get(correspondance.getIdDepart()));
			correspondance.setEndOfLink(positionParId.get(correspondance.getIdArrivee()));
			if (correspondance.getStartOfLink()==null)
				throw new ServiceException(CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.CONNECTIONLINK_DEPARTURE,correspondance.getIdDepart(),correspondance.getId());
			if (correspondance.getEndOfLink()==null)
				throw new ServiceException(CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.CONNECTIONLINK_ARRIVAL,correspondance.getIdArrivee(),correspondance.getId());
			resultat.addConnectionLink(correspondance.getConnectionLink());
		}
                }
	}

	private void construireCourseTM(final List<Itineraire> itineraires) 
	{
		Map<Long, String> objectIdParIdItineraire = new Hashtable<Long, String>();
		for (Itineraire itineraire : itineraires)
			objectIdParIdItineraire.put(itineraire.getId(), itineraire.getObjectId());
		Collection<Long> idItineraires = new HashSet<Long>(objectIdParIdItineraire.keySet());
		List<Horaire> tousHoraires = itineraireManager.getHorairesItineraires(idItineraires);
		logger.debug("début tri");
		SortedSet<Horaire> horairesTries = new TreeSet<Horaire>(new ExportManager.ComparatorHoraire<Horaire>());
		horairesTries.addAll(tousHoraires);
		tousHoraires = new ArrayList<Horaire>(horairesTries);
		logger.debug("fin tri");
		Map<Long, List<Horaire>> horairesTriesParCourse = new Hashtable<Long, List<Horaire>>();
		for (Horaire horaire : tousHoraires) 
		{
			List<Horaire> horairesTriesDeCourse = horairesTriesParCourse.get(horaire.getIdCourse());
			if (horairesTriesDeCourse == null) 
			{
				horairesTriesDeCourse = new ArrayList<Horaire>();
				horairesTriesParCourse.put(horaire.getIdCourse(), horairesTriesDeCourse);
			}
			horairesTriesDeCourse.add(horaire);
		}
		Set<String> journeyPatternIds = new HashSet<String>(); 
		ChouetteLineDescription description = resultat.getChouetteLineDescription();
		List<Course> courses = itineraireManager.getCoursesItineraires(idItineraires);
		Collection<Long> idMissions = new HashSet<Long>();
		for (Course course : courses)
			idMissions.add(course.getIdMission());
		Map<Long, Mission> missionParId = new Hashtable<Long, Mission>();
		List<Mission> missions = missionManager.getMissions(idMissions);
		for (Mission mission : missions)
			missionParId.put(mission.getId(), mission);
		Map<String, ChouetteRoute> itineraireParTridentId = new Hashtable<String, ChouetteRoute>();
		int totalItineraires = description.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++)
			itineraireParTridentId.put(description.getChouetteRoute(i).getObjectId(), description.getChouetteRoute(i));
		for (Course course : courses) 
		{
			Long idCourse = course.getId();
			List<Horaire> horaires = horairesTriesParCourse.get(idCourse);
			if (horaires!=null && 1<horaires.size()) 
			{
				List<String> stopPointList = new ArrayList<String>();
				objectIdParCourseId.put(idCourse, course.getObjectId());
				for (Horaire horaire : horaires) 
				{
					horaire.setStopPointId(objectIdParArretLogiqueId.get(horaire.getIdArret()));
					horaire.setVehicleJourneyId(course.getObjectId());
					course.getVehicleJourney().addVehicleJourneyAtStop(horaire.getVehicleJourneyAtStop());
					stopPointList.add(horaire.getStopPointId());
				}
				VehicleJourney vehicleJourney = course.getVehicleJourney();
				vehicleJourney.setRouteId(objectIdParIdItineraire.get(course.getIdItineraire()));
				description.addVehicleJourney(vehicleJourney);
				Long idMission = course.getIdMission();
				if (idMission != null) 
				{
					Mission mission = missionParId.get(idMission);
					vehicleJourney.setJourneyPatternId(mission.getObjectId());
					if (journeyPatternIds.add(mission.getObjectId())) 
					{
						JourneyPattern journeyPattern = mission.getJourneyPattern();
						journeyPattern.setRouteId(vehicleJourney.getRouteId());
						journeyPattern.setStopPointList(stopPointList.toArray(new String[0]));
						if (journeyPattern.getRegistration().getRegistrationNumber()==null)
							journeyPattern.setRegistration(null);
						description.addJourneyPattern(journeyPattern);
						ChouetteRoute itineraire = itineraireParTridentId.get(vehicleJourney.getRouteId());
						itineraire.addJourneyPatternId(journeyPattern.getObjectId());
					}
				}
			}
		}
		List<TableauMarche> tousTableauxMarche = itineraireManager.getTableauxMarcheItineraires(idItineraires);
		for (TableauMarche marche : tousTableauxMarche) 
		{
			Long idTM = marche.getId();
			List<Course> tmCourses = tableauMarcheManager.getCoursesTableauMarche(idTM);
			for (Course course : tmCourses) 
				marche.addVehicleJourneyId(course.getObjectId());
			resultat.addTimetable(marche.getTimetable());
		}
	}
	
	private void construireRoutesArretsLogiques(final List<Itineraire> itineraires) 
	{
		Collection<Long> idItineraires = new HashSet<Long>();		
		for (Itineraire itineraire : itineraires)
		{
			idItineraires.add(itineraire.getId());
		}
		
		Map<String, PtLink> troncons = new Hashtable<String, PtLink>();
		Map<Long, List<ArretItineraire>> arretsParItineraire = new Hashtable<Long, List<ArretItineraire>>();
		List<ArretItineraire> arretsToutItineraire = itineraireManager.getArretsItineraires(idItineraires);
		for (ArretItineraire arret : arretsToutItineraire) 
		{
			List<ArretItineraire> arretsItineraire = arretsParItineraire.get(arret.getIdItineraire());
			if (arretsItineraire == null) 
			{
				arretsItineraire = new ArrayList<ArretItineraire>();
				arretsParItineraire.put(arret.getIdItineraire(), arretsItineraire);
			}
			arretsItineraire.add(arret);
			positionParArretId.put(arret.getId(), arret.getPosition());
		}
		
		ChouetteLineDescription description = resultat.getChouetteLineDescription();
		for (Itineraire itineraire : itineraires) 
		{
			Long idItineraire = itineraire.getId();
			ChouetteRoute route = itineraire.getChouetteRoute();
			description.addChouetteRoute(route);
			description.getLine().addRouteId(route.getObjectId());
			List<ArretItineraire> itineraireArrets = arretsParItineraire.get(idItineraire);
			List<PtLink> itineraireTroncons = getPtLinkArray(itineraireArrets);
			objectIdParItineraireId.put(idItineraire, itineraire.getObjectId());
			for (PtLink link : itineraireTroncons) {
				route.addPtLinkId(link.getObjectId());
				if (!troncons.containsKey(link.getObjectId()))
					troncons.put(link.getObjectId(), link);
			}
		}
		for (PtLink link : troncons.values())
			description.addPtLink(link);
		Collection<Long> idPhysiques = new HashSet<Long>();
		for (ArretItineraire arret : arretsToutItineraire) 
			idPhysiques.add(arret.getIdPhysique());
		ChouetteArea chouetteArea = new ChouetteArea();
		resultat.setChouetteArea(chouetteArea);
		List<PositionGeographique> arretsPhysiques = positionGeographiqueManager.getArretsPhysiques(idPhysiques);
		Map<String, StopArea> stopAreaParTridentId = new Hashtable<String, StopArea>();
		Map<String, AreaCentroid> centroidParTridentId = new Hashtable<String, AreaCentroid>();
		
		for (PositionGeographique physique : arretsPhysiques) 
		{
			objectIdParArretPhysiqueId.put(physique.getId(), physique.getObjectId());
			StopArea stopArea = getExportStopArea(physique);
			stopAreaParTridentId.put(stopArea.getObjectId(), stopArea);
			chouetteArea.addStopArea(stopArea);
			AreaCentroid areaCentroid = getExportAreaCentroid(physique);
			if (areaCentroid != null) {
				centroidParTridentId.put(areaCentroid.getObjectId(), areaCentroid);
				chouetteArea.addAreaCentroid(areaCentroid);
			}
			if (physique.getIdParent()!=null)
				idsArretsPhysiquesSurZone.add(physique.getId());
		}
		
		for (ArretItineraire arret : arretsToutItineraire) 
		{
			String arretPhysiqueObjectId = objectIdParArretPhysiqueId.get(arret.getIdPhysique());
			arret.setContainedIn(arretPhysiqueObjectId);
			StopArea stopArea = stopAreaParTridentId.get(arretPhysiqueObjectId);
			AreaCentroid centroid = centroidParTridentId.get(stopArea.getCentroidOfArea());
			stopArea.addContains(arret.getObjectId());
			StopPoint stopPoint = arret.getStopPoint();
			stopPoint.setName(stopArea.getName());
			if (centroid != null) 
			{
				stopPoint.setLatitude(centroid.getLatitude());
				stopPoint.setLongitude(centroid.getLongitude());
				stopPoint.setLongLatType(centroid.getLongLatType());
			}
			description.addStopPoint(arret.getStopPoint());
			objectIdParArretLogiqueId.put(arret.getId(), arret.getObjectId());
		}
	}
	
	private StopArea getExportStopArea(PositionGeographique arretPhysique) 
	{
		PositionGeographique copie = new PositionGeographique();
		try 
		{
			PropertyUtils.copyProperties(copie, arretPhysique);
		}
		catch(Exception e) 
		{
			throw new RuntimeException(e);
		}
		String systemIdArretPhysique = lireSystemeOrigine(arretPhysique.getObjectId());
		String areaCentroidId = identificationManager.getIdFonctionnel(systemIdArretPhysique, "Place", arretPhysique);
		StopArea stopArea = copie.getStopArea();
		stopArea.setCentroidOfArea(areaCentroidId);
		retirerStructuresVides(stopArea);
		return stopArea;
	}
	
	private void retirerStructuresVides(StopArea stopArea) 
	{
		if (!PositionGeographique.isExtensionDefinie(stopArea))
			stopArea.setStopAreaExtension(null);
		else if (!PositionGeographique.isRegistrationNumberDefinie(stopArea))
			stopArea.getStopAreaExtension().setRegistration(null);
	}
	
	private ITL getITL(InterdictionTraficLocal itl, Ligne ligne) 
	{
		ITL schemaITL = new ITL();
		schemaITL.setAreaId(itl.getObjectId());
		schemaITL.setLineIdShortCut(ligne.getObjectId());
		schemaITL.setName(itl.getNom());
		return schemaITL;
	}
	
	private StopArea getITLStopArea(InterdictionTraficLocal itl) 
	{
		StopArea area = new StopArea();
		StopAreaExtension extension = new StopAreaExtension();
		extension.setAreaType(ChouetteAreaType.ITL);
		area.setObjectId(itl.getObjectId());
		area.setStopAreaExtension(extension);
		area.setName(itl.getNom());
		for (Long physiqueId : itl.getArretPhysiqueIds()) 
			area.addContains(objectIdParArretPhysiqueId.get(physiqueId));
		return area;
	}

	private AreaCentroid getExportAreaCentroid(PositionGeographique arretPhysique) 
	{
		AreaCentroid areaCentroid = new AreaCentroid();
		try 
		{
			PropertyUtils.copyProperties(areaCentroid, arretPhysique.getAreaCentroid());
		}
		catch(Exception e) 
		{
			throw new RuntimeException(e);
		}
		String systemIdArretPhysique = lireSystemeOrigine(arretPhysique.getObjectId());
		String areaCentroidId = identificationManager.getIdFonctionnel(systemIdArretPhysique, "Place", arretPhysique);
		areaCentroid.setObjectId(areaCentroidId);
		areaCentroid.setObjectVersion(arretPhysique.getObjectVersion());
		areaCentroid.setCreationTime(arretPhysique.getStopArea().getCreationTime());
		areaCentroid.setCreatorId(arretPhysique.getStopArea().getCreatorId());
		areaCentroid.setName(arretPhysique.getName());
		areaCentroid.setContainedIn(arretPhysique.getObjectId());
		if (areaCentroid.getAddress() != null) 
		{
			boolean noCountryCode = isBlank(areaCentroid.getAddress().getCountryCode());
			boolean noStreetName = isBlank(areaCentroid.getAddress().getStreetName());
			if (noCountryCode && noStreetName)
				areaCentroid.setAddress(null);
			else 
			{
				if (noCountryCode)
					areaCentroid.getAddress().setCountryCode(null);
				if (noStreetName)
					areaCentroid.getAddress().setStreetName(null);
			}
		}
		if (areaCentroid.getProjectedPoint() != null && areaCentroid.getProjectedPoint().getX() == null && areaCentroid.getProjectedPoint().getY()==null)
			areaCentroid.setProjectedPoint(null);
		return areaCentroid;
	}
	
	private boolean isBlank(String string) 
	{
		return string == null || string.isEmpty();
	}
	
	private String lireSystemeOrigine(String objectId) 
	{
		if (objectId == null)
			return null;
		try {
			return ObjectIdLecteur.lirePartieSysteme(objectId);
		}
		catch(Exception e) { 
			throw new ServiceException(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.MALFORMED_ID, objectId);
		}
	}
	

	private List<PtLink> getPtLinkArray(List<ArretItineraire> arrets) 
	{
		if (arrets == null)
			return new ArrayList<PtLink>();
		int totalTroncon = arrets.size()-1;
		if (totalTroncon < 1)
			return new ArrayList<PtLink>(0);
		ArrayList<PtLink> troncons = new ArrayList<PtLink>(totalTroncon);
		for (int i = 0; i < totalTroncon; i++) 
			troncons.add(creerTroncon(arrets.get(i), arrets.get(i+1)));
		return troncons;
	}
	
	
	private PtLink creerTroncon(ArretItineraire debut, ArretItineraire fin) 
	{
		String objectId = identificationManager.getIdFonctionnel(systemId, "PtLink", debut.getId()+SEPARATOR+fin.getId());
		PtLink ptLink = new PtLink();
		ptLink.setObjectId(objectId);
		ptLink.setStartOfLink(debut.getObjectId());
		ptLink.setEndOfLink(fin.getObjectId());
		return ptLink;
	}
	

	public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager) 
	{
		this.correspondanceManager = correspondanceManager;
	}
	
	
	public void setItineraireManager(IItineraireManager itineraireManager) 
	{
		this.itineraireManager = itineraireManager;
	}
	

	public void setLigneManager(ILigneManager ligneManager) 
	{
		this.ligneManager = ligneManager;
	}
	
	

	public void setReseauManager(IReseauManager reseauManager) 
	{
		this.reseauManager = reseauManager;
	}


	
	public void setTableauMarcheManager(ITableauMarcheManager tableauMarcheManager) 
	{
		this.tableauMarcheManager = tableauMarcheManager;
	}
	


	public void setTransporteurManager(ITransporteurManager transporteurManager) 
	{
		this.transporteurManager = transporteurManager;
	}
	

	public void setIdentificationManager(IIdentificationManager identificationManager) 
	{
		this.identificationManager = identificationManager;
	}
	

	public IPositionGeographiqueManager getPositionGeographiqueManager() 
	{
		return positionGeographiqueManager;
	}
	

	public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) 
	{
		this.positionGeographiqueManager = positionGeographiqueManager;
	}
	

	public void setMissionManager(IMissionManager missionManager) 
	{
		this.missionManager = missionManager;
	}
	

	public void setItlManager(IITLManager itlManager) 
	{
		this.itlManager = itlManager;
	}
	
	
	private class ComparatorHoraire<T extends Horaire> implements Comparator<Horaire> 
	{
		
		public int compare(Horaire o1, Horaire o2) {
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;
			int deltaCourse = (int)(o2.getIdCourse()-o1.getIdCourse());
			if (deltaCourse != 0)
				return deltaCourse;
			return positionParArretId.get(o1.getIdArret())-positionParArretId.get(o2.getIdArret());
		}
	}
}
