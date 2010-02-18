package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ReseauManager implements IReseauManager
{
	private final Log log = LogFactory.getLog(ReseauManager.class);
	
	private ITemplateDao<Reseau> reseauDao;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#getLignesReseau(java.lang.Long)
	 */
	public List<Ligne> getLignesReseau( final Long idReseau) 
	{
		return selectionSpecifique.getLignesReseau(idReseau);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#modifier(fr.certu.chouette.modele.Reseau)
	 */
	public void modifier( Reseau reseau)
	{
		reseauDao.update( reseau);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#creer(fr.certu.chouette.modele.Reseau)
	 */
	public void creer( Reseau reseau)
	{
		reseauDao.save( reseau);
		String objectId = identificationManager.getIdFonctionnel("PtNetwork", reseau);
		reseau.setObjectId(objectId);
		reseau.setCreationTime( new Date());
		reseau.setObjectVersion(1);
		reseauDao.update( reseau);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#lire(java.lang.Long)
	 */
	public Reseau lire( final Long idReseau)
	{
		return reseauDao.get( idReseau);
	}

	public List<Reseau> getReseaux( final Collection<Long> idReseaux) {
		return selectionSpecifique.getReseaux(idReseaux);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#lire()
	 */
	public List<Reseau> lire() {
		List<Ordre> ordres = new ArrayList<Ordre>();
		ordres.add(new Ordre( "name", true));
		return reseauDao.select(null, ordres);
	}

	public List<Reseau> select (IClause clause) {
		return reseauDao.select(clause);
	}

	public List<Reseau> select(IClause clause, Collection<Ordre> ordres) {
		return reseauDao.select(clause, ordres);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ReseauManager#supprimer(java.lang.Long)
	 */
	public void supprimer( final Long idReseau)
	{
		modificationSpecifique.supprimerReseau( idReseau);
	}

	public ITemplateDao<Reseau> getReseauDao() {
		return reseauDao;
	}

	public void setReseauDao(ITemplateDao<Reseau> reseauDao) {
		this.reseauDao = reseauDao;
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

	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
