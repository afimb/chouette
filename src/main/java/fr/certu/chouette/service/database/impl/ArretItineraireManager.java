package fr.certu.chouette.service.database.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IArretItineraireManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ArretItineraireManager implements IArretItineraireManager
{
	private ITemplateDao<ArretItineraire> arretItineraireDao;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;

	public void modifier( ArretItineraire arret)
	{
		arretItineraireDao.update( arret);
	}

	public void creer( ArretItineraire arret)
	{
		arret.setCreationTime( new java.util.Date());
		arretItineraireDao.save( arret);
		String objectId = identificationManager.getIdFonctionnel("StopPoint", arret);
		arret.setObjectId(objectId);
		arret.setCreationTime( new Date());
		arret.setObjectVersion( 1);
		arretItineraireDao.update( arret);
	}

	public ArretItineraire lire( Long idArret)
	{
		return arretItineraireDao.get( idArret);
	}

	public List<ArretItineraire> lire()
	{
		return arretItineraireDao.getAll();
	}

	public void supprimer( Long idArret)
	{
		ArretItineraire arret = lire(idArret);
		
		modificationSpecifique.supprimerArretItineraire( idArret);
		modificationSpecifique.referencerDepartsCourses(arret.getIdItineraire());
	}

	public List<Horaire> getHorairesArret(Long idArret) {
		return selectionSpecifique.getHorairesArretItineraire(idArret);
	}

	public List<PositionGeographique> getArretsPhysiques(final Collection<Long> idPhysiques, Ordre ordre) {
		return selectionSpecifique.getGeoPositions(idPhysiques,ordre);
	}

	public void setArretItineraireDao(
			ITemplateDao<ArretItineraire> arretItineraireDao) {
		this.arretItineraireDao = arretItineraireDao;
	}

	public void setArretDao(ITemplateDao<ArretItineraire> arretDao) {
		this.arretItineraireDao = arretDao;
	}

	public ISelectionSpecifique getSelectionSpecifique() {
		return selectionSpecifique;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public IModificationSpecifique getModificationSpecifique() {
		return modificationSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
}
