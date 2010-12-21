package fr.certu.chouette.service.database.impl;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.NotClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class LigneManager implements ILigneManager {
	
	private              ITemplateDao<Ligne>     ligneDao;
	private              ISelectionSpecifique    selectionSpecifique;
	private              IModificationSpecifique modificationSpecifique;
	private              IIdentificationManager  identificationManager;
	private              IItineraireManager      itineraireManager;
	
	public Ligne getLigneParRegistration(String registrationNumber) 
	{
		return selectionSpecifique.getLigneParRegistration(registrationNumber);
	}
	
	public List<Itineraire> getItinerairesLigne(Long idLigne) 
	{
		return selectionSpecifique.getItinerairesLigne(idLigne);
	}
	
	public List<Itineraire> getLigneItinerairesExportables(Long idLigne) 
	{
		return selectionSpecifique.getLigneItinerairesExportables(idLigne);
	}
	
	public void modifier(Ligne ligne) 
	{
		ligneDao.update(ligne);
	}

	public void creer(Ligne ligne) 
	{
		ligneDao.save(ligne);
		String objectId = identificationManager.getIdFonctionnel("Line", ligne);
		ligne.setObjectId(objectId);
		ligne.setCreationTime(new Date());
		ligne.setObjectVersion(1);
		ligneDao.update(ligne);
	}

	public Ligne lire(Long idLigne) 
	{
		return ligneDao.get(idLigne);
	}

	public List<Ligne> lire() 
	{
		List<Ordre> ordres = new ArrayList<Ordre>();
		ordres.add(new Ordre("name", true));
		return ligneDao.select(null, ordres);
	}
	
	public boolean nomConnu(String name) 
	{
		return ligneDao.select(ScalarClause.newEqualsClause("name", name)).size() > 0;
	}

	public boolean nomConnu(Long id,String name) 
	{
		IClause clauseID = ScalarClause.newEqualsClause("id", id);
		IClause notClause = new NotClause(clauseID);
		IClause andClause = new AndClause(notClause,ScalarClause.newEqualsClause("name", name));
		return ligneDao.select(andClause).size() > 0;
	}
	public List<Ligne> filtrer(Collection<Long> idReseaux, Collection<Long> idTransporteurs) 
	{
		return selectionSpecifique.getLignesFiltrees(idReseaux, idTransporteurs);
	}

	public List<Ligne> getLignes(final Collection<Long> idLignes) 
	{
		return selectionSpecifique.getLignes(idLignes);
	}
	
	public List<PositionGeographique> getArretsPhysiques(Long idLigne) 
	{
		return selectionSpecifique.getArretPhysiqueLigne(idLigne);
	}

	public void supprimer(Long idLigne, boolean detruireAvecTMs, boolean detruireAvecArrets, boolean detruireAvecTransporteur, boolean detruireAvecReseau) {
		modificationSpecifique.supprimerLigne(idLigne, detruireAvecTMs, detruireAvecArrets, detruireAvecTransporteur, detruireAvecReseau);
	}
	
	public void supprimer(Long idLigne) 
	{
		supprimer(idLigne, false, false, false, false);
	}
	
	public IItineraireManager getItineraireManager() 
	{
		return itineraireManager;
	}
	
	public void setItineraireManager(IItineraireManager itineraireManager) 
	{
		this.itineraireManager = itineraireManager;
	}
	
	public ITemplateDao<Ligne> getLigneDao() 
	{
		return ligneDao;
	}
	
	public void setLigneDao(ITemplateDao<Ligne> ligneDao) 
	{
		this.ligneDao = ligneDao;
	}
	
	public ISelectionSpecifique getSelectionSpecifique() 
	{
		return selectionSpecifique;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) 
	{
		this.selectionSpecifique = selectionSpecifique;
	}

	public IModificationSpecifique getModificationSpecifique() 
	{
		return modificationSpecifique;
	}

	public void setModificationSpecifique(IModificationSpecifique modificationSpecifique) 
	{
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setIdentificationManager(IIdentificationManager identificationManager) 
	{
		this.identificationManager = identificationManager;
	}

	public List<Ligne> select(IClause clause) 
	{
		return ligneDao.select(clause);
	}
}
