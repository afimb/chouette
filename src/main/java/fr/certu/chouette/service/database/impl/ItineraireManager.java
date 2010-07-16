package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IArretItineraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.impl.modele.EnumMaj;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import fr.certu.chouette.service.database.impl.modele.ReferenceArretPhysique;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ItineraireManager implements IItineraireManager
{
    private static final Logger logger = Logger.getLogger( ItineraireManager.class);
	private ITemplateDao<Itineraire> itineraireDao;
	private IArretItineraireManager arretItineraireManager;
	private IPositionGeographiqueManager positionGeographiqueManager;
	private IMissionManager missionManager;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;

	public void associerItineraire(Long idRoute1, Long idRoute2) {
		modificationSpecifique.associerItineraire(idRoute1, idRoute2);
	}

	public void dissocierItineraire(Long idRoute1) {
		modificationSpecifique.dissocierItineraire(idRoute1);
	}

	public List<TableauMarche> getTableauxMarcheItineraire(Long idItineraire) {
		return selectionSpecifique.getTableauxMarcheItineraire(idItineraire);
	}
	
	public List<TableauMarche> getTableauxMarcheItineraires(Collection<Long> idItineraires) {
		return selectionSpecifique.getTableauxMarcheItineraires(idItineraires);
	}
  public Map<Long,String> getCommentParTMId(final Long idItineraire) {
		return selectionSpecifique.getCommentParTMId(idItineraire);
  }


	public List<Course> getCoursesItineraire(Long idItineraire) {
		return selectionSpecifique.getCoursesItineraire(idItineraire);
	}

	public List<ArretItineraire> getArretsItineraire(Long idItineraire) {
		return selectionSpecifique.getArretsItineraire(idItineraire);
	}
	
	public List<PositionGeographique> getArretPhysiqueItineraire(
			Long idItineraire) {
		return selectionSpecifique.getArretPhysiqueItineraire(idItineraire);
	}

	public List<Mission> getMissionsItineraire(Long idItineraire) {
		return selectionSpecifique.getMissionsItineraire(idItineraire);
	}
	
	public List<Mission> getMissionsItineraires(Collection<Long> idItineraires) {
		return selectionSpecifique.getMissionsItineraires(idItineraires);
	}

	public List<Horaire> getHorairesItineraire(Long idItineraire) {
		return selectionSpecifique.getHorairesItineraire(idItineraire);
	}

	public List<Horaire> getHorairesItineraires(Collection<Long> idItineraires) {
		return selectionSpecifique.getHorairesItineraires(idItineraires);
	}

	public List<ArretItineraire> getArretsItineraires( final Collection<Long> idItineraires)
	{
		return selectionSpecifique.getArretsItineraires( idItineraires);
	}
	
	public List<Course> getCoursesItineraires(Collection<Long> idItineraires) {
		return selectionSpecifique.getCoursesItineraires(idItineraires);
	}

	private Map<Long, List<ArretItineraire>> getArretsLogiquesParIdPhysique(Long idItineraire)
	{
		Map<Long, List<ArretItineraire>> resultat = new Hashtable<Long, List<ArretItineraire>>();
		List<ArretItineraire> arretsLogiques = getArretsItineraire( idItineraire);
		
		for (ArretItineraire arretLogique : arretsLogiques) 
		{
			Long idPhysique = arretLogique.getIdPhysique();
			List<ArretItineraire> suiteArretsLogiques = resultat.get( idPhysique);
			if ( suiteArretsLogiques==null)
			{
				suiteArretsLogiques = new ArrayList<ArretItineraire>();
				resultat.put( idPhysique, suiteArretsLogiques);
			}
			suiteArretsLogiques.add( arretLogique);
		}
		
		return resultat;
	}
	
	private Map<Long, List<Integer>> getPositionsParIdPhysiques( final List<ReferenceArretPhysique> arretsPhysiques)
	{
		final Map<Long, List<Integer>> resultat = new Hashtable<Long, List<Integer>>();
		final int totalArrets = arretsPhysiques.size();
		
		for (int i = 0; i < totalArrets; i++) 
		{
			final ReferenceArretPhysique physique = arretsPhysiques.get( i);
			final Long idPhysique = physique.getId();
			final List<Integer> positions = resultat.get( idPhysique);
			if ( positions==null)
			{
				resultat.put(idPhysique, new ArrayList<Integer>());
			}
			positions.add( Integer.valueOf( i));
		}
		
		return resultat;
	}
	
	public void creerItineraireRetour(Long idItineraire)
	{
		Itineraire aller = lire(idItineraire);
		if(aller.getIdRetour() == null)
		{
			Itineraire retour = new Itineraire();
			retour.setIdLigne(aller.getIdLigne());
			retour.setIdRetour( aller.getId());
			retour.setName( "Retour: "+aller.getName());
			retour.setPublishedName( "Retour: "+aller.getPublishedName());
			retour.setWayBack( aller.getWayBack().equals("A")?"R":"A");
			creer( retour);
			
			aller.setIdRetour( retour.getId());
			modifier( aller);

			// creating backword route stop points
			List<ArretItineraire> allerArrets = getArretsItineraire(idItineraire);
			int total = allerArrets.size()-1;
			for (ArretItineraire arretAller : allerArrets) {
				ArretItineraire arretRetour = new ArretItineraire();
				arretRetour.setIdPhysique( arretAller.getIdPhysique());
				arretRetour.setIdItineraire( retour.getId());
				arretRetour.setPosition( total-arretAller.getPosition());
				arretItineraireManager.creer( arretRetour);
			}
		}
	}
	
	public void modifierArretsItineraire(Long idItineraire, List<EtatMajArretItineraire> majArretsItineraire)
	{
		List<Long> idsLogiquesPerdus = new ArrayList<Long>();
		List<Long> arretsDeplaces = new ArrayList<Long>();
		Map<Integer, Long> arretsDeplacesParPosition = new Hashtable<Integer, Long>();
		Map<Integer, Long> arretsInitiauxParPosition = new Hashtable<Integer, Long>();
		
		List<Integer> positions = new ArrayList<Integer>();
		
		List<ArretItineraire> arrets = selectionSpecifique.getArretsItineraire(idItineraire);
		int totalInitialArrets = arrets.size();
		
		List<EtatMajArretItineraire> creationArretItineraire = new ArrayList<EtatMajArretItineraire>();
		List<EtatMajArretItineraire> deplacementArretItineraire = new ArrayList<EtatMajArretItineraire>();
		
		for (ArretItineraire arret : arrets) {
			arretsInitiauxParPosition.put( arret.getPosition(), arret.getId());
		}
		
		for (EtatMajArretItineraire etatMaj : majArretsItineraire) 
		{
			if ( EnumMaj.SUPPRIMER.equals( etatMaj.getEnumMaj()))
			{
				idsLogiquesPerdus.add( etatMaj.getIdArretLogique());
			}
			else if ( EnumMaj.CREER.equals( etatMaj.getEnumMaj()))
			{
				creationArretItineraire.add( etatMaj);
			}
			else if ( EnumMaj.DEPLACER.equals( etatMaj.getEnumMaj()))
			{
				deplacementArretItineraire.add( etatMaj);
			}
			else
			{
				assert false : "etat de la mise à jour inconnu";
			}
		}
		
		for (EtatMajArretItineraire etatMaj : deplacementArretItineraire) 
		{
			int positionMaximum = totalInitialArrets-idsLogiquesPerdus.size()+creationArretItineraire.size();
			
			int positionCible = etatMaj.getNouvellePosition();
			if ( positionCible<0 || positionCible>=positionMaximum)
			{
				throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_STOPPOINTPOSITION, positionCible,totalInitialArrets,idsLogiquesPerdus.size(),creationArretItineraire.size());
			}
			
			// déplacer l'arrêt sur l'itinéraire
			positions.add( positionCible);
			arretsDeplaces.add( etatMaj.getIdArretLogique());
			
			// enregistrer la nouvelle position
			arretsDeplacesParPosition.put( positionCible, etatMaj.getIdArretLogique());
		}

		modificationSpecifique.supprimerArretsItineraire( idsLogiquesPerdus);

		validerModificationItineraire(arretsDeplaces, positions, arretsInitiauxParPosition, idsLogiquesPerdus);
		
		// repérer les arrets d'itineraires à replacer
		modificationSpecifique.echangerPositions(arretsDeplaces, positions);
		
		// repérer les horaires à échanger
		List<Long> arretsHorairesEchanges = new ArrayList<Long>();
		List<Integer> positionsTriees = new ArrayList<Integer>( arretsDeplacesParPosition.keySet());
		Collections.sort( positionsTriees);
		
		for (Integer position : positionsTriees) {
			arretsHorairesEchanges.add( arretsDeplacesParPosition.get( position));
		}
		
		List<Long> arretsHorairesEchangesInitiaux = new ArrayList<Long>();
		for (ArretItineraire arret : arrets) {
			if ( arretsDeplacesParPosition.values().contains( arret.getId()))
			{
				arretsHorairesEchangesInitiaux.add( arret.getId());
			}
		}
		
		validerEchangeHoraire(arretsHorairesEchanges, arretsHorairesEchangesInitiaux);
		
		
		List<Long> idArretAvantEchange = new ArrayList<Long>();
		List<Long> idArretApresEchange = new ArrayList<Long>();
		for (int i = 0; i < arretsHorairesEchangesInitiaux.size(); i++) 
		{
			if ( !arretsHorairesEchangesInitiaux.get( i).equals( arretsHorairesEchanges.get( i)))
			{
				idArretAvantEchange.add( arretsHorairesEchangesInitiaux.get( i));
				idArretApresEchange.add( arretsHorairesEchanges.get( i));
			}
		}
		modificationSpecifique.echangerHoraires(idArretAvantEchange, idArretApresEchange);
		
		// création des nouveaux arrets
		for (EtatMajArretItineraire etatMaj : creationArretItineraire) 
		{
			// définir l'arrêt physique
			creerArretItineraire(idItineraire, etatMaj);		
		}
		
		// des horaires peuvent avoir ete supprimes
		modificationSpecifique.referencerDepartsCourses(idItineraire);

		// Updating Journey Patterns
		if ( !idsLogiquesPerdus.isEmpty())
		{
			missionManager.fusionnerMissions(idItineraire);
		}
	}

	private void validerEchangeHoraire(List<Long> arretsHorairesEchanges, 
			List<Long> arretsHorairesEchangesInitiaux) {
		if ( arretsHorairesEchangesInitiaux.size() != arretsHorairesEchanges.size())
		{
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_SWAPVEHICLEJOURNEYATSTOP);
		}
	}

	private void validerModificationItineraire(List<Long> arretsDeplaces, 
			List<Integer> positions,
			Map<Integer, Long> arretInitiauxParPosition,
			List<Long> arretsRetires) {
		
		List<Integer> emplacementNonLiberes = new ArrayList<Integer>();
		for (Integer position : positions) {
			Long idArretInitial = arretInitiauxParPosition.get( position);
			if ( idArretInitial!=null && !arretsDeplaces.contains( idArretInitial)
					&& !arretsRetires.contains( idArretInitial))
			{
				emplacementNonLiberes.add( position);
			}
		}
		if ( !emplacementNonLiberes.isEmpty())
		{
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.ROUTE_STOPPOINTFREEPOSITION, emplacementNonLiberes);
		}
		
		// tester si répétition de position
		Set<Integer> positionsDistinctes = new HashSet<Integer>( positions);
		if ( positionsDistinctes.size()!=positions.size())
		{
			List<Integer> positionsRepetees = new ArrayList<Integer>( positions);
			positionsRepetees.removeAll(positions);
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_STOPPOINTDUPLICATEPOSITION,positionsRepetees);
		}
		
		Set<Long> arretsDeplacesDistincts = new HashSet<Long>( arretsDeplaces);
		if ( arretsDeplacesDistincts.size()!=arretsDeplaces.size())
		{
			List<Long> arretsDeplacesRepetes = new ArrayList<Long>( arretsDeplaces);
			arretsDeplacesRepetes.removeAll( arretsDeplacesDistincts);
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_STOPPOINTDUPLICATETARGETSTOPPOINT,arretsDeplacesDistincts);
		}
	}

	private void creerArretItineraire(Long idItineraire, EtatMajArretItineraire etatMaj) {
		PositionGeographique arretPhysique = null;
		if ( etatMaj.getIdArretPhysique()!=null)
		{
			arretPhysique = positionGeographiqueManager.lire( etatMaj.getIdArretPhysique());
		}
		else
		{
			arretPhysique = PositionGeographique.creerArretPhysique( etatMaj.getNom());
			positionGeographiqueManager.creer( arretPhysique);
		}
		
		// créer l'arrêt logique
		ArretItineraire arret = new ArretItineraire();
		arret.setIdPhysique( arretPhysique.getId());
		arret.setIdItineraire( idItineraire);
		arret.setPosition( etatMaj.getNouvellePosition());
		arretItineraireManager.creer( arret);
	}
	
	private void validationDeplacement( List<Long> arretsOrdreInitial, List<Long> arretsOrdreNouveau, List<Integer> nouvellesPositions)
	{
		Set<Integer> positions = new HashSet<Integer>( nouvellesPositions);
		if ( positions.size()!=nouvellesPositions.size()) 
		{
			List<Integer> positionsRepetees = new ArrayList<Integer>( nouvellesPositions);
			positionsRepetees.removeAll(positions);
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_STOPPOINTDUPLICATEPOSITION,positionsRepetees);
		}
		
		Set<Long> arretsCibles = new HashSet<Long>( arretsOrdreNouveau);
		if ( arretsCibles.size()!=arretsOrdreNouveau.size())
		{
			List<Long> arretsCiblesRepetes = new ArrayList<Long>( arretsOrdreNouveau);
			arretsCiblesRepetes.removeAll( arretsCibles);
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_STOPPOINTDUPLICATETARGETSTOPPOINT,arretsCibles);
		}
		
		arretsCibles.removeAll(arretsOrdreInitial);
		if ( arretsCibles.size()>0)
		{
			throw new ServiceException( CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.ROUTE_STOPPOINTCOLLUSIONSTOPPOINT,arretsCibles);
		}
	}


	public void modifier( Itineraire itineraire)
	{
		itineraireDao.update( itineraire);
	}

	public void creer( Itineraire itineraire)
	{
		itineraireDao.save( itineraire);
		String objectId = identificationManager.getIdFonctionnel("Route", itineraire);
		itineraire.setCreationTime( new Date());
		itineraire.setObjectId(objectId);
		itineraire.setObjectVersion(1);
		itineraireDao.update( itineraire);
	}

	public Itineraire lire( Long idItineraire)
	{
		Itineraire resultat = null;
		resultat = itineraireDao.get( idItineraire);
		return resultat;
	}

	public List<Itineraire> lire()
	{
		return itineraireDao.getAll();
	}

	public void supprimer( Long idItineraire)
	{
		modificationSpecifique.dissocierItineraire( idItineraire);
		modificationSpecifique.supprimerItineraire(idItineraire);
	}

	public void setItineraireDao(ITemplateDao<Itineraire> itineraireDao) {
		this.itineraireDao = itineraireDao;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}

	public void setArretItineraireManager(
			IArretItineraireManager arretItineraireManager) {
		this.arretItineraireManager = arretItineraireManager;
	}

	public void setMissionManager(IMissionManager missionManager) {
		this.missionManager = missionManager;
	}

	public void setPositionGeographiqueManager(
			IPositionGeographiqueManager positionGeographiqueManager) {
		this.positionGeographiqueManager = positionGeographiqueManager;
	}

	public List<Course> getCoursesItineraireSelonHeureDepartPremiereCourse(Long idItineraire, Date seuilDateDepartCourse) {
		return this.selectionSpecifique.getCoursesItineraireSelonHeureDepartPremiereCourse(idItineraire, seuilDateDepartCourse);
	}


}
