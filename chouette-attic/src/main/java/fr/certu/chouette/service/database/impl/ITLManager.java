package fr.certu.chouette.service.database.impl;

import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ITLManager implements IITLManager 
{
	private ITemplateDao<InterdictionTraficLocal> itlDao;
	private IIdentificationManager identificationManager;
	private ISelectionSpecifique selectionSpecifique;
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#getITLLigne(java.lang.Long)
	 */
	public List<InterdictionTraficLocal> getITLLigne(Long idLigne) {
		return selectionSpecifique.getITLLigne(idLigne);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#creer(fr.certu.chouette.modele.InterdictionTraficLocal)
	 */
	public void creer( InterdictionTraficLocal itl)
	{
		itlDao.save( itl);
		String objectId = identificationManager.getIdFonctionnel("ITL", itl);
		itl.setObjectId(objectId);
		itlDao.update( itl);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#modifier(fr.certu.chouette.modele.InterdictionTraficLocal)
	 */
	public void modifier( InterdictionTraficLocal itl)
	{
		itlDao.update( itl);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#supprimer(java.lang.Long)
	 */
	public void supprimer( Long idITL)
	{
		itlDao.remove( idITL);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#lire(java.lang.Long)
	 */
	public InterdictionTraficLocal lire( Long idITL)
	{
		return itlDao.get( idITL);
	}
	public List<InterdictionTraficLocal> select(IClause clause) {
		return itlDao.select(clause);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IITLManager#lire()
	 */
	public List<InterdictionTraficLocal> lire()
	{
		return itlDao.getAll();
	}
	public void setItlDao(ITemplateDao<InterdictionTraficLocal> itlDao) {
		this.itlDao = itlDao;
	}
	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}
}
