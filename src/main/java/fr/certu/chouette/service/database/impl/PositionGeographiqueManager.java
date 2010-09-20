package fr.certu.chouette.service.database.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class PositionGeographiqueManager implements IPositionGeographiqueManager 
{
	private static final Log log = LogFactory.getLog(PositionGeographiqueManager.class);
	
	public ITemplateDao<PositionGeographique> positionGeographiqueDao;
	public ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;
	private ICorrespondanceManager correspondanceManager;

	private int profondeurMaximum;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#creer(fr.certu.chouette.modele.interne.PositionGeographique)
	 */
	public void creer( PositionGeographique positionGeo)
	{
		positionGeographiqueDao.save( positionGeo);
		String objectId = identificationManager.getIdFonctionnel("StopArea", positionGeo);
		positionGeo.setObjectId(objectId);
		positionGeo.setCreationTime( new Date());
		positionGeo.setObjectVersion( 1);
		positionGeographiqueDao.update( positionGeo);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#modifier(fr.certu.chouette.modele.interne.PositionGeographique)
	 */
	public void modifier( PositionGeographique positionGeo)
	{
		positionGeographiqueDao.update( positionGeo);
	}
	

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#lire(java.lang.Long)
	 */
	public PositionGeographique lire( Long idPositionGeo)
	{
		return positionGeographiqueDao.get( idPositionGeo);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#lireParObjectId(java.lang.String)
	 */
	public PositionGeographique lireParObjectId( String objectId)
	{
		return positionGeographiqueDao.getByObjectId(objectId);
	}
	
	public List<PositionGeographique> select(final IClause clause) {
		return positionGeographiqueDao.select(clause);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getPresenceItineraireParPhysiqueId()
	 */
	public Map<Long, Boolean> getPresenceItineraireParPhysiqueId()
	{
		Map<Long, Boolean> presenceItineraireParPhysiqueId = new Hashtable<Long, Boolean>();
		List<PositionGeographique> physiques = lire();
		List<Long> physiquesAvecItineraire = selectionSpecifique.getPhysiqueIdAvecItineraire();
		
		for (PositionGeographique arretPhysique : physiques) 
		{
			presenceItineraireParPhysiqueId.put(arretPhysique.getId(), physiquesAvecItineraire.contains( arretPhysique.getId()));
		}
		return presenceItineraireParPhysiqueId;
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getArretPhysiqueParIdArret(java.util.Collection)
	 */
	public Map<Long, PositionGeographique> getArretPhysiqueParIdArret( Collection<ArretItineraire> arrets)
	{
		Map<Long, PositionGeographique> arretPhysiqueParIdArret = new Hashtable<Long, PositionGeographique>();
		
		if ( arrets==null || arrets.isEmpty())
		{
			return arretPhysiqueParIdArret;
		}
		
		Collection<Long> idsArretsPhysiques = new HashSet<Long>();
		for (ArretItineraire arret : arrets)
		{
			if (arret.getIdPhysique() != null) idsArretsPhysiques.add(arret.getIdPhysique());
		}
		// CrÃ©ation de la liste des arrets physique Ã  partir de la liste des
		// identfiants des arrets physique
		List<PositionGeographique> positionsGeographiques = selectionSpecifique.getGeoPositions(idsArretsPhysiques, new Ordre("name",true));

		// CrÃ©ation d'une map liant id Ligne -> Objet Ligne
		Map<Long, PositionGeographique> arretPhysiqueParId = new Hashtable<Long, PositionGeographique>();
		for (PositionGeographique geoPosition : positionsGeographiques)
		{
			arretPhysiqueParId.put(geoPosition.getId(), geoPosition);
		}
		// CrÃ©ation d'une hashtable liant id Itineraire -> Objet Ligne
		for (ArretItineraire arret : arrets)
		{
			if (arret.getIdPhysique() != null)
			{
				PositionGeographique arretPhysique = arretPhysiqueParId.get(arret.getIdPhysique());
				arretPhysiqueParIdArret.put(arret.getId(), arretPhysique);
			}
		}		
		return arretPhysiqueParIdArret;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#lire()
	 */
	public List<PositionGeographique> lire()
	{
		return selectionSpecifique.getGeoPositions();
	}
	
	public List<Itineraire> getItinerairesArretPhysique(Long idPhysique) {
		return selectionSpecifique.getItinerairesParGeoPosition(idPhysique);
	}
	
	public List<PositionGeographique> lireZones()
	{
		Collection<ChouetteAreaType> areas = new HashSet<ChouetteAreaType>();
		areas.add( ChouetteAreaType.COMMERCIALSTOPPOINT);
		areas.add( ChouetteAreaType.STOPPLACE);

		return lire( areas);
	}

	public List<PositionGeographique> lireArretsPhysiques()
	{
		Collection<ChouetteAreaType> areas = new HashSet<ChouetteAreaType>();
		areas.add( ChouetteAreaType.QUAY);
		areas.add( ChouetteAreaType.BOARDINGPOSITION);

		return lire( areas);
	}

	public List<PositionGeographique> lire( Collection<ChouetteAreaType> areas)
	{
		List<PositionGeographique> positionsGeo = new ArrayList<PositionGeographique>();
		List<PositionGeographique> positionsGeographiques = selectionSpecifique.getGeoPositions();
		
		if ( positionsGeographiques==null) 
			return positionsGeo;
		
		for (PositionGeographique geoPosition : positionsGeographiques) {
			// filtrer sur le type
			for (ChouetteAreaType area : areas) {
				if ( area.equals( geoPosition.getAreaType()))
				{
					positionsGeo.add( geoPosition);
					break;
				}
			}
		}
		return positionsGeo;
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#supprimer(java.lang.Long)
	 */
	public void supprimer( Long idPositionGeo)
	{
		PositionGeographique positionGeo = lire( idPositionGeo);
		
		if ( isArretPhysique( positionGeo))
		{
			List<ArretItineraire> arrets = selectionSpecifique.getArretsItineraireParGeoPosition( idPositionGeo);
			if ( arrets.size()>0)
				throw new ServiceException( CodeIncident.ERR_ARRETS_RELIES, CodeDetailIncident.DEFAULT,arrets.size());
			
			Collection<Long> idGeoPositions = new HashSet<Long>();
			idGeoPositions.add( idPositionGeo);
			modificationSpecifique.dissocierITLGeoPosition(idGeoPositions);
		}
		modificationSpecifique.dissocierGeoPositionsContenues( idPositionGeo);
		
		Collection<Long> positionIds = new ArrayList<Long>(1);
		positionIds.add( idPositionGeo);
		List<Correspondance> correspondances = correspondanceManager.selectionParPositions(positionIds);
		for (Correspondance correspondance : correspondances) {
			correspondanceManager.supprimer( correspondance.getId());
		}
		
		positionGeographiqueDao.remove( idPositionGeo);		
	}
	
	private boolean isArretPhysique( PositionGeographique positionGeographique)
	{
		return ChouetteAreaType.QUAY.equals( positionGeographique.getAreaType())
			|| ChouetteAreaType.BOARDINGPOSITION.equals( positionGeographique.getAreaType());
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getHorairesArret(java.lang.Long)
	 */
	public List<Horaire> getHorairesArret(Long idPhysique) {
		return selectionSpecifique.getHorairesArretItineraire(idPhysique);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getArretsPhysiques(java.util.Collection)
	 */
	public List<PositionGeographique> getArretsPhysiques(final Collection<Long> idPhysiques) {
		return selectionSpecifique.getGeoPositions(idPhysiques, new Ordre("name",true));
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#getGeoPositionsParentes(java.lang.Long)
	 */
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getGeoPositionsParentes(java.lang.Long)
	 */
	public List<PositionGeographique> getGeoPositionsParentes(final Long idGeoPositionParente) {
		return selectionSpecifique.getGeoPositionsParentes(idGeoPositionParente);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#getGeoPositionsDirectementContenues(java.lang.Long)
	 */
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getGeoPositionsDirectementContenues(java.lang.Long)
	 */
	public List<PositionGeographique> getGeoPositionsDirectementContenues(Long idParent) {
		return selectionSpecifique.getGeoPositionsDirectementContenues(idParent);
	}

	private int getProfondeur( Long id)
	{
		List<PositionGeographique> zonesContenues = getGeoPositionsDirectementContenues( id);
		if ( zonesContenues==null || zonesContenues.isEmpty()) return 0;
		
		int profondeurEnfants = 0;
		for (PositionGeographique geographique : zonesContenues) {
			int profondeurEnfant = getProfondeur( geographique.getId());
			if ( profondeurEnfants<profondeurEnfant) profondeurEnfants = profondeurEnfant;
		}
		
		return profondeurEnfants + 1;
	}

//	private List<IPositionGeographique> getGeoPositionContenues( Long id)
//	{
//		List<IPositionGeographique> zonesContenues = getGeoPositionsDirectementContenues( id);
//		if ( zonesContenues==null || zonesContenues.isEmpty()) return new ArrayList<IPositionGeographique>();
//		
//		for (IPositionGeographique geographique : zonesContenues) {
//			zonesContenues.addAll( getGeoPositionContenues( geographique.getId()));
//		}
//		
//		return zonesContenues;
//	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#associerGeoPositions(java.lang.Long, java.lang.Long)
	 */
	public void associerGeoPositions(Long idContenant, Long idContenue) {

		// controler la compatibilitÃ© contenant - contenu
		PositionGeographique contenant = positionGeographiqueDao.get( idContenant);
		PositionGeographique contenu = positionGeographiqueDao.get( idContenue);
		
		if ( !contenant.canContain( contenu.getAreaType()))
		{
			throw new ServiceException( CodeIncident.ASSOCIATION_ZONES_INVALIDE,CodeDetailIncident.DEFAULT,idContenant,contenant.getAreaType(),idContenue,contenu.getAreaType());
		}
		
		List<PositionGeographique> parentsContenant = getGeoPositionsParentes( idContenant);
		Set<Long> idParentsContenant = getIdSet( parentsContenant);
		
		List<PositionGeographique> parentsContenu = getGeoPositionsParentes( idContenue);
		Set<Long> idParentsContenue = getIdSet( parentsContenu);
		
		if ( idParentsContenue.removeAll( idParentsContenant))
		{
			throw new ServiceException( CodeIncident.RECURSIVITE_INTER_ZONES,CodeDetailIncident.DEFAULT);
		}
		
		if ( profondeurMaximum <= parentsContenant.size()+getProfondeur( idContenue))
		{
			throw new ServiceException( CodeIncident.PROFONDEUR_ZONES_INVALIDE,CodeDetailIncident.DEFAULT,(parentsContenant.size()-1),idContenue,getProfondeur( idContenue),profondeurMaximum );
		}

		
		modificationSpecifique.associerGeoPositions(idContenant, idContenue);
	}
	
	private Set<Long> getIdSet(List<PositionGeographique> positions)
	{
		Set<Long> ids = new HashSet<Long>();
		for (PositionGeographique positionGeographique : positions) {
			ids.add( positionGeographique.getId());
		}
		return ids;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPosition(java.lang.Long)
	 */
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#dissocierGeoPosition(java.lang.Long)
	 */
	public void dissocierGeoPosition(Long idGeoPosition) {
		modificationSpecifique.dissocierGeoPosition(idGeoPosition);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPositionParente(java.lang.Long)
	 */
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#dissocierGeoPositionParente(java.lang.Long)
	 */
	public void dissocierGeoPositionParente(Long idContenue) {
		modificationSpecifique.dissocierGeoPositionParente(idContenue);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPositionsContenues(java.lang.Long)
	 */
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#dissocierGeoPositionsContenues(java.lang.Long)
	 */
	public void dissocierGeoPositionsContenues(Long idContenant) {
		modificationSpecifique.dissocierGeoPositionsContenues(idContenant);
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}

	public void setPositionGeographiqueDao(ITemplateDao<PositionGeographique> positionGeographiqueDao) {
		this.positionGeographiqueDao = positionGeographiqueDao;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager) {
		this.correspondanceManager = correspondanceManager;
	}

	public void setModificationSpecifique(IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.IPositionGeographiqueManager#getProfondeurMaximum()
	 */
	public int getProfondeurMaximum() {
		return profondeurMaximum;
	}

	public void setProfondeurMaximum(int profondeurMaximum) {
		this.profondeurMaximum = profondeurMaximum;
	}

	public void fusionnerPositionsGeographiques(Long idPositionSource, Long idPositionDestination) {
		
		// REMPLACER LES IDS PHYSIQUES DE TOUT LES STOP-POINT POINTANT SUR LA SOURCE 
		// PAR L'ID DE LA DESTINATION
		modificationSpecifique.substituerArretPhysiqueDansArretsItineraireAssocies(idPositionSource, idPositionDestination);
		
		// REMPLACER LES IDS PHYSIQUES DE TOUT LES ITLs POINTANT SUR LA SOURCE 
		// PAR L'ID DE LA DESTINATION
		modificationSpecifique.substituerArretPhysiqueDansITLsAssocies(idPositionSource, idPositionDestination);
		
		// SUPPRIMER LE STOP-AREA SOURCE
		modificationSpecifique.supprimerGeoPosition(idPositionSource);
	}

	public List<PositionGeographique> lire(String nomArret, String code, Long idReseau, List <ChouetteAreaType> areaTypes) {
		return selectionSpecifique.getArretsFiltres(nomArret, code, idReseau, areaTypes);
	}

    public void setBounds(BigDecimal maxLat, BigDecimal maxLong, BigDecimal minLat, BigDecimal minLong) {
        List<PositionGeographique> stopAreas = lire();
        maxLat  = null;
        maxLong = null;
        minLat = null;
        minLong = null;
        for (PositionGeographique stopArea : stopAreas) {
            BigDecimal latitude = stopArea.getLatitude();
            BigDecimal longitude = stopArea.getLongitude();
            if ((latitude == null) || (longitude == null))
                continue;
            if (maxLat == null) {
                maxLat = latitude;
                minLat = latitude;
                maxLong = longitude;
                maxLong = longitude;
            }
            else {
                maxLat = latitude.max(maxLat);
                minLat = latitude.min(minLat);
                maxLong = longitude.max(maxLong);
                minLong = longitude.min(minLong);
            }
        }
    }
}