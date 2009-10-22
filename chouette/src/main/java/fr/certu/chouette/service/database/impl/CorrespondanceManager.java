package fr.certu.chouette.service.database.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.OrClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class CorrespondanceManager implements ICorrespondanceManager 
{
	public ITemplateDao<Correspondance> correspondanceDao;
	public ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;
	

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.ICorrespondanceManager#creer(fr.certu.chouette.modele.Correspondance)
	 */
	public void creer( Correspondance correspondance)
	{
		correspondanceDao.save( correspondance);
		String objectId = identificationManager.getIdFonctionnel("ConnectionLink", correspondance);
		correspondance.setObjectId(objectId);
		correspondance.setCreationTime( new Date());
		correspondance.setObjectVersion( 1);
		correspondanceDao.update( correspondance);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.ICorrespondanceManager#modifier(fr.certu.chouette.modele.Correspondance)
	 */
	public void modifier( Correspondance correspondance)
	{
		correspondanceDao.update( correspondance);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.ICorrespondanceManager#lire(java.lang.Long)
	 */
	public  List<Correspondance> lire()
	{
		return selectionSpecifique.getCorrespondances();
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.ICorrespondanceManager#lire(java.lang.Long)
	 */
	public Correspondance lire( Long idCorrespondance)
	{
		return correspondanceDao.get( idCorrespondance);
	}
	
	public Correspondance lireParObjectId( String objectId)
	{
		return correspondanceDao.getByObjectId(objectId);
	}
	
	public List<Correspondance> selectionParPositions(Collection<Long> positionIds) {
		IClause clauseDepart = VectorClause.newInClause("idDepart", positionIds);
		IClause clauseArrivee = VectorClause.newInClause("idArrivee", positionIds);
		return correspondanceDao.select(new OrClause(clauseDepart, clauseArrivee));
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.impl.ICorrespondanceManager#supprimer(java.lang.Long)
	 */
	public void supprimer( Long idCorrespondance)
	{
		correspondanceDao.remove( idCorrespondance);
	}
	
	
	
	public List<Correspondance> getCorrespondancesParGeoPosition(Long idGeoPosition) {
		return selectionSpecifique.getCorrespondancesParGeoPosition(idGeoPosition);
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setCorrespondanceDao(ITemplateDao<Correspondance> correspondanceDao) {
		this.correspondanceDao = correspondanceDao;
	}
}
